package edu.hm.praegla.order.eventhandler;

import edu.hm.praegla.account.dto.CreditAccountDTO;
import edu.hm.praegla.account.dto.DebitAccountDTO;
import edu.hm.praegla.account.error.BrickstoreException;
import edu.hm.praegla.account.event.Event;
import edu.hm.praegla.account.service.AccountCommandService;
import edu.hm.praegla.messaging.config.MessagingRabbitMqConfig;
import edu.hm.praegla.messaging.service.MessagingService;
import edu.hm.praegla.order.dto.OrderErrorDTO;
import edu.hm.praegla.order.dto.OrderStatusUpdateDTO;
import edu.hm.praegla.order.entity.Order;
import edu.hm.praegla.order.entity.OrderStatus;
import edu.hm.praegla.order.event.OrderCanceledEvent;
import edu.hm.praegla.order.event.OrderCreatedEvent;
import edu.hm.praegla.order.event.OrderDebitAccountFailedEvent;
import edu.hm.praegla.order.event.OrderDebitAccountSucceededEvent;
import edu.hm.praegla.order.event.OrderRefundMoneyFailedEvent;
import edu.hm.praegla.order.event.OrderRefundMoneySucceededEvent;
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
    private final MessagingService messagingService;

    public OrderEventHandler(AccountCommandService accountCommandService, MessagingService messagingService) {
        this.accountCommandService = accountCommandService;
        this.messagingService = messagingService;
    }

    @RabbitHandler
    @SendTo
    public void process(@Payload OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);

        Order order = event.getPayload();
        Event<?> result;
        try {
            accountCommandService.debitMoneyFromAccount(order.getAccountId(), new DebitAccountDTO(order.getTotal()));
            OrderStatusUpdateDTO orderStatusUpdateDTO = new OrderStatusUpdateDTO(order.getId(), OrderStatus.PAYED);
            result = new OrderDebitAccountSucceededEvent(order.getId(), orderStatusUpdateDTO);
            messagingService.sendMessage(result, "order.account.debit.succeeded");
        } catch (BrickstoreException e) {
            log.error("Debit {} for orderId={} and accountId={} failed", order.getTotal(), order.getId(), order.getAccountId(), e);
            OrderErrorDTO orderErrorDTO = new OrderErrorDTO(order.getId(), e.getResponseCode());
            result = new OrderDebitAccountFailedEvent(order.getId(), orderErrorDTO);
            messagingService.sendMessage(result, "order.account.debit.failed");
        }
    }

    @RabbitHandler
    public void process(@Payload OrderCanceledEvent event) {
        log.info("Received OrderCanceledEvent: {}", event);

        Order order = event.getPayload();
        Event<?> result;
        try {
            accountCommandService.creditMoneyToAccount(order.getAccountId(), new CreditAccountDTO(order.getTotal()));
            OrderStatusUpdateDTO orderStatusUpdateDTO = new OrderStatusUpdateDTO(order.getId(), OrderStatus.PAYED);
            result = new OrderRefundMoneySucceededEvent(order.getId(), orderStatusUpdateDTO);
            messagingService.sendMessage(result, "order.refund.money.succeeded");
        } catch (BrickstoreException e) {
            log.error("Refund {} for orderId={} and accountId={} failed", order.getTotal(), order.getId(), order.getAccountId(), e);
            OrderErrorDTO orderErrorDTO = new OrderErrorDTO(order.getId(), e.getResponseCode());
            result = new OrderRefundMoneyFailedEvent(order.getId(), orderErrorDTO);
            messagingService.sendMessage(result, "order.refund.money.failed");
        }
    }
}
