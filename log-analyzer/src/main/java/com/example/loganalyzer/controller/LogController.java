package com.example.loganalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.loganalyzer.service.LogService;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("/analyze")
    public List<Map<String, Object>> analyzeLogs(@RequestBody List<String> logs) {
        // Assuming you want to process logs using the LogProcessingService
        return logService.processLogs(logs);
    }

    @GetMapping("/analyze")
    public List<Map<String, Object>> fetchAndAnalyzeLogs(@RequestParam String indexName) {
        return logService.fetchAndAnalyzeLogs(indexName);
    }
}