package hm.edu.praegla.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class DemoUserController {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping
    public String home() {
        return "Hello world from user resource";
    }

    @GetMapping("/activeProfile")
    public String getActiveProfile() {
        return activeProfile;
    }

}
