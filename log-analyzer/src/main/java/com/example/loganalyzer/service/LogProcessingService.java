package com.example.loganalyzer.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.Map;

@Service
public class LogProcessingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> processLogs(List<String> logs) {
        String url = "http://localhost:5000/process_logs";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare the request payload
        Map<String, Object> payload = Map.of("logs", logs);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // Send the POST request
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url, HttpMethod.POST, request, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        // Return the response body
        return response.getBody();
    }
}