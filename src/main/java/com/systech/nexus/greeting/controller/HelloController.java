package com.systech.nexus.greeting.controller;

import com.systech.nexus.greeting.domain.Greeting;
import com.systech.nexus.greeting.service.HelloService;
import com.systech.nexus.common.annotation.Loggable;
import com.systech.nexus.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Public endpoints (accessible without authentication)
    @GetMapping("/public/hello")
    @Loggable(description = "Get public hello world message")
    public Greeting publicHello() {
        return helloService.getHelloMessage();
    }

    @GetMapping("/public/health")
    @Loggable(logParameters = false, description = "Public health check endpoint")
    public Map<String, String> publicHealth() {
        return Map.of("status", "UP", "auth", "not required");
    }

    // User level endpoints (requires any authenticated user)
    @GetMapping("/user/hello")
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "Get authenticated user hello message")
    public Greeting userHello() {
        String username = jwtTokenUtil.getCurrentUsername();
        String message = "Hello " + (username != null ? username : "authenticated user") + "!";
        return new Greeting(message);
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "Get user profile information")
    public Map<String, String> userProfile() {
        return Map.of(
            "userId", jwtTokenUtil.getCurrentUserId() != null ? jwtTokenUtil.getCurrentUserId() : "unknown",
            "username", jwtTokenUtil.getCurrentUsername() != null ? jwtTokenUtil.getCurrentUsername() : "unknown",
            "email", jwtTokenUtil.getCurrentUserEmail() != null ? jwtTokenUtil.getCurrentUserEmail() : "unknown",
            "roles", String.join(", ", jwtTokenUtil.getCurrentUserRoles())
        );
    }

    // Manager level endpoints (requires manager or admin role)
    @GetMapping("/manager/hello")
    @PreAuthorize("hasAnyRole('nexus-manager', 'nexus-admin')")
    @Loggable(description = "Get manager level hello message")
    public Greeting managerHello() {
        return new Greeting("Hello Manager! You have elevated access.");
    }

    // Admin level endpoints (requires admin role only)
    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('nexus-admin')")
    @Loggable(description = "Get admin level hello message")
    public Greeting adminHello() {
        return new Greeting("Hello Admin! You have full access to the system.");
    }

    @GetMapping("/admin/system-status")
    @PreAuthorize("hasRole('nexus-admin')")
    @Loggable(description = "Get system status information")
    public Map<String, String> systemStatus() {
        return Map.of(
            "status", "UP",
            "auth", "admin required",
            "jwtValid", "true",
            "adminUser", jwtTokenUtil.getCurrentUsername() != null ? jwtTokenUtil.getCurrentUsername() : "unknown"
        );
    }

    // Legacy endpoints (kept for backward compatibility, now require authentication)
    @GetMapping("/hello")
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "Get hello world message (legacy endpoint)")
    public Greeting hello() {
        return helloService.getHelloMessage();
    }

    @GetMapping("/hello/custom")
    @PreAuthorize("hasAnyRole('nexus-user', 'nexus-manager', 'nexus-admin')")
    @Loggable(description = "Get custom greeting message (legacy endpoint)")
    public Greeting customHello(@RequestParam String name) {
        return helloService.getCustomGreeting(name);
    }
}