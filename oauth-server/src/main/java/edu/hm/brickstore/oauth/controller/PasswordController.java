package edu.hm.brickstore.oauth.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("password")
public class PasswordController {


    private final PasswordEncoder passwordEncoder;

    public PasswordController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public String generatePassword(@RequestBody String password) {
        return passwordEncoder.encode(password);
    }
}
