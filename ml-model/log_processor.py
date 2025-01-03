from flask import Flask, request, jsonify
import logging
import pandas as pd
import numpy as np
from sklearn.ensemble import IsolationForest
import joblib

# Initialize Flask app
app = Flask(__name__)

# Set up logging
logging.basicConfig(level=logging.DEBUG)

# Load the Isolation Forest model
# Ensure the model is loaded only once to avoid reloading on each request
isolation_forest_model = joblib.load('anomaly_detection_model.pkl')

@app.route("/process_logs", methods=["POST"])
def process_logs():
    """
    API endpoint to process logs and detect anomalies.
    Accepts a JSON payload with logs.
    """
    # Log the incoming request data
    logging.debug("Received request data: %s", request.json)

    # Check if the incoming data is a list
    if isinstance(request.json, list):
        logs = request.json  # Directly use the list of log entries
    else:
        logging.error("Expected a list but got: %s", type(request.json))
        return jsonify({"error": "Invalid input format"}), 400

    results = []

    # Prepare data for anomaly detection
    anomaly_data = []

    # Iterate over each log entry to prepare for anomaly detection
    for log_entry in logs:
        ip_address = log_entry.get("ip_address", "")
        print(ip_address)
        print("its working now")
        status_code = log_entry.get("response_code", 200)  # Default to 200 if not provided

        if ip_address and status_code is not None:
            print("working")
            ip_address_numeric = int(ip_address.replace('.', ''))  # Convert IP to numeric format
            anomaly_data.append([ip_address_numeric, status_code])

    # Convert anomaly data to DataFrame for prediction
    if anomaly_data:
        anomaly_df = pd.DataFrame(anomaly_data, columns=['ip_address_numeric', 'status_code'])
        
        # Predict anomalies using the Isolation Forest model
        anomaly_predictions = isolation_forest_model.predict(anomaly_df)
        
        # Convert predictions to binary (1 for anomaly, 0 for normal)
        anomaly_predictions_binary = np.where(anomaly_predictions == -1, 1, 0)

        # Append anomaly detection results to the results list
        for i, log_entry in enumerate(logs):
            results.append({
                "log": log_entry,
                "anomaly_label": "anomaly" if anomaly_predictions_binary[i] == 1 else "normal"
            })

    return jsonify(results)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)