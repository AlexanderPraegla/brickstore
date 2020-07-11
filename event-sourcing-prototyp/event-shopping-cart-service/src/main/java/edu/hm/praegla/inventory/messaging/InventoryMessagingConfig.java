package edu.hm.praegla.inventory.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryMessagingConfig {
    public static final String INVENTORY_EVENT_EXCHANGE = "inventory-event-exchange";
    public static final String INVENTORY_SERVICE_QUEUE = "shopping-cart-service";
    public static final String INVENTORY_ROUTING_KEY = "inventory.*";

    @Bean
    public Queue inventoryQueue() {
        return new Queue(INVENTORY_SERVICE_QUEUE, false);
    }

    @Bean
    @Qualifier(INVENTORY_EVENT_EXCHANGE)
    public TopicExchange inventoryExange() {
        return new TopicExchange(INVENTORY_EVENT_EXCHANGE);
    }

    @Bean
    public Binding bindingInventoryQueue(@Qualifier(INVENTORY_EVENT_EXCHANGE) TopicExchange exchange) {
        return BindingBuilder.bind(inventoryQueue()).to(exchange).with(INVENTORY_ROUTING_KEY);
    }
}
