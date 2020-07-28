package hm.edu.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile({"test"})
@EnableWebSecurity
@Configuration
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * disable all security for 'test' profile
     * @param web
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/**");
    }
}
