package com.example.loganalyzer.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;
import java.util.logging.Logger;

@Service
public class LogService {

    private static final Logger logger = Logger.getLogger(LogService.class.getName());

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private RestTemplate restTemplate; // RestTemplate for calling the ML model API

    private static final int BATCH_SIZE = 1000; // Number of logs to fetch in each batch
    private static final Scroll SCROLL = new Scroll(TimeValue.timeValueMinutes(1)); // Scroll timeout
    private static final String ML_MODEL_URL = "http://localhost:5000/process_logs"; // Replace with your ML model's API endpoint

    public List<Map<String, Object>> fetchAndAnalyzeLogs(String indexName) {
        List<Map<String, Object>> logEntries = new ArrayList<>();
        List<Map<String, Object>> analyzedLogs = new ArrayList<>();

        try {
            logger.info("Initializing search request for index: " + indexName);
            // Initialize the search request with the specified index
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            sourceBuilder.size(BATCH_SIZE); // Set the batch size

            searchRequest.source(sourceBuilder);
            searchRequest.scroll(SCROLL); // Set the scroll context

            // Execute the initial search
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            logger.info("Initial search executed. Processing results...");

            // Process the first batch of results
            while (response.getHits().getHits().length > 0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    logEntries.add(hit.getSourceAsMap()); // Add the log entry as a Map
                }

                // Send logs to the ML model for analysis
                List<Map<String, Object>> batchAnalysis = analyzeLogsWithMLModel(logEntries);
                analyzedLogs.addAll(batchAnalysis);

                // Clear the fetched logs
                logEntries.clear();

                // Get the next batch of results
                String scrollId = response.getScrollId();
                response = client.scroll(new SearchScrollRequest(scrollId).scroll(SCROLL), RequestOptions.DEFAULT);
            }

            // Clear scroll context after processing all batches
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(response.getScrollId());
            client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.severe("Error fetching and analyzing logs: " + e.getMessage());
            e.printStackTrace();
        }
        logger.info("Returning analyzed logs. Total logs analyzed: " + analyzedLogs.size());
        return analyzedLogs; // Return the list of analyzed logs
    }

    private List<Map<String, Object>> analyzeLogsWithMLModel(List<Map<String, Object>> logs) {
        try {
            logger.info("Sending logs to ML model for analysis. Logs count: " + logs.size());
            // Send logs to the ML model via POST request
            List<Map<String, Object>> response = Arrays.asList(restTemplate.postForObject(ML_MODEL_URL, logs, Map[].class));
            logger.info("Received response from ML model. Analyzed logs count: " + response.size());
            return response;
        } catch (Exception e) {
            logger.severe("Error analyzing logs with ML model: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Return an empty list in case of errors
        }
    }

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

        // Check for null response body
        if (response.getBody() == null) {
            logger.warning("Received null response body from ML model.");
            return Collections.emptyList();
        }

        // Return the response body
        return response.getBody();
    }
}