package com.systech.nexus.greeting.service;

import com.systech.nexus.greeting.domain.Greeting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

class HelloServiceTest {

    private HelloService helloService;

    @BeforeEach
    void setUp() {
        helloService = new HelloService();
    }

    @Test
    void shouldReturnHelloWorldMessage() {
        // This test should PASS - already implemented
        Greeting greeting = helloService.getHelloMessage();
        assertThat(greeting.getMessage()).isEqualTo("Hello, World!");
    }

    @Test
    void shouldReturnCustomGreeting() {
        // This test will FAIL - method doesn't exist yet
        Greeting greeting = helloService.getCustomGreeting("John");
        assertThat(greeting.getMessage()).isEqualTo("Hello, John!");
    }

    @Test
    void shouldReturnGreetingWithTime() {
        // This test will FAIL - method doesn't exist yet
        Greeting greeting = helloService.getGreetingWithTime();
        assertThat(greeting.getMessage()).contains("Hello, World!");
        assertThat(greeting.getMessage()).contains("2025");
    }
}