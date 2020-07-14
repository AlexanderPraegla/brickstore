package edu.hm.praegla.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerUIConfiguration {

    @Bean
    public OpenAPI sortTagsAlphabetically() {
        OpenAPI openApi = new OpenAPI();
        Info info = new Info();
        info.setTitle("Order API");
        info.setDescription("Documentation Order API v1.0");
        openApi.setInfo(info);
        Server server = new Server();
        server.setUrl("http://localhost:8080/");
        openApi.setServers(Collections.singletonList(server));
        return openApi;
    }

}
