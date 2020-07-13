package edu.hm.praegla.apigateway.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SwaggerUIConfiguration {

    private final RouteDefinitionLocator locator;

    public SwaggerUIConfiguration(RouteDefinitionLocator locator) {
        this.locator = locator;
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        assert definitions != null;
        return definitions.stream()
                .filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
                .map(routeDefinition -> {
                    String name = routeDefinition.getId().replaceAll("-service", "");
                    return GroupedOpenApi.builder()
                            .pathsToMatch("/" + name + "/**")
                            .setGroup(name)
                            .build();
                }).collect(Collectors.toList());
    }
}
