package edu.hm.praegla;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
@EnableEurekaClient
public class EventInventoryServiceApplication {

    @Value("${spring.data.mongodb.database}")
    private String mongoInventoryDb;
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    public static void main(String[] args) {
        SpringApplication.run(EventInventoryServiceApplication.class, args);
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        MongoClient mongoClient = MongoClients.create(mongoUri);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, mongoInventoryDb);

        if (!mongoTemplate.getCollectionNames().contains("database_sequences")) {
            mongoTemplate.createCollection("database_sequences");
        }
        if (!mongoTemplate.getCollectionNames().contains("event_store")) {
            mongoTemplate.createCollection("event_store");
        }

    }
}
