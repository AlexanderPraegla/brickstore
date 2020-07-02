package hm.edu.praegla.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class DemoAdminController {

    @GetMapping
    public String home() {
        return "Hello world from admin resource";
    }

}
