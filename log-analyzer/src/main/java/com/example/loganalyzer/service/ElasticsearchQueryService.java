// package com.example.loganalyzer.service;

// import org.apache.http.HttpHost;
// import org.apache.http.impl.client.BasicCredentialsProvider;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.auth.AuthScope;
// import org.apache.http.auth.UsernamePasswordCredentials;
// import org.elasticsearch.client.Request;
// import org.elasticsearch.client.Response;
// import org.elasticsearch.client.RestHighLevelClient;
// import org.elasticsearch.client.RestClient;
// import org.elasticsearch.client.RestClientBuilder;
// import org.springframework.stereotype.Service;

// import java.io.IOException;

// @Service
// public class ElasticsearchQueryService {

//     private RestHighLevelClient client;

//     public ElasticsearchQueryService() {
//         // Setup credentials for Elasticsearch (if authentication is enabled)
//         BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//         credentialsProvider.setCredentials(
//             new AuthScope("localhost", 9200),
//             new UsernamePasswordCredentials("elastic", "Jay@92004")  // Replace with your password
//         );

//         // Create the client with the credentials
//         RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"))
//                 .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

//         client = new RestHighLevelClient(builder);
//     }

//     // Query for detecting SQL Injection patterns in URLs and query parameters
//     public String detectSQLInjection() throws IOException {
//         String query = "{\n" +
//             "  \"query\": {\n" +
//             "    \"query_string\": {\n" +
//             "      \"query\": \"*' OR 1=1 -- *\"\n" +
//             "    }\n" +
//             "  }\n" +
//             "}";

//         // Create the request to Elasticsearch
//         Request request = new Request("POST", "/logs-2025.01.02/_search");
//         request.setJsonEntity(query);

//         // Execute the request
//         Response response = client.getLowLevelClient().performRequest(request);

//         // Return the response as a string (you can process the response as needed)
//         return response.getEntity().toString();
//     }

//     // Query for detecting XSS (Cross-Site Scripting) patterns
//     public String detectXSS() throws IOException {
//         String query = "{\n" +
//                 "  \"query\": {\n" +
//                 "    \"query_string\": {\n" +
//                 "      \"query\": \"*<script>* OR *onerror* OR *alert* OR *javascript:*\"\n" +
//                 "    }\n" +
//                 "  }\n" +
//                 "}";

//         // Create the request to Elasticsearch
//         Request request = new Request("POST", "/logs-2025.01.02/_search");
//         request.setJsonEntity(query);

//         // Execute the request
//         Response response = client.getLowLevelClient().performRequest(request);

//         // Return the response as a string (you can process the response as needed)
//         return response.getEntity().toString();
//     }

//     // Query for detecting DDoS attack patterns by identifying multiple requests from same IP
//     public String detectDDoS() throws IOException {
//         String query = "{\n" +
//                 "  \"query\": {\n" +
//                 "    \"range\": {\n" +
//                 "      \"@timestamp\": {\n" +
//                 "        \"gte\": \"now-5m/m\",\n" +
//                 "        \"lt\": \"now/m\"\n" +
//                 "      }\n" +
//                 "    }\n" +
//                 "  },\n" +
//                 "  \"aggs\": {\n" +
//                 "    \"requests_per_ip\": {\n" +
//                 "      \"terms\": {\n" +
//                 "        \"field\": \"client_ip.keyword\",\n" +
//                 "        \"size\": 10,\n" +
//                 "        \"min_doc_count\": 100\n" +  // Threshold for DDoS: e.g., 100 requests per IP in 5 minutes
//                 "      }\n" +
//                 "    }\n" +
//                 "  }\n" +
//                 "}";

//         // Create the request to Elasticsearch
//         Request request = new Request("POST", "/logs-2025.01.02/_search");
//         request.setJsonEntity(query);

//         // Execute the request
//         Response response = client.getLowLevelClient().performRequest(request);

//         // Return the response as a string (you can process the response as needed)
//         return response.getEntity().toString();
//     }

//     // Close the Elasticsearch client
//     public void closeClient() throws IOException {
//         if (client != null) {
//             client.close();
//         }
//     }

//     public static void main(String[] args) {
//         try {
//             ElasticsearchQueryService service = new ElasticsearchQueryService();

//             // Detect SQL injection patterns
//             String sqlInjectionResult = service.detectSQLInjection();
//             System.out.println("SQL Injection Detection Result: " + sqlInjectionResult);

//             // Detect XSS attack patterns
//             String xssResult = service.detectXSS();
//             System.out.println("XSS Detection Result: " + xssResult);

//             // Detect DDoS attack patterns
//             String ddosResult = service.detectDDoS();
//             System.out.println("DDoS Detection Result: " + ddosResult);

//             // Close client
//             service.closeClient();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
