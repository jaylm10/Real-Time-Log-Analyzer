package com.example.loganalyzer.service;

import com.example.loganalyzer.model.Log;
import com.example.loganalyzer.model.Threat;
import com.example.loganalyzer.repository.LogRepository;
import com.example.loganalyzer.repository.ThreatRepository;
import com.example.loganalyzer.utils.ThreatAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private ThreatAnalyser threatAnalyser;

    public Log saveLog(Log log) {
        return logRepository.save(log); // Save log to Elasticsearch
    }

    public List<Threat> detectAndSaveThreats() {
        List<Threat> detectedThreats = threatAnalyser.findThreatsInLogs();
        for (Threat threat : detectedThreats) {
            threatRepository.save(threat); // Save threats to Elasticsearch
        }
        return detectedThreats;
    }

    public List<Threat> detectAndSaveThreatsByType(String threatType) {
        List<Threat> detectedThreats = threatAnalyser.findThreatsInLogsByType(threatType);
        for (Threat threat : detectedThreats) {
            threatRepository.save(threat); // Save threats to Elasticsearch
        }
        return detectedThreats;
    }

    public List<Threat> getAllThreats() {
        List<Threat> threats = new ArrayList<>();
        threatRepository.findAll().forEach(threats::add);
        return threats;
    }

    public List<Log> getAllLogs() {
        List<Log> logs = new ArrayList<>();
        logRepository.findAll().forEach(logs::add);
        return logs;
    }
}
