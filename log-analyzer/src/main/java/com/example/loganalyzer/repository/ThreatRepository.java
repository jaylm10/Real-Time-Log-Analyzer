package com.example.loganalyzer.repository;

import com.example.loganalyzer.model.Threat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreatRepository extends ElasticsearchRepository<Threat, String> {}
