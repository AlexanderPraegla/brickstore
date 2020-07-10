package edu.hm.praegla.account;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableEurekaClient
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class EventAccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventAccountServiceApplication.class, args);
    }

    @Bean
    public Queue myQueue() {
        return new Queue("myQueue", false);
    }
}
