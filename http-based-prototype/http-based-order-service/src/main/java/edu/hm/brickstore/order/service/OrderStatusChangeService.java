package edu.hm.brickstore.order.service;

import edu.hm.brickstore.client.account.AccountClient;
import edu.hm.brickstore.client.account.dto.CreditAccountDTO;
import edu.hm.brickstore.client.account.dto.DebitAccountDTO;
import edu.hm.brickstore.client.inventory.InventoryClient;
import edu.hm.brickstore.client.inventory.dto.UpdateInventoryItemsStockDTO;
import edu.hm.brickstore.error.InvalidOrderStatusChangeException;
import edu.hm.brickstore.order.dto.CreateOrderDTO;
import edu.hm.brickstore.order.entity.Order;
import edu.hm.brickstore.order.entity.OrderItem;
import edu.hm.brickstore.order.entity.OrderStatus;
import edu.hm.brickstore.order.entity.ShippingAddress;
import edu.hm.brickstore.order.repository.OrderItemRepository;
import edu.hm.brickstore.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Create new order instance with status CREATED and save it to the database
     * @param orderDTO
     * @return
     */
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

    /**
     * Debit the order total from the account. If successful, the status of the order is updated to PAYED
     * @param order
     */
    protected void payOrder(Order order) {
        log.info("Debit {} from accountId={} for orderId={}", order.getTotal(), order.getAccountId(), order.getId());
        accountClient.debitAccount(order.getAccountId(), new DebitAccountDTO(order.getTotal()));
        order.setStatus(OrderStatus.PAYED);
        orderRepository.save(order);
    }

    /**
     * Gather inventory items from the inventory. If successful, the status of the order is updated to PROCESSED
     * @param order
     */
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

    /**
     * Update the order to status SHIPPED. Only possible for orders with status PROCESSED
     * @param order
     */
    protected void shipOrder(Order order) {
        log.info("Ship order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        if (order.getStatus() != OrderStatus.PROCESSED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.SHIPPED);
        }

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    /**
     * Update the order to status DELIVERED. Only possible for orders with status SHIPPED
     * @param order
     */
    protected void deliverOrder(Order order) {
        log.info("Deliver order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.DELIVERED);
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    /**
     * Set order status to CANCELED
     * @param order
     */
    protected void createCancellation(Order order) {
        log.info("Cancel order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    /**
     * Return money from order to customer account.
     * @param order
     */
    protected void refundCancellation(Order order) {
        log.info("Refund total for order with orderId={} for accountId={}", order.getId(), order.getAccountId());
        accountClient.creditAccount(order.getAccountId(), new CreditAccountDTO(order.getTotal()));

        order.setStatus(OrderStatus.CANCELED_AMOUNT_REFUNDED);
        orderRepository.save(order);
    }

    /**
     * Return the reserved quantity of the inventory items back to the inventory.
     * @param order
     */
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

    /**
     * Update order status to CANCELLATION_COMPLETED to indicate that the cancellation process is completed.
     * @param order
     */
    protected void completeCancellation(Order order) {
        log.info("Complete order cancellation for orderId={} and accountId={}", order.getId(), order.getAccountId());
        order.setStatus(OrderStatus.CANCELLATION_COMPLETED);
        orderRepository.save(order);
    }

    /**
     * Extract list of {@link OrderItem} from the {@link CreateOrderDTO}
     * @param orderDTO
     * @return
     */
    private List<OrderItem> getOrderItemsFromShoppingCart(CreateOrderDTO orderDTO) {
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

    /**
     * Extract new instance of {@link ShippingAddress} from the {@link CreateOrderDTO}
     * @param orderDTO
     * @return
     */
    private ShippingAddress getShippingAddressFromOrderDTO(CreateOrderDTO orderDTO) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCustomerName(orderDTO.getShippingAddress().getCustomerName());
        shippingAddress.setCity(orderDTO.getShippingAddress().getCity());
        shippingAddress.setStreet(orderDTO.getShippingAddress().getStreet());
        shippingAddress.setPostalCode(orderDTO.getShippingAddress().getPostalCode());
        return shippingAddress;
    }
}
