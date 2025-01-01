package com.example.loganalyzer.repository;

import com.example.loganalyzer.model.Threat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ThreatRepository extends ElasticsearchRepository<Threat, String> {
}
