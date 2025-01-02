from flask import Flask, request, jsonify
import pandas as pd
from sklearn.ensemble import IsolationForest
import json
from collections import Counter

app = Flask(__name__)

# Model and function for threat detection
def analyze_logs(logs):
    df = pd.DataFrame(logs)

    df['status_code'] = pd.to_numeric(df['status_code'], errors='coerce')
    df['ip_address_numeric'] = pd.to_numeric(df['ip_address'].str.replace('.', ''), errors='coerce')
    df = df.dropna(subset=['status_code', 'ip_address_numeric'])

    # Predefined threat patterns
    blacklisted_ips = ["192.168.1.13", "203.0.113.42"]
    suspicious_urls = ["/admin", "/config", "/login"]
    suspicious_user_agents = ["curl", "sqlmap", "nmap"]
    sql_injection_keywords = ["UNION", "SELECT", "OR 1=1", "DROP TABLE", "--", "'--"]
    xss_keywords = ["<script>", "</script>", "javascript:", "onerror=", "onload="]
    ddos_threshold = 100
    
    df['is_mitm'] = df['url_accessed'].str.startswith('http://').astype(int)
    df['is_blacklisted'] = df['ip_address'].isin(blacklisted_ips).astype(int)
    df['is_suspicious_url'] = df['url_accessed'].isin(suspicious_urls).astype(int)
    df['is_suspicious_user_agent'] = df['user_agent'].str.contains('|'.join(suspicious_user_agents), case=False, na=False).astype(int)
    df['is_sql_injection'] = df['url_accessed'].str.contains('|'.join(sql_injection_keywords), case=False, na=False).astype(int)

    ip_request_counts = df['ip_address'].value_counts()
    ddos_ips = ip_request_counts[ip_request_counts > ddos_threshold].index.tolist()
    df['is_ddos'] = df['ip_address'].isin(ddos_ips).astype(int)

    # Anomaly Detection with Isolation Forest
    model = IsolationForest(contamination=0.05, random_state=42)
    df['anomaly'] = model.fit_predict(df[['status_code', 'ip_address_numeric']])
    df['anomaly_score'] = model.decision_function(df[['status_code', 'ip_address_numeric']])

    THREAT_THRESHOLD = -0.1
    df['is_anomalous'] = (df['anomaly_score'] < THREAT_THRESHOLD).astype(int)

    # Threat Classification
    def classify_threat(row):
        if row['is_sql_injection']:
            return "SQL Injection"
        elif row['is_ddos']:
            return "DDoS Attack"
        elif row['is_mitm']:
            return "Man-in-the-Middle Attack"
        elif row['is_xss']:
            return "XSS Attack"
        elif row['is_blacklisted']:
            return "Blacklisted IP"
        elif row['is_suspicious_url']:
            return "Suspicious URL Access"
        elif row['is_suspicious_user_agent']:
            return "Malicious User-Agent"
        elif row['is_anomalous']:
            return "Anomaly Detected"
        return "No Threat"

    df['threat_type'] = df.apply(classify_threat, axis=1)

    # Filter out the logs with threats
    threats = df[df['threat_type'] != "No Threat"]

    return threats

@app.route('/analyze', methods=['POST'])
def analyze_logs_api():
    try:
        # Receive logs from the Spring Boot application in JSON format
        logs = request.json

        # Analyze the logs and classify threats
        threats = analyze_logs(logs)

        if not threats.empty:
            response = threats[['timestamp', 'ip_address', 'status_code', 'url_accessed', 'user_agent', 'anomaly_score', 'threat_type']].to_dict(orient='records')
            return jsonify(response), 200
        else:
            return jsonify({"message": "No threats detected."}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Endpoint for health check
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "OK"}), 200

if __name__ == "_main_":
    app.run(port=5000)