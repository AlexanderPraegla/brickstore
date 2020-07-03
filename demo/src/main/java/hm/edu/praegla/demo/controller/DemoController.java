package hm.edu.praegla.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DemoController {

    private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping("/activeProfile")
    public String getActiveProfile() {
        return activeProfile;
    }

    @GetMapping("/customerAccess")
    @PreAuthorize("hasAuthority('customers')")
    public String customer() {
        return "Hello world from customer resource";
    }

    @GetMapping("/adminAccess")
    @PreAuthorize("hasAuthority('admins')")
    public String admin() {
        return "Hello world from admin resource";
    }

    @GetMapping("/jwt")
    public Jwt resourceAdmin(@AuthenticationPrincipal Jwt jwt) {
        LOG.trace("***** JWT Headers: {}", jwt.getHeaders());
        LOG.trace("***** JWT Claims: {}", jwt.getClaims().toString());
        LOG.trace("***** JWT Token: {}", jwt.getTokenValue());
        return jwt;
    }

}
