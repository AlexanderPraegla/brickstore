package edu.hm.praegla.order.service;

import edu.hm.praegla.client.account.AccountClient;
import edu.hm.praegla.client.account.dto.AccountDTO;
import edu.hm.praegla.client.account.dto.ModifyAccountBalanceDTO;
import edu.hm.praegla.client.inventory.InventoryClient;
import edu.hm.praegla.client.inventory.dto.ChangeInventoryItemStockDTO;
import edu.hm.praegla.client.shoppingcart.dto.ShoppingCartDTO;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderItem;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.entity.ShippingAddress;
import edu.hm.praegla.order.error.InvalidOrderStatusChangeException;
import edu.hm.praegla.order.repository.OrderItemRepository;
import edu.hm.praegla.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OrderStatusChangeService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryClient inventoryClient;
    private final AccountClient accountClient;

    public OrderStatusChangeService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, InventoryClient inventoryClient, AccountClient accountClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryClient = inventoryClient;
        this.accountClient = accountClient;
    }

    protected Order saveOrder(AccountDTO account, ShoppingCartDTO shoppingCartDTO) {
        Order order = new Order();
        order.setAccountId(account.getId());
        order.setStatus(OrderStatus.CREATED);
        BigDecimal total = calcTotalShoppingCart(shoppingCartDTO);
        order.setTotal(total);
        order.setShippingAddress(getShippingAddressFromAccount(account));
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItem orderItem : getOrderItemsFromShoppingCart(shoppingCartDTO)) {
            orderItem.setOrder(order);
            OrderItem item = orderItemRepository.save(orderItem);
            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        return order;
    }

    protected void payOrder(Order order) {
        accountClient.debitAccount(order.getAccountId(), new ModifyAccountBalanceDTO(order.getTotal()));
        order.setStatus(OrderStatus.PAYED);
        orderRepository.save(order);
    }

    protected void gatherOrderInventoryItems(Order order) {
        List<ChangeInventoryItemStockDTO> items = order.getOrderItems().stream()
                .map(orderItem -> new ChangeInventoryItemStockDTO(orderItem.getInventoryItemId(), orderItem.getQuantity()))
                .collect(Collectors.toList());
        inventoryClient.gather(items);
        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
    }

    protected void shipOrder(Order order) {
        if (order.getStatus() != OrderStatus.PROCESSED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.SHIPPED);
        }

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    protected void deliverOrder(Order order) {
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.DELIVERED);
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    protected void createCancellation(Order order) {
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    protected void refundCancellation(Order order) {
        accountClient.chargeAccount(order.getAccountId(), new ModifyAccountBalanceDTO(order.getTotal()));

        order.setStatus(OrderStatus.CANCELED_AMOUNT_REFUNDED);
        orderRepository.save(order);
    }

    protected void restockCancellation(Order order) {
        List<ChangeInventoryItemStockDTO> items = order.getOrderItems().stream()
                .map(orderItem -> new ChangeInventoryItemStockDTO(orderItem.getInventoryItemId(), orderItem.getQuantity()))
                .collect(Collectors.toList());
        inventoryClient.stockUp(items);

        order.setStatus(OrderStatus.CANCELED_STOCK_RESTORED);
        orderRepository.save(order);
    }

    protected void completeCancellation(Order order) {
        order.setStatus(OrderStatus.CANCELLATION_COMPLETED);
        orderRepository.save(order);
    }

    private BigDecimal calcTotalShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        return shoppingCartDTO.getLineItems()
                .stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderItem> getOrderItemsFromShoppingCart(ShoppingCartDTO shoppingCart) {
        return shoppingCart.getLineItems()
                .stream()
                .map(lineItemDTO -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setInventoryItemId(lineItemDTO.getInventoryItemId());
                    orderItem.setName(lineItemDTO.getName());
                    orderItem.setPrice(lineItemDTO.getPrice());
                    orderItem.setQuantity(lineItemDTO.getQuantity());
                    orderItem.setDeliveryTime(lineItemDTO.getDeliveryTime());
                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    private ShippingAddress getShippingAddressFromAccount(AccountDTO accountDTO) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCustomerName(accountDTO.getCustomer().getFirstname() + " " + accountDTO.getCustomer().getLastname());
        shippingAddress.setCity(accountDTO.getAddress().getCity());
        shippingAddress.setStreet(accountDTO.getAddress().getStreet());
        shippingAddress.setPostalCode(accountDTO.getAddress().getPostalCode());
        return shippingAddress;
    }
}
