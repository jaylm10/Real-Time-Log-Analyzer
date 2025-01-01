package com.example.loganalyzer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "log-analyzer-threats")
public class Threat {

    @Id
    private String id;
    private String ip;
    private String threatLevel;
    private String description;

    // Constructors
    public Threat() {}

    public Threat(String id, String ip, String threatLevel, String description) {
        this.id = id;
        this.ip = ip;
        this.threatLevel = threatLevel;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getThreatLevel() { return threatLevel; }
    public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
