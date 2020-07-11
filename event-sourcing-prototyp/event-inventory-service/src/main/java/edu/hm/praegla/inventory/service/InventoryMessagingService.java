package edu.hm.praegla.inventory.service;

import edu.hm.praegla.inventory.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class InventoryMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public InventoryMessagingService(RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendMessage(Event<?> event) {
        rabbitTemplate.convertAndSend(exchange.getName(), "inventory." + event.getEventType(), event);
    }

}