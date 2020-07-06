package edu.hm.praegla.apigateway.security;

import org.springframework.context.annotation.Profile;

@Profile("!demo")
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
public class SecurityConfiguration {

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        // @formatter:off
//        http
//                .authorizeExchange()
//                .anyExchange().authenticated()
//                .and()
//                .oauth2Login();
//        return http.build();
//        // @formatter:on
//    }
}
