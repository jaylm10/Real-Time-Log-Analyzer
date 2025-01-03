import pandas as pd
from sklearn.ensemble import IsolationForest
import re
import json
from elasticsearch import Elasticsearch
import logging

# Set up logging
logging.basicConfig(level=logging.DEBUG)

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

def categorize_threats(row):
    if "sql" in row['url'].lower():
        return "SQL INJECTION"
    elif "dos" in row['url'].lower():
        return "DDOS ATTACK"
    elif "mitm" in row['url'].lower():
        return "MAN IN THE MIDDLE"
    else:
        return "Unknown Threat"

def main(log_file, output_file, block_ips_file):
    df = parse_log(log_file)

    df['status'] = pd.to_numeric(df['status'], errors='coerce')
    df['size'] = pd.to_numeric(df['size'], errors='coerce')

    clf = IsolationForest(random_state=42, contamination=0.1)
    df['anomaly'] = clf.fit_predict(df[['status', 'size']])

    df['category'] = df['anomaly'].map({-1: 'Threat', 1: 'Normal'})

    df['threat_type'] = df.apply(
        lambda row: categorize_threats(row) if row['category'] == 'Threat' else None, axis=1
    )

    # Get the list of malicious IPs
    malicious_ips = df[df['category'] == 'Threat']['ip'].unique().tolist()

    # Save the list of malicious IPs to block
    with open(block_ips_file, 'w') as f:
        json.dump(malicious_ips, f, indent=4)

    # Send the block_ips.json file to Elasticsearch
    es = Elasticsearch(
        [{'host': 'localhost', 'port': 9200, 'scheme': 'http'}],
        basic_auth=('elastic', 'Jay@92004')  # Replace with your actual username and password
    )

    # Remove all entries from the existing index if it exists
    if es.indices.exists(index="blocked_ips_v1"):
        es.delete_by_query(index="blocked_ips_v1", body={"query": {"match_all": {}}})
        print(f"Removed all entries from existing index: blocked_ips")

    with open(block_ips_file, 'r') as f:
        block_ips_data = json.load(f)
        for ip in block_ips_data:
            try:
                es.index(index="blocked_ips_v1", document={"ip": ip})
                logging.debug(f"Indexed IP: {ip}")
            except Exception as e:
                logging.error(f"Failed to index IP: {ip}, Error: {e}")


    print(f"Analysis complete. Results saved to {output_file} and IPs to block saved to {block_ips_file}")

# Run the script
if __name__ == "__main__":
    log_file_path = r"E:\Real-Time-LogAnzalyzer\ml-model\user.log"
    output_file_path = r"E:\Real-Time-LogAnzalyzer\ml-model\analyzed_output.json"
    block_ips_file_path = r"E:\Real-Time-LogAnzalyzer\ml-model\block_ips.json"
    main(log_file_path, output_file_path, block_ips_file_path)