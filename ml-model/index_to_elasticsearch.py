from elasticsearch import Elasticsearch, helpers
import json

def index_data_to_elasticsearch(json_file, es_host, index_name, username, password):
    # Initialize Elasticsearch client with authentication
    es = Elasticsearch(
        [es_host],
        basic_auth=(username, password)
    )

    # Remove all entries from the existing index if it exists
    if es.indices.exists(index=index_name):
        es.delete_by_query(index=index_name, body={"query": {"match_all": {}}})
        print(f"Removed all entries from existing index: {index_name}")

    # Load the JSON file
    with open(json_file, 'r') as f:
        data = json.load(f)

    # Prepare bulk indexing
    actions = [
        {
            "_index": index_name,
            "_source": record
        }
        for record in data
    ]

    # Perform bulk indexing
    helpers.bulk(es, actions)
    print(f"Data successfully indexed into Elasticsearch index: {index_name}")

# Main script
if __name__ == "__main__":
    json_file_path = "analyzed_output.json"  # Path to your JSON file
    elasticsearch_host = "http://localhost:9200"  # Change to your Elasticsearch host
    index_name = "log_analysis_v4"  # Specify your desired index name
    username = "elastic"  # Replace with your Elasticsearch username
    password = "Jay@92004"  # Replace with your Elasticsearch password

    index_data_to_elasticsearch(json_file_path, elasticsearch_host, index_name, username, password)
