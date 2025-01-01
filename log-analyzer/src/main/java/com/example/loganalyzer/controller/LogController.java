package com.example.loganalyzer.controller;

import com.example.loganalyzer.model.Log;
import com.example.loganalyzer.model.Threat;
import com.example.loganalyzer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping
    public Log addLog(@RequestBody Log log) {
        return logService.saveLog(log);
    }

    @GetMapping("/threats")
    public Iterable<Threat> getAllThreats() {
        return logService.getAllThreats();
    }
}
