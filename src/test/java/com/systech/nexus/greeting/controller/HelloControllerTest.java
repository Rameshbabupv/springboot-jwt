package com.systech.nexus.greeting.controller;

import com.systech.nexus.config.TestSecurityConfig;
import com.systech.nexus.greeting.service.HelloService;
import com.systech.nexus.greeting.domain.Greeting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
@Import(TestSecurityConfig.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;

    @Test
    void shouldReturnPublicHelloMessage() throws Exception {
        // Mock the service to return expected result
        when(helloService.getHelloMessage()).thenReturn(new Greeting("Hello, World!"));

        mockMvc.perform(get("/api/public/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, World!"));
    }

    @Test
    void shouldReturnPublicHealthCheck() throws Exception {
        mockMvc.perform(get("/api/public/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.auth").value("not required"));
    }

    @Test
    void shouldAllowAllEndpointsInTestEnvironment() throws Exception {
        // In test environment with TestSecurityConfig, all endpoints should be accessible
        // This tests that the test configuration is working properly
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/hello"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/hello"))
                .andExpect(status().isOk());
    }
}