package com.example.loganalyzer.utils;

import com.example.loganalyzer.model.Log;
import com.example.loganalyzer.model.Threat;
import com.example.loganalyzer.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ThreatAnalyser {

    @Autowired
    private LogRepository logRepository;

    private static final List<String> SQL_INJECTION_PATTERNS = List.of(
            "select * from", "union select", "' or '1'='1", "drop table"
    );
    private static final List<String> XSS_PATTERNS = List.of(
            "<script>", "javascript:"
    );
    private static final List<String> DDOS_PATTERNS = List.of(
            "ddos", "excessive traffic"
    );

    /**
     * Finds threats in logs using in-memory search.
     *
     * @return List of Threat objects detected in logs.
     */
    public List<Threat> findThreatsInLogs() {
        List<Threat> threats = new ArrayList<>();

        // Search for SQL Injection threats
        List<Log> sqlInjectionLogs = searchLogsByPatterns(SQL_INJECTION_PATTERNS);
        threats.addAll(convertLogsToThreats(sqlInjectionLogs, "SQL Injection", "High"));

        // Search for XSS threats
        List<Log> xssLogs = searchLogsByPatterns(XSS_PATTERNS);
        threats.addAll(convertLogsToThreats(xssLogs, "XSS Attack", "Medium"));

        // Search for DDoS threats
        List<Log> ddosLogs = searchLogsByPatterns(DDOS_PATTERNS);
        threats.addAll(convertLogsToThreats(ddosLogs, "DDoS Attack", "Critical"));

        return threats;
    }

    /**
     * Finds threats of a specific type in logs using in-memory search.
     *
     * @param threatType The type of threat to search for.
     * @return List of Threat objects detected in logs.
     */
    public List<Threat> findThreatsInLogsByType(String threatType) {
        List<Threat> threats = new ArrayList<>();
        List<Log> logs;

        switch (threatType) {
            case "SQL Injection":
                logs = searchLogsByPatterns(SQL_INJECTION_PATTERNS);
                threats.addAll(convertLogsToThreats(logs, "SQL Injection", "High"));
                break;
            case "XSS Attack":
                logs = searchLogsByPatterns(XSS_PATTERNS);
                threats.addAll(convertLogsToThreats(logs, "XSS Attack", "Medium"));
                break;
            case "DDoS Attack":
                logs = searchLogsByPatterns(DDOS_PATTERNS);
                threats.addAll(convertLogsToThreats(logs, "DDoS Attack", "Critical"));
                break;
            default:
                // Handle unknown threat type
                break;
        }

        return threats;
    }

    /**
     * Searches logs in memory for specific patterns.
     *
     * @param patterns List of patterns to search for.
     * @return List of logs matching the patterns.
     */
    private List<Log> searchLogsByPatterns(List<String> patterns) {
        List<Log> allLogs = new ArrayList<>();
        logRepository.findAll().forEach(allLogs::add);

        List<Log> matchingLogs = new ArrayList<>();
        for (Log log : allLogs) {
            for (String pattern : patterns) {
                if (log.getMessage().contains(pattern)) {
                    matchingLogs.add(log);
                    break;
                }
            }
        }

        return matchingLogs;
    }

    /**
     * Converts logs to Threat objects.
     *
     * @param logs       List of logs.
     * @param threatType The type of threat (e.g., SQL Injection, XSS).
     * @param threatLevel The severity level of the threat.
     * @return List of Threat objects.
     */
    private List<Threat> convertLogsToThreats(List<Log> logs, String threatType, String threatLevel) {
        List<Threat> threats = new ArrayList<>();
        for (Log log : logs) {
            Threat threat = new Threat();
            threat.setId(log.getId());
            threat.setMessage(log.getMessage());
            threat.setType(threatType);
            threat.setLevel(threatLevel);
            threats.add(threat);
        }
        return threats;
    }
}
