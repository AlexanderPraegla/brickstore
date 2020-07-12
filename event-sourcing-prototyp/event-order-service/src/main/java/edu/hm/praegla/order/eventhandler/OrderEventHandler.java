package edu.hm.praegla.order.eventhandler;

import edu.hm.praegla.messaging.service.MessagingService;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.error.ResponseCode;
import edu.hm.praegla.order.event.OrderCanceledEvent;
import edu.hm.praegla.order.event.OrderCreatedEvent;
import edu.hm.praegla.order.event.OrderDebitAccountFailedEvent;
import edu.hm.praegla.order.event.OrderDebitAccountSucceededEvent;
import edu.hm.praegla.order.event.OrderGatherInventoryItemFailedEvent;
import edu.hm.praegla.order.event.OrderGatherInventoryItemSucceededEvent;
import edu.hm.praegla.order.event.OrderPayedEvent;
import edu.hm.praegla.order.event.OrderStatusUpdatedEvent;
import edu.hm.praegla.order.repository.OrderRepository;
import edu.hm.praegla.order.service.OrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static edu.hm.praegla.messaging.config.MessagingRabbitMqConfig.ORDER_QUEUE;


@Slf4j
@Component
@RabbitListener(queues = ORDER_QUEUE)
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;
    private final MessagingService messagingService;

    public OrderEventHandler(OrderRepository orderRepository, OrderQueryService orderQueryService, MessagingService messagingService) {
        this.orderRepository = orderRepository;
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
        Order order = orderQueryService.getOrder(event.getAggregateId());
        ResponseCode errorCode = event.getPayload().getErrorCode();
        order.setErrorCode(errorCode);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(@Payload OrderGatherInventoryItemSucceededEvent event) {
        log.info("Received OrderGatherInventoryItemSucceededEvent: {}", event);
        Order order = orderQueryService.getOrder(event.getAggregateId());
        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(@Payload OrderGatherInventoryItemFailedEvent event) {
        log.info("Received OrderGatherInventoryItemFailedEvent: {}", event);
        Order order = orderQueryService.getOrder(event.getAggregateId());
        ResponseCode errorCode = event.getPayload().getErrorCode();
        order.setStatus(OrderStatus.PAYED);
        order.setErrorCode(errorCode);
        orderRepository.save(order);
    }

    @RabbitHandler
    public void process(OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent: {}", event);
    }

    @RabbitHandler
    public void process(OrderStatusUpdatedEvent event) {
        log.info("Received OrderStatusUpdatedEvent: {}", event);
        throw new NotImplementedException();
    }
}
