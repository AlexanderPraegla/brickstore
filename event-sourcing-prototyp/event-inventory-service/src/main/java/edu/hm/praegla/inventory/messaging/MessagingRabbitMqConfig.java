package edu.hm.praegla.inventory.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingRabbitMqConfig {
    public static final String INVENTORY_EVENT_EXCHANGE = "inventory-event-exchange";
    public static final String INVENTORY_SERVICE_QUEUE = "inventory-service";
    public static final String ROUTING_KEY = "inventory.*";

    @Bean
    public Queue accountQueue() {
        return new Queue(INVENTORY_SERVICE_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(INVENTORY_EVENT_EXCHANGE);
    }

    @Bean
    public Binding bindingAccountQueue(TopicExchange exchange) {
        return BindingBuilder.bind(accountQueue()).to(exchange).with(ROUTING_KEY);
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

//    @Bean
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange(FANOUT_EXCHANGE_NAME, false, false);
//    }
//
//    @Bean
//    public Declarables fanoutBindings(FanoutExchange fanoutExchange) {
//        Queue fanoutQueue1 = new Queue(ACCOUNT_SERVICE_QUEUE, false);
//
//        return new Declarables(fanoutQueue1, fanoutExchange, BindingBuilder
//                .bind(fanoutQueue1)
//                .to(fanoutExchange));
//    }
}
