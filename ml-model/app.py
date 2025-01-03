import pandas as pd
from sklearn.ensemble import IsolationForest
import re
import json
from sklearn.metrics import accuracy_score, confusion_matrix

# Function to parse the log file into a DataFrame
def parse_log(file_path):
    data = []
    with open(file_path, 'r') as file:
        for line in file:
            match = re.match(
                r'(?P<ip>[\d\.]+) - - \[(?P<timestamp>.+)\] "(?P<request_method>\w+) (?P<url>.+) HTTP/1\.\d" (?P<status>\d+) (?P<size>\d+)', 
                line
            )
            if match:
                data.append(match.groupdict())
    return pd.DataFrame(data)

# Function to categorize threats
def categorize_threats(row):
    if "sql" in row['url'].lower():
        return "SQL INJECTION"
    elif "dos" in row['url'].lower():
        return "DDOS ATTACK"
    elif "mitm" in row['url'].lower():
        return "MAN IN THE MIDDLE"
    else:
        return "Unknown Threat"
    

# Main function
def main(log_file, output_file):
    # Parse log file
    df = parse_log(log_file)

    # Convert necessary columns to numeric where applicable
    df['status'] = pd.to_numeric(df['status'], errors='coerce')
    df['size'] = pd.to_numeric(df['size'], errors='coerce')

    # Use Isolation Forest to detect anomalies
    clf = IsolationForest(random_state=42, contamination=0.1)
    df['anomaly'] = clf.fit_predict(df[['status', 'size']])

    # Map anomalies to "Threat" and "Normal"
    df['category'] = df['anomaly'].map({-1: 'Threat', 1: 'Normal'})

    # Further categorize threats
    df['threat_type'] = df.apply(
        lambda row: categorize_threats(row) if row['category'] == 'Threat' else None, axis=1
    )

    # Save the output to a JSON file
    output_data = df.to_dict(orient='records')
    with open(output_file, 'w') as f:
        json.dump(output_data, f, indent=4)

    print(f"Analysis complete. Results saved to {output_file}")



if __name__ == "__main__":
    log_file_path = r"E:\Real-Time-LogAnzalyzer\ml-model\user.log"
    output_file_path = r"E:\Real-Time-LogAnzalyzer\ml-model\analyzed_output.json"

    # Debug the input file path
    print(f"Reading log file from: {log_file_path}")
    
    main(log_file_path, output_file_path)
