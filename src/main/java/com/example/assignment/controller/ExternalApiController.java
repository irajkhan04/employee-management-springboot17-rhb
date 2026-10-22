package com.example.assignment.controller;

import com.example.assignment.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external")
public class ExternalApiController {
    private final ExternalApiService externalApiService;

    public ExternalApiController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/google-status")
    public ResponseEntity<ExternalApiService.ExternalApiStatus> googleStatus() {
        ExternalApiService.ExternalApiStatus result = externalApiService.checkGoogle();
        return result.reachable()
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(result);
    }
}
