package edu.hm.brickstore.order.service;

import edu.hm.brickstore.client.error.FeignBadRequestException;
import edu.hm.brickstore.error.EntityNotFoundException;
import edu.hm.brickstore.error.InvalidOrderStatusChangeException;
import edu.hm.brickstore.error.NoItemsInShoppingCartException;
import edu.hm.brickstore.error.OrderNotCancelableException;
import edu.hm.brickstore.order.dto.CreateOrderDTO;
import edu.hm.brickstore.order.entity.Order;
import edu.hm.brickstore.order.entity.OrderStatus;
import edu.hm.brickstore.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusChangeService orderStatusChangeService;

    public OrderService(OrderRepository orderRepository, OrderStatusChangeService orderStatusChangeService) {
        this.orderRepository = orderRepository;
        this.orderStatusChangeService = orderStatusChangeService;
    }

    public List<Order> getOpenOrders() {
        log.info("Get all open order");
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .filter(order -> order.getStatus() != OrderStatus.SHIPPED || order.getStatus() != OrderStatus.DELIVERED)
                .collect(Collectors.toList());
    }

    public Order getOrder(long orderId) {
        log.info("Get order with orderId={}", orderId);
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException(Order.class, "id", orderId));
    }

    public Iterable<Order> getOrdersForAccount(long accountId) {
        log.info("Get all orders for accountId={}", accountId);
        return orderRepository.findAllByAccountId(accountId, Sort.by(Sort.Direction.DESC, "createdOn"));
    }

    /**
     * Execute the workflow to create an order
     * @param orderDTO
     * @return
     */
    public Order createOrder(CreateOrderDTO orderDTO) {
        log.info("Create new order for accountId={}", orderDTO.getAccountId());

        if (orderDTO.getOrderItems().size() == 0) {
            throw new NoItemsInShoppingCartException();
        }

        Order order = orderStatusChangeService.saveOrder(orderDTO);

        try {
            orderStatusChangeService.payOrder(order);
        } catch (FeignBadRequestException e) {
            log.error(e.getLocalizedMessage(), e);
            order.setErrorCode(e.getResponseCode());
            orderRepository.save(order);
            return order;
        }

        try {
            orderStatusChangeService.gatherOrderInventoryItems(order);
        } catch (FeignBadRequestException e) {
            order.setErrorCode(e.getResponseCode());
            orderRepository.save(order);
            return order;
        }

        return order;
    }

    /**
     * Update the status of an order manually
     * @param orderId
     * @param status
     */
    public void updateStatus(long orderId, OrderStatus status) {
        log.info("Update status for orderId={} to {}", orderId, status);
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

    /**
     * Execute the workflow to cancel an order
     * @param orderId
     */
    public void cancelOrder(long orderId) {
        log.info("Cancel order with orderId={}", orderId);
        Order order = getOrder(orderId);
        OrderStatus status = order.getStatus();

        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new OrderNotCancelableException();
        }
        order.setErrorCode(null);

        switch (status) {
            case CREATED:
                orderStatusChangeService.completeCancellation(order);
                break;
            case PAYED:
                orderStatusChangeService.createCancellation(order);
                orderStatusChangeService.refundCancellation(order);
                orderStatusChangeService.restockCancellation(order);
                break;
            case PROCESSED:
                orderStatusChangeService.createCancellation(order);
            case CANCELED:
                orderStatusChangeService.refundCancellation(order);
                orderStatusChangeService.restockCancellation(order);
                break;
            case CANCELED_AMOUNT_REFUNDED:
                orderStatusChangeService.restockCancellation(order);
                break;
            case CANCELLATION_COMPLETED:
                break;
        }
    }

}
