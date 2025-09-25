package com.vitalcajavet.msagendamentoconsultas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ms-agendamento-consultas");
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }
}