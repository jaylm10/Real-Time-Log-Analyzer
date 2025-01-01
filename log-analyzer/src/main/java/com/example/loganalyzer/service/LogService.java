package com.example.loganalyzer.service;

import com.example.loganalyzer.model.Log;
import com.example.loganalyzer.model.Threat;
import com.example.loganalyzer.repository.LogRepository;
import com.example.loganalyzer.repository.ThreatRepository;
import com.example.loganalyzer.utils.ThreatAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ThreatRepository threatRepository;

    @Autowired
    private ThreatAnalyzer threatAnalyzer;

    public Log saveLog(Log log) {
        log = logRepository.save(log);

        Threat threat = threatAnalyzer.detectThreat(log);
        if (threat != null) {
            threatRepository.save(threat);
        }

        return log;
    }

    public Iterable<Threat> getAllThreats() {
        return threatRepository.findAll();
    }
}
