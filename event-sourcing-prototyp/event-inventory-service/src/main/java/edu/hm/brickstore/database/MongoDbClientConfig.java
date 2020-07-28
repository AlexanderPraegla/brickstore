package edu.hm.brickstore.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Adding transaction manager to mongoDB.
 */
@SuppressWarnings("NullableProblems")
@Configuration
public class MongoDbClientConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String mongoInventoryDb;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return mongoInventoryDb;
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }


}
