package hm.edu.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${spring.security.oauth2.clientId}")
    private String clientId;
    @Value("${spring.security.oauth2.resourceId}")
    private String resourceId;
    @Value("${spring.security.oauth2.userInfoEndpointUrl}")
    private String userInfoEndpointUrl;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/activeProfile")
                .permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();

        // process CORS annotations
        http.cors();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(resourceId)
                .authenticationManager(authenticationManagerBean());
    }

    @Bean
    public ResourceServerTokenServices tokenService() {
        return new UserInfoTokenServices(userInfoEndpointUrl, clientId);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() {
        OAuth2AuthenticationManager authenticationManager = new OAuth2AuthenticationManager();
        authenticationManager.setTokenServices(tokenService());
        return authenticationManager;
    }

}
