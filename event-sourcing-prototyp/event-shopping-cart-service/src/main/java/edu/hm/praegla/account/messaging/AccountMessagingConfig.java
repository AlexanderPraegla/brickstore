package edu.hm.praegla.account.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountMessagingConfig {

    public static final String ACCOUNT_EVENT_EXCHANGE = "account-event-exchange";
    public static final String ACCOUNT_SERVICE_QUEUE = "shopping-cart-service";
    public static final String ACCOUNT_ROUTING_KEY = "account.*";

    @Bean
    public Queue accountQueue() {
        return new Queue(ACCOUNT_SERVICE_QUEUE, false);
    }

    @Bean
    @Qualifier(ACCOUNT_EVENT_EXCHANGE)
    public TopicExchange accountExange() {
        return new TopicExchange(ACCOUNT_EVENT_EXCHANGE);
    }

    @Bean
    public Binding bindingAccountQueue(@Qualifier(ACCOUNT_EVENT_EXCHANGE) TopicExchange exchange) {
        return BindingBuilder.bind(accountQueue()).to(exchange).with(ACCOUNT_ROUTING_KEY);
    }
}
