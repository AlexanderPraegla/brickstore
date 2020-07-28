package edu.hm.brickstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EventShoppingCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventShoppingCartServiceApplication.class, args);
    }

}
