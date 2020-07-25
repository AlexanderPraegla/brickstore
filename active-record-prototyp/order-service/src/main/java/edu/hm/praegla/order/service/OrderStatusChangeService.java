package edu.hm.praegla.order.service;

import edu.hm.praegla.client.account.AccountClient;
import edu.hm.praegla.client.account.dto.CreditAccountDTO;
import edu.hm.praegla.client.account.dto.DebitAccountDTO;
import edu.hm.praegla.client.inventory.InventoryClient;
import edu.hm.praegla.client.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.praegla.client.shoppingcart.dto.ShoppingCartDTO;
import edu.hm.praegla.error.InvalidOrderStatusChangeException;
import edu.hm.praegla.order.dto.CreateOrderDTO;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderItem;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.entity.ShippingAddress;
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

    protected Order saveOrder(CreateOrderDTO orderDTO) {
        log.info("Save new order for accountId={}", orderDTO.getAccountId());
        Order order = new Order();
        order.setAccountId(orderDTO.getAccountId());
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(orderDTO.getTotal());
        order.setShippingAddress(getShippingAddressFromOrderDTO(orderDTO));
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItem orderItem : getOrderItemsFromShoppingCart(orderDTO)) {
            orderItem.setOrder(order);
            OrderItem item = orderItemRepository.save(orderItem);
            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        return order;
    }

    protected void payOrder(Order order) {
        log.info("Debit {} from accountId={} for orderId={}", order.getTotal(), order.getAccountId(), order.getId());
        accountClient.debitAccount(order.getAccountId(), new DebitAccountDTO(order.getTotal()));
        order.setStatus(OrderStatus.PAYED);
        orderRepository.save(order);
    }

    protected void gatherOrderInventoryItems(Order order) {
        log.info("Gather inventory items for order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        List<UpdateInventoryItemsStockDTO.Item> items = order.getOrderItems().stream()
                .map(orderItem -> new UpdateInventoryItemsStockDTO.Item(orderItem.getInventoryItemId(), orderItem.getQuantity()))
                .collect(Collectors.toList());
        UpdateInventoryItemsStockDTO updateInventoryItemsStockDTO = new UpdateInventoryItemsStockDTO();
        updateInventoryItemsStockDTO.setItems(items);
        inventoryClient.gather(updateInventoryItemsStockDTO);
        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
    }

    protected void shipOrder(Order order) {
        log.info("Ship order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        if (order.getStatus() != OrderStatus.PROCESSED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.SHIPPED);
        }

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    protected void deliverOrder(Order order) {
        log.info("Deliver order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.DELIVERED);
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    protected void createCancellation(Order order) {
        log.info("Cancel order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    protected void refundCancellation(Order order) {
        log.info("Refund total for order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        accountClient.chargeAccount(order.getAccountId(), new CreditAccountDTO(order.getTotal()));

        order.setStatus(OrderStatus.CANCELED_AMOUNT_REFUNDED);
        orderRepository.save(order);
    }

    protected void restockCancellation(Order order) {
        log.info("Return inventory items for order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        List<UpdateInventoryItemsStockDTO.Item> items = order.getOrderItems().stream()
                .map(orderItem -> new UpdateInventoryItemsStockDTO.Item(orderItem.getInventoryItemId(), orderItem.getQuantity()))
                .collect(Collectors.toList());
        UpdateInventoryItemsStockDTO updateInventoryItemsStockDTO = new UpdateInventoryItemsStockDTO();
        updateInventoryItemsStockDTO.setItems(items);
        inventoryClient.stockUp(updateInventoryItemsStockDTO);

        order.setStatus(OrderStatus.CANCELLATION_COMPLETED);
        orderRepository.save(order);
    }

    protected void completeCancellation(Order order) {
        log.info("Complete order cancellation for orderId={} and accountId={}", order.getId(), order.getAccountId());
        order.setStatus(OrderStatus.CANCELLATION_COMPLETED);
        orderRepository.save(order);
    }

    private BigDecimal calcTotalShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        return shoppingCartDTO.getLineItems()
                .stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderItem> getOrderItemsFromShoppingCart(CreateOrderDTO orderDTO) {
        return orderDTO.getOrderItems()
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

    private ShippingAddress getShippingAddressFromOrderDTO(CreateOrderDTO orderDTO) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCustomerName(orderDTO.getShippingAddress().getCustomerName());
        shippingAddress.setCity(orderDTO.getShippingAddress().getCity());
        shippingAddress.setStreet(orderDTO.getShippingAddress().getStreet());
        shippingAddress.setPostalCode(orderDTO.getShippingAddress().getPostalCode());
        return shippingAddress;
    }
}
