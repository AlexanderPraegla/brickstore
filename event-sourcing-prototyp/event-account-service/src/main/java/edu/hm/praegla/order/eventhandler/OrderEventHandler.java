package edu.hm.praegla.order.eventhandler;

import edu.hm.praegla.account.service.AccountCommandService;
import edu.hm.praegla.messaging.config.MessagingRabbitMqConfig;
import edu.hm.praegla.order.event.OrderCanceledEvent;
import edu.hm.praegla.order.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = MessagingRabbitMqConfig.ORDER_TO_ACCOUNT_QUEUE)
public class OrderEventHandler {

    private final AccountCommandService accountCommandService;

    public OrderEventHandler(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }

    @RabbitHandler
    @SendTo
    public void process(@Payload OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);

        accountCommandService.debitAccountForOrder(event.getPayload());
    }

    @RabbitHandler
    public void process(@Payload OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent: {}", event);

    }
}
