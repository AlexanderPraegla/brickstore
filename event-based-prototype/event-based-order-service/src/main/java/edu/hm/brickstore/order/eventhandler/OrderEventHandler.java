package edu.hm.brickstore.order.eventhandler;

import edu.hm.brickstore.error.ResponseCode;
import edu.hm.brickstore.messaging.service.MessagingService;
import edu.hm.brickstore.order.dto.OrderStatusUpdateDTO;
import edu.hm.brickstore.order.entity.Order;
import edu.hm.brickstore.order.entity.OrderStatus;
import edu.hm.brickstore.order.event.OrderCanceledEvent;
import edu.hm.brickstore.order.event.OrderCreatedEvent;
import edu.hm.brickstore.order.event.OrderDebitAccountFailedEvent;
import edu.hm.brickstore.order.event.OrderDebitAccountSucceededEvent;
import edu.hm.brickstore.order.event.OrderGatherInventoryItemFailedEvent;
import edu.hm.brickstore.order.event.OrderGatherInventoryItemSucceededEvent;
import edu.hm.brickstore.order.event.OrderPayedEvent;
import edu.hm.brickstore.order.event.OrderRefundMoneyFailedEvent;
import edu.hm.brickstore.order.event.OrderRefundMoneySucceededEvent;
import edu.hm.brickstore.order.event.OrderRefundedEvent;
import edu.hm.brickstore.order.event.OrderReturnInventoryItemFailedEvent;
import edu.hm.brickstore.order.event.OrderReturnInventoryItemSucceededEvent;
import edu.hm.brickstore.order.event.OrderStatusUpdatedEvent;
import edu.hm.brickstore.order.repository.EventRepository;
import edu.hm.brickstore.order.repository.OrderRepository;
import edu.hm.brickstore.order.service.OrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static edu.hm.brickstore.messaging.config.MessagingRabbitMqConfig.ORDER_QUEUE;

/**
 * Event handler for all order events
 */
@Slf4j
@Component
@RabbitListener(queues = ORDER_QUEUE)
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;
    private final OrderQueryService orderQueryService;
    private final MessagingService messagingService;

    public OrderEventHandler(OrderRepository orderRepository, EventRepository eventRepository, OrderQueryService orderQueryService, MessagingService messagingService) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.orderQueryService = orderQueryService;
        this.messagingService = messagingService;
    }

    @RabbitHandler
    public void process(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);

        Order order = event.getPayload();
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(@Payload OrderDebitAccountSucceededEvent event) {
        log.info("Received OrderDebitAccountSucceededEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        order.setStatus(OrderStatus.PAYED);
        orderRepository.save(order);

        //Trigger gathering of items in inventory service
        OrderPayedEvent debitedEvent = new OrderPayedEvent(order.getId(), order);
        messagingService.sendMessage(debitedEvent, "payed.order");
    }

    @RabbitHandler
    public void process(@Payload OrderDebitAccountFailedEvent event) {
        log.info("Received OrderDebitAccountFailedEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        ResponseCode errorCode = event.getPayload().getErrorCode();
        order.setErrorCode(errorCode);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(@Payload OrderGatherInventoryItemSucceededEvent event) {
        log.info("Received OrderGatherInventoryItemSucceededEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(@Payload OrderGatherInventoryItemFailedEvent event) {
        log.info("Received OrderGatherInventoryItemFailedEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        ResponseCode errorCode = event.getPayload().getErrorCode();
        order.setErrorCode(errorCode);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(OrderStatusUpdatedEvent event) {
        log.info("Received OrderStatusUpdatedEvent: {}", event);
        OrderStatusUpdateDTO payload = event.getPayload();
        Order order = orderQueryService.getOrder(payload.getOrderId());
        order.setStatus(payload.getStatus());
        order.setErrorCode(null);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent: {}", event);
        Order order = event.getPayload();

        if (order.getStatus() == OrderStatus.CREATED) {
            order.setStatus(OrderStatus.CANCELLATION_COMPLETED);
        } else {
            order.setStatus(OrderStatus.CANCELED);
        }
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(@Payload OrderRefundMoneySucceededEvent event) {
        log.info("Received OrderRefundMoneySucceededEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        order.setStatus(OrderStatus.CANCELED_AMOUNT_REFUNDED);
        orderRepository.save(order);

        //Trigger return of items to inventory service
        OrderRefundedEvent refundedEvent = new OrderRefundedEvent(order.getId(), order);
        messagingService.sendMessage(refundedEvent, "refunded.order");
    }

    @RabbitHandler
    public void process(@Payload OrderRefundMoneyFailedEvent event) {
        log.info("Received OrderRefundMoneyFailedEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        ResponseCode errorCode = event.getPayload().getErrorCode();
        order.setErrorCode(errorCode);
        orderRepository.save(order);

    }

    @RabbitHandler
    public void process(@Payload OrderReturnInventoryItemSucceededEvent event) {
        log.info("Received OrderReturnInventoryItemSucceededEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        order.setStatus(OrderStatus.CANCELLATION_COMPLETED);
        orderRepository.save(order);

    }

    @RabbitHandler
    public void process(@Payload OrderReturnInventoryItemFailedEvent event) {
        log.info("Received OrderReturnInventoryItemFailedEvent: {}", event);
        eventRepository.save(event);

        Order order = orderQueryService.getOrder(event.getAggregateId());
        ResponseCode errorCode = event.getPayload().getErrorCode();
        order.setErrorCode(errorCode);
        orderRepository.save(order);
    }
}
