package edu.hm.brickstore.order.service;

import edu.hm.brickstore.error.InvalidOrderStatusChangeException;
import edu.hm.brickstore.error.NoItemsInShoppingCartException;
import edu.hm.brickstore.error.OrderNotCancelableException;
import edu.hm.brickstore.messaging.service.MessagingService;
import edu.hm.brickstore.order.dto.OrderStatusUpdateDTO;
import edu.hm.brickstore.order.entity.Order;
import edu.hm.brickstore.order.entity.OrderItem;
import edu.hm.brickstore.order.entity.OrderStatus;
import edu.hm.brickstore.order.event.OrderCanceledEvent;
import edu.hm.brickstore.order.event.OrderCreatedEvent;
import edu.hm.brickstore.order.event.OrderPayedEvent;
import edu.hm.brickstore.order.event.OrderRefundedEvent;
import edu.hm.brickstore.order.event.OrderStatusUpdatedEvent;
import edu.hm.brickstore.order.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderCommandService {

    private final OrderQueryService orderQueryService;
    private final EventRepository eventRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final MessagingService messagingService;

    public OrderCommandService(OrderQueryService orderQueryService, EventRepository eventRepository,
                               SequenceGeneratorService sequenceGeneratorService, MessagingService messagingService) {
        this.eventRepository = eventRepository;
        this.orderQueryService = orderQueryService;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.messagingService = messagingService;
    }

    /**
     * Trigger the workflow to create an order
     * @param order
     * @return
     */
    public Order createOrder(Order order) {
        log.info("Create new order for accountId={}", order.getAccountId());
        long orderId = sequenceGeneratorService.generateSequence(Order.SEQUENCE_NAME);
        order.setId(orderId);

        if (order.getOrderItems().size() == 0) {
            throw new NoItemsInShoppingCartException();
        }

        List<OrderItem> orderItems = order.getOrderItems()
                .stream()
                .peek(orderItem -> orderItem.setId(sequenceGeneratorService.generateSequence(OrderItem.SEQUENCE_NAME)))
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, order);
        eventRepository.save(event);
        messagingService.sendMessage(event, "order.created");
        return order;
    }

    /**
     * Update the status of an order manually
     * @param statusUpdateDTO
     */
    public void updateStatus(OrderStatusUpdateDTO statusUpdateDTO) {
        log.info("Update status for orderId={} to {}", statusUpdateDTO.getOrderId(), statusUpdateDTO.getStatus());
        OrderStatus newStatus = statusUpdateDTO.getStatus();
        Order order = orderQueryService.getOrder(statusUpdateDTO.getOrderId());
        order.setErrorCode(null);

        switch (newStatus) {
            case PAYED:
                payOrder(order);
                break;
            case PROCESSED:
                gatherOrderInventoryItems(order);
                break;
            case SHIPPED:
                shipOrder(order, statusUpdateDTO);
                break;
            case DELIVERED:
                deliverOrder(order, statusUpdateDTO);
                break;
            default:
                throw new InvalidOrderStatusChangeException(order.getStatus(), newStatus);
        }

        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(statusUpdateDTO.getOrderId(), statusUpdateDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "order.status.updated");
    }

    /**
     * Trigger the workflow to cancel an order
     * @param orderId
     */
    public void cancelOrder(long orderId) {
        log.info("Cancel order with orderId={}", orderId);
        Order order = orderQueryService.getOrder(orderId);

        OrderStatus status = order.getStatus();
        if (status.isOneOf(OrderStatus.SHIPPED, OrderStatus.DELIVERED)) {
            throw new OrderNotCancelableException();
        }

        order.setErrorCode(null);
        OrderCanceledEvent event = new OrderCanceledEvent(orderId, order);
        eventRepository.save(event);

        switch (status) {
            case CREATED:
                messagingService.sendMessage(event, "order.cancel.created");
                break;
            case PAYED:
            case CANCELED:
                messagingService.sendMessage(event, "cancel.order.retry");
                break;
            case PROCESSED:
                messagingService.sendMessage(event, "order.canceled");
                break;
            case CANCELED_AMOUNT_REFUNDED:
                //Trigger return of items to inventory service
                OrderRefundedEvent refundedEvent = new OrderRefundedEvent(order.getId(), order);
                messagingService.sendMessage(refundedEvent, "refunded.order.retry");
                break;
            case CANCELLATION_COMPLETED:
                break;
        }

    }

    private void payOrder(Order order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.PAYED);
        }

        //Trigger order creation workflow
        OrderCreatedEvent event = new OrderCreatedEvent(order.getId(), order);
        messagingService.sendMessage(event, "create.order.retry");
    }

    private void gatherOrderInventoryItems(Order order) {
        if (order.getStatus() != OrderStatus.PAYED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.PROCESSED);
        }

        //Trigger gathering of items in inventory service
        OrderPayedEvent debitedEvent = new OrderPayedEvent(order.getId(), order);
        messagingService.sendMessage(debitedEvent, "payed.order.retry");
    }

    private void shipOrder(Order order, OrderStatusUpdateDTO statusUpdateDTO) {
        if (order.getStatus() != OrderStatus.PROCESSED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.SHIPPED);
        }

        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(order.getId(), statusUpdateDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "order.status.updated");
    }

    private void deliverOrder(Order order, OrderStatusUpdateDTO statusUpdateDTO) {
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusChangeException(order.getStatus(), OrderStatus.DELIVERED);
        }

        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(order.getId(), statusUpdateDTO);
        eventRepository.save(event);
        messagingService.sendMessage(event, "order.status.updated");
    }

}
