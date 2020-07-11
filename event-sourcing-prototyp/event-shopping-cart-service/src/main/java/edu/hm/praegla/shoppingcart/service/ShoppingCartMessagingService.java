package edu.hm.praegla.shoppingcart.service;

import edu.hm.praegla.shoppingcart.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static edu.hm.praegla.shoppingcart.messaging.MessagingRabbitMqConfig.SHOPPING_CART_EVENT_EXCHANGE;
import static edu.hm.praegla.shoppingcart.messaging.MessagingRabbitMqConfig.SHOPPING_CART_ROUTING_PREFIX;

@Slf4j
@Service
@Transactional
public class ShoppingCartMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public ShoppingCartMessagingService(RabbitTemplate rabbitTemplate,
                                        @Qualifier(SHOPPING_CART_EVENT_EXCHANGE) Exchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendMessage(Event<?> event) {
        rabbitTemplate.convertAndSend(exchange.getName(), SHOPPING_CART_ROUTING_PREFIX + event.getEventType(), event);
    }
}
