package edu.hm.praegla.client;

import edu.hm.praegla.client.error.ClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ClientErrorDecoder();
    }
}
