package edu.hm.praegla.order.service;

import edu.hm.praegla.client.account.AccountClient;
import edu.hm.praegla.client.account.dto.AccountDTO;
import edu.hm.praegla.client.shoppingcart.ShoppingCartClient;
import edu.hm.praegla.client.shoppingcart.dto.ShoppingCartDTO;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.error.EntityNotFoundException;
import edu.hm.praegla.order.error.InvalidOrderStatusChangeException;
import edu.hm.praegla.order.error.NoItemsInShoppingCartException;
import edu.hm.praegla.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusChangeService orderStatusChangeService;
    private final AccountClient accountClient;
    private final ShoppingCartClient shoppingCartClient;

    public OrderService(OrderRepository orderRepository, OrderStatusChangeService orderStatusChangeService, AccountClient accountClient, ShoppingCartClient shoppingCartClient) {
        this.orderRepository = orderRepository;
        this.orderStatusChangeService = orderStatusChangeService;
        this.shoppingCartClient = shoppingCartClient;
        this.accountClient = accountClient;
    }

    @Transactional
    public List<Order> getOpenOrders() {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> order.getStatus() != OrderStatus.SHIPPED || order.getStatus() != OrderStatus.DELIVERED)
                .collect(Collectors.toList());
    }

    @Transactional
    public Order getOrder(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException(Order.class, "id", orderId));
    }

    public Order createOrder(long accountId) {
        ShoppingCartDTO shoppingCart = shoppingCartClient.getShoppingCart(accountId);

        if (shoppingCart.getLineItems().size() == 0) {
            throw new NoItemsInShoppingCartException();
        }

        AccountDTO account = accountClient.getAccount(accountId);

        Order order = orderStatusChangeService.saveOrder(account, shoppingCart);

        orderStatusChangeService.payOrder(order);

        orderStatusChangeService.gatherOrderInventoryItems(order);

        return order;
    }

    public void updateStatus(long orderId, OrderStatus status) {
        Order order = getOrder(orderId);
        switch (status) {
            case PAYED:
                orderStatusChangeService.payOrder(order);
                break;
            case PROCESSED:
                orderStatusChangeService.gatherOrderInventoryItems(order);
                break;
            case SHIPPED:
                orderStatusChangeService.shipOrder(order);
                break;
            case DELIVERED:
                orderStatusChangeService.deliverOrder(order);
                break;
            default:
                throw new InvalidOrderStatusChangeException(order.getStatus(), status);
        }
    }

    public void cancelOrder(long orderId) {
        Order order = getOrder(orderId);
        OrderStatus status = order.getStatus();

        orderStatusChangeService.createCancellation(order);
        switch (status) {
            case CREATED:
                break;
            case PAYED:
            case CANCELED:
                orderStatusChangeService.refundCancellation(order);
                break;
            case PROCESSED:
            case CANCELED_REFUNDED:
                orderStatusChangeService.refundCancellation(order);
                orderStatusChangeService.restockCancellation(order);
                break;
        }
        orderStatusChangeService.completeCancellation(order);
    }

}
