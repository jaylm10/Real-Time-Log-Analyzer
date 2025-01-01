package com.example.loganalyzer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "log-analyzer-logs")
public class Log {

    @Id
    private String id;
    private String ip;
    private String timestamp;
    private String method;
    private String uri;
    private int statusCode;
    private String level;
    private String message;
    private String source;

    // Constructors
    public Log() {}

    public Log(String id, String ip, String timestamp, String method, String uri, int statusCode, String level, String message, String source) {
        this.id = id;
        this.ip = ip;
        this.timestamp = timestamp;
        this.method = method;
        this.uri = uri;
        this.statusCode = statusCode;
        this.level = level;
        this.message = message;
        this.source = source;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
