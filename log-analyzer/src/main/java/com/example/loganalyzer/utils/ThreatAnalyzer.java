package com.example.loganalyzer.utils;

import com.example.loganalyzer.model.Log;
import com.example.loganalyzer.model.Threat;
import org.springframework.stereotype.Component;

@Component
public class ThreatAnalyzer {

    public Threat detectThreat(Log log) {
        if (log.getStatusCode() >= 500) {
            return new Threat(
                log.getId(),
                log.getIp(),
                "HIGH",
                "Server error detected: " + log.getStatusCode()
            );
        }

        if ("ERROR".equalsIgnoreCase(log.getLevel())) {
            return new Threat(
                log.getId(),
                log.getIp(),
                "MEDIUM",
                "Log level indicates an error: " + log.getMessage()
            );
        }

        if (log.getUri().contains("/admin") || log.getUri().contains("/login")) {
            return new Threat(
                log.getId(),
                log.getIp(),
                "LOW",
                "Suspicious activity detected on sensitive endpoints: " + log.getUri()
            );
        }

        return null;
    }
}
