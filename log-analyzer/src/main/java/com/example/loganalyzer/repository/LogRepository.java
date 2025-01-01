package com.example.loganalyzer.repository;

import com.example.loganalyzer.model.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends ElasticsearchRepository<Log, String> {}
