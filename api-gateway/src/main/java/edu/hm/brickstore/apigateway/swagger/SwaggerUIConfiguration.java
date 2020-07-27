package edu.hm.brickstore.apigateway.swagger;

import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerUIConfiguration {

    private final RouteDefinitionLocator locator;

//    private String authUrl = "http://localhost:8080/oauth/";

    public SwaggerUIConfiguration(RouteDefinitionLocator locator) {
        this.locator = locator;
    }


//    @Bean
//    public List<GroupedOpenApi> apis() {
//        List<GroupedOpenApi> groups = new ArrayList<>();
//        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
//        definitions.stream().filter(routeDefinition -> routeDefinition.getId().matches(".*-service")).forEach(routeDefinition -> {
//            String name = routeDefinition.getId().replaceAll("-service", "");
//            GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").setGroup(name).build();
//        });
//        return groups;
//    }

}
