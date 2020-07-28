package edu.hm.brickstore.messaging.config;

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
    public static final String EVENT_EXCHANGE = "event_exchange";
    public static final String ACCOUNT_QUEUE = "account_queue";
    public static final String ORDER_TO_ACCOUNT_QUEUE = "order_to_account_queue";

    private static final boolean QUEUE_DURABLE = false;
    private static final boolean EXCHANGE_DURABLE = false;
    private static final boolean EXCHANGE_AUTO_DELETE = false;

    @Bean
    @Qualifier(ORDER_TO_ACCOUNT_QUEUE)
    public Queue orderQueue() {
        return new Queue(ORDER_TO_ACCOUNT_QUEUE, QUEUE_DURABLE);
    }

    @Bean
    @Qualifier(ACCOUNT_QUEUE)
    public Queue accountQueue() {
        return new Queue(ACCOUNT_QUEUE, QUEUE_DURABLE);
    }

    @Bean
    public TopicExchange accountExchange() {
        return new TopicExchange(EVENT_EXCHANGE, EXCHANGE_DURABLE, EXCHANGE_AUTO_DELETE);
    }

    @Bean
    public Binding bindingAccountQueue(@Qualifier(ACCOUNT_QUEUE) Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("account.#");
    }

    @Bean
    public Declarables orderTopicBindings(@Qualifier(ORDER_TO_ACCOUNT_QUEUE) Queue queue, TopicExchange exchange) {
        return new Declarables(
                BindingBuilder.bind(queue).to(exchange).with("order.canceled"),
                BindingBuilder.bind(queue).to(exchange).with("cancel.order.retry"),
                BindingBuilder.bind(queue).to(exchange).with("create.order.retry"),
                BindingBuilder.bind(queue).to(exchange).with("order.created")
        );
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
