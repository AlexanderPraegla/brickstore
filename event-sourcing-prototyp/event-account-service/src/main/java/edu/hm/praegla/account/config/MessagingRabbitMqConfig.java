package edu.hm.praegla.account.config;

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
    public static final String EXCHANGE_NAME = "eventExchange";
    public static final String ACCOUNT_SERVICE_QUEUE = "account-queue";
    public static final String ROUTING_KEY = "account.*";

    @Bean
    public Queue accountQueue() {
        return new Queue(ACCOUNT_SERVICE_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
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
//
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
