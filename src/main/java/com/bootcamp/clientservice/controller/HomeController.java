package com.bootcamp.clientservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Client Service");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("status", "Running");
        response.put("swagger-ui", "http://localhost:8080/swagger-ui.html");
        response.put("endpoints", Map.of(
            "clients", "http://localhost:8080/clientes",
            "health", "http://localhost:8080/actuator/health"
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "client-service");
        return ResponseEntity.ok(response);
    }
}
