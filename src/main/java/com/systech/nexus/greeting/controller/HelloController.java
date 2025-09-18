package com.systech.nexus.greeting.controller;

import com.systech.nexus.greeting.domain.Greeting;
import com.systech.nexus.greeting.service.HelloService;
import com.systech.nexus.common.annotation.Loggable;
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
    @Loggable(description = "Get hello world message")
    public Greeting hello() {
        return helloService.getHelloMessage();
    }

    // TDD GREEN: Implemented to make tests pass
    @GetMapping("/hello/custom")
    @Loggable(description = "Get custom greeting message")
    public Greeting customHello(@RequestParam String name) {
        return helloService.getCustomGreeting(name);
    }

    @GetMapping("/health")
    @Loggable(logParameters = false, description = "Health check endpoint")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}