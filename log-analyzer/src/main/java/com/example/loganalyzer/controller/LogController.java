package com.example.loganalyzer.controller;

import com.example.loganalyzer.model.Log;
import com.example.loganalyzer.model.Threat;
import com.example.loganalyzer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogService logService;

    @PostMapping
    public Log saveLog(@RequestBody Log log) {
        logger.debug("Received request to save log: {}", log);
        Log savedLog = logService.saveLog(log);
        logger.debug("Saved log: {}", savedLog);
        return savedLog;
    }

    @GetMapping
    public List<Log> getAllLogs() {
        logger.debug("Received request to get all logs");
        List<Log> logs = logService.getAllLogs();
        logger.debug("Returning all logs: {}", logs);
        return logs;
    }

    @GetMapping("/threats")
    public List<Threat> getAllThreats() {
        logger.debug("Received request to get all threats");
        List<Threat> threats = logService.getAllThreats();
        logger.debug("Returning all threats: {}", threats);
        return threats;
    }

    /**
     * Endpoint to detect and save threats.
     *
     * @return List of detected threats.
     */
    @GetMapping("/threats/detect")
    public List<Threat> detectThreats() {
        logger.debug("Received request to detect threats");
        List<Threat> detectedThreats = logService.detectAndSaveThreats();
        logger.debug("Detected and saved threats: {}", detectedThreats);
        return detectedThreats;
    }

    @GetMapping("/status")
    public String getStatus() {
        logger.debug("Received request to get status");
        String status = "Log analyzer is running";
        logger.debug("Returning status: {}", status);
        return status;
    }
}
