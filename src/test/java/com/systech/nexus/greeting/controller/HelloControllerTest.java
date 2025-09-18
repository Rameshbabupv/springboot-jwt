package com.systech.nexus.greeting.controller;

import com.systech.nexus.greeting.service.HelloService;
import com.systech.nexus.greeting.domain.Greeting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;

    @Test
    void shouldReturnHelloWorldMessage() throws Exception {
        // Mock the service to return expected result
        when(helloService.getHelloMessage()).thenReturn(new Greeting("Hello, World!"));

        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, World!"));
    }

    @Test
    void shouldReturnCustomGreeting() throws Exception {
        // Mock the service to return expected result
        when(helloService.getCustomGreeting("John")).thenReturn(new Greeting("Hello, John!"));

        mockMvc.perform(get("/api/hello/custom").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, John!"));
    }

    @Test
    void shouldHandleHealthCheck() throws Exception {
        // This test will FAIL - we haven't implemented health endpoint yet
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}