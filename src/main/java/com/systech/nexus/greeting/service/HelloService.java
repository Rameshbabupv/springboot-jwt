package com.systech.nexus.greeting.service;

import com.systech.nexus.greeting.domain.Greeting;
import com.systech.nexus.common.annotation.Loggable;
import org.springframework.stereotype.Service;

@Service
@Loggable(description = "Greeting Service Operations")
public class HelloService {

    public Greeting getHelloMessage() {
        return new Greeting("Hello, World!");
    }

    // TDD GREEN: Implemented to make tests pass
    public Greeting getCustomGreeting(String name) {
        return new Greeting("Hello, " + name + "!");
    }

    public Greeting getGreetingWithTime() {
        return new Greeting("Hello, World! " + java.time.LocalDate.now());
    }
}