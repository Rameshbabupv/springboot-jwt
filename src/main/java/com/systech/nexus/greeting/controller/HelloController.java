package com.systech.nexus.greeting.controller;

import com.systech.nexus.greeting.domain.Greeting;
import com.systech.nexus.greeting.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/hello")
    public Greeting hello() {
        return helloService.getHelloMessage();
    }

    // TDD GREEN: Implemented to make tests pass
    @GetMapping("/hello/custom")
    public Greeting customHello(@RequestParam String name) {
        return helloService.getCustomGreeting(name);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}