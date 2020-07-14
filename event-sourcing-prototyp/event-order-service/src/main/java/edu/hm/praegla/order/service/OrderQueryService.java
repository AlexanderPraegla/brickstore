package edu.hm.praegla.order.service;

import edu.hm.praegla.error.EntityNotFoundException;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public List<Order> getOpenOrders() {
        log.info("Get all open order");
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != OrderStatus.SHIPPED || order.getStatus() != OrderStatus.DELIVERED)
                .collect(Collectors.toList());
    }

    @Transactional
    public Order getOrder(long orderId) {
        log.info("Get order with orderId={}", orderId);
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException(Order.class, "id", orderId));
    }

    @Transactional
    public Iterable<Order> getOrdersForAccount(long accountId) {
        log.info("Get all orders for accountId={}", accountId);
        return orderRepository.findAllByAccountId(accountId);
    }
}
