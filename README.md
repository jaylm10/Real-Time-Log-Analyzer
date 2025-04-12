# 🔍 Real-Time Log Analyzer for Threat Detection

A real-time cybersecurity threat detection system built with the ELK Stack (Elasticsearch, Logstash, Kibana), Spring Boot, and Machine Learning. It processes log files, detects anomalies (SQL Injection, DDoS, Brute Force, Unauthorized Access), and visualizes threats on dynamic dashboards.

🚀 Built in 36 hours during a hackathon with a team of 4.

---

## 📌 Features

- ✅ Real-time log ingestion and parsing using Logstash  
- ✅ Machine Learning model for detecting anomalies (e.g., brute force, SQL injection)  
- ✅ Spring Boot API backend for ML inference and threat classification  
- ✅ Elasticsearch for scalable log storage and fast querying  
- ✅ Kibana dashboards for real-time threat visualization  
- ✅ Free threat intelligence integration (AlienVault OTX, AbuseIPDB)  
- ✅ Modular GitHub repo with CI-friendly structure  

---

## 🧱 Architecture

Log Files ➜ Logstash ➜ Spring Boot + ML Model ➜ Elasticsearch ➜ Kibana Dashboards
                                  ⬑         ↳ Threat Intelligence APIs


📂 Project Structure

real-time-log-analyzer/
│
├── log-ingestion/         # Logstash configuration and sample logs
│   └── logstash.conf
│
├── backend/               # Spring Boot app with ML model integration
│   ├── src/
│   ├── pom.xml
│   └── application.yml
│
├── ml-models/             # Python ML scripts (e.g., Isolation Forest)
│   └── train_model.py
│
├── elasticsearch/         # Index mappings and search queries
│   └── index-setup.json
│
├── kibana/                # Dashboard exports (JSON) and screenshots
│   └── dashboard.ndjson
│
├── docs/                  # Documentation and sample log files
│   └── README.md
│
└── README.md              # This file


🔧 Installation & Setup
Prerequisites
Java 17+

Python 3.8+

Docker + Docker Compose (optional)

Elasticsearch (v8+)

Kibana

Logstash

Maven

1. Clone the Repository

git clone https://github.com/yourusername/real-time-log-analyzer.git
cd real-time-log-analyzer

2. Run Logstash

Edit log-ingestion/logstash.conf with your log path, then run:
logstash -f log-ingestion/logstash.conf

3. Start Elasticsearch & Kibana (via Docker)

docker-compose up -d
Docker Compose file should define Elasticsearch and Kibana containers.

4. Train & Serve ML Model

cd ml-models
python train_model.py
Optional: Deploy model as a microservice or integrate into Spring Boot directly.

5. Run Spring Boot App

cd backend
./mvnw spring-boot:run
Spring Boot exposes an endpoint to receive logs and return anomaly predictions.

📊 Kibana Dashboards
Dashboard Panels:
Top Malicious IPs
Failed Login Heatmaps
DDoS Spike Detection
SQLi Attempt Frequency

Import from: kibana/dashboard.ndjson

📁 Sample Logs
Located in /docs/sample-logs/, including:

SQL Injection attempts
Brute Force login patterns
Simulated DDoS bursts
Unauthorized /admin access

🛡️ Threat Intelligence Sources
AlienVault OTX API
AbuseIPDB
(Optional) VirusTotal for URL/file checks

👥 Team Members
Member 1 – Logstash & Log Parsing
Member 2 – Spring Boot & ML API
Member 3 – Elasticsearch Queries & Threat Detection
Member 4 – Kibana Dashboards & Repo Management

🤝 Contribution Guide
We follow feature-based branching:

main – production-ready code
log-ingestion – Logstash pipeline setup
backend – Spring Boot API + ML logic
elasticsearch – Indexing and query writing
kibana – Visualization components

📜 License
This project is open-source under the MIT License.