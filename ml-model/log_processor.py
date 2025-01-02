from transformers import pipeline
from flask import Flask, request, jsonify

# Initialize Flask app
app = Flask(__name__)

# Initialize the Hugging Face model
classifier = pipeline("zero-shot-classification", model="facebook/bart-large-mnli")
candidate_labels = ["normal", "anomaly"]

@app.route("/process_logs", methods=["POST"])
def process_logs():
    """
    API endpoint to process logs and detect anomalies.
    Accepts a JSON payload with logs.
    """
    # Extract the hits from the incoming JSON
    hits = request.json.get("hits", {}).get("hits", [])
    results = []

    # Iterate over each hit to extract the log message
    for hit in hits:
        log_message = hit["_source"].get("message", "")  # Extract the log message
        if log_message:  # Only process if there is a log message
            result = classifier(log_message, candidate_labels)
            results.append({
                "log": log_message,
                "label": result["labels"][0],  # Top predicted label
                "score": result["scores"][0]   # Confidence score
            })
    
    return jsonify(results)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)