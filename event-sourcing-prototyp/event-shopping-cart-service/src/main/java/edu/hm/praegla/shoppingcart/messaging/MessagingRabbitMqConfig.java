package edu.hm.praegla.shoppingcart.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingRabbitMqConfig {
    public static final String SHOPPING_CART_EVENT_EXCHANGE = "shopping-cart-event-exchange";
    public static final String SHOPPING_CART_SERVICE_QUEUE = "shopping-cart-service";
    public static final String SHOPPING_CART_ROUTING_PREFIX = "shoppingcart.";
    private static final String SHOPPING_CART_ROUTING_KEY = SHOPPING_CART_ROUTING_PREFIX + "*";

    @Bean
    public Queue shoppingCartQueue() {
        return new Queue(SHOPPING_CART_SERVICE_QUEUE, false);
    }

    @Bean
    @Qualifier(SHOPPING_CART_EVENT_EXCHANGE)
    public TopicExchange shoppingCartExange() {
        return new TopicExchange(SHOPPING_CART_EVENT_EXCHANGE);
    }

    @Bean
    public Binding bindingShoppingCartQueue(@Qualifier(SHOPPING_CART_EVENT_EXCHANGE) TopicExchange exchange) {
        return BindingBuilder.bind(shoppingCartQueue()).to(exchange).with(SHOPPING_CART_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

}
