package edu.hm.praegla.account.service;

import edu.hm.praegla.account.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AccountMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public AccountMessagingService(RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendMessage(Event<?> event) {
        rabbitTemplate.convertAndSend(exchange.getName(), "account." + event.getEventType(), event);
    }
}
