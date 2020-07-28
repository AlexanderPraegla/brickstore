package edu.hm.brickstore.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * this endpoint is used to authorize an user in the resource servers and get the user information
     * @param principal
     * @return
     */
    @GetMapping("/me")
    public Principal userInfo(Principal principal) {
        return principal;
    }
}
