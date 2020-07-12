package edu.hm.praegla.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
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
    private static final String EVENT_EXCHANGE = "event_exchange";

    public static final String SHOPPING_CART_QUEUE = "shopping_cart_queue";
    public static final String ACCOUNT_TO_SHOPPING_CART_QUEUE = "account_to_shopping_cart_queue";
    public static final String INVENTORY_TO_SHOPPING_CART_QUEUE = "inventory_to_shopping_cart_queue";

    private static final boolean QUEUE_DURABLE = false;
    private static final boolean EXCHANGE_DURABLE = false;
    private static final boolean EXCHANGE_AUTO_DELETE = false;

    @Bean
    @Qualifier(SHOPPING_CART_QUEUE)
    public Queue shoppingCartQueue() {
        return new Queue(SHOPPING_CART_QUEUE, QUEUE_DURABLE);
    }

    @Bean
    @Qualifier(ACCOUNT_TO_SHOPPING_CART_QUEUE)
    public Queue accountQueue() {
        return new Queue(ACCOUNT_TO_SHOPPING_CART_QUEUE, QUEUE_DURABLE);
    }

    @Bean
    @Qualifier(INVENTORY_TO_SHOPPING_CART_QUEUE)
    public Queue inventoryQueue() {
        return new Queue(INVENTORY_TO_SHOPPING_CART_QUEUE, QUEUE_DURABLE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EVENT_EXCHANGE, EXCHANGE_DURABLE, EXCHANGE_AUTO_DELETE);
    }

    @Bean
    public Binding bindingShoppingCartQueue(@Qualifier(SHOPPING_CART_QUEUE) Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("shopping.cart.#");
    }

    @Bean
    public Declarables accountTopicBindings(@Qualifier(ACCOUNT_TO_SHOPPING_CART_QUEUE) Queue queue, TopicExchange exchange) {
        return new Declarables(
                BindingBuilder.bind(queue).to(exchange).with("account.created"),
                BindingBuilder.bind(queue).to(exchange).with("account.status.updated"),
                BindingBuilder.bind(queue).to(exchange).with("account.customer.updated")
        );
    }

    @Bean
    public Binding bindingInventoryQueue(@Qualifier(INVENTORY_TO_SHOPPING_CART_QUEUE) Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("inventory.#");
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
