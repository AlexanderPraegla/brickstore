package edu.hm.praegla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EventOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventOrderServiceApplication.class, args);
    }

}
