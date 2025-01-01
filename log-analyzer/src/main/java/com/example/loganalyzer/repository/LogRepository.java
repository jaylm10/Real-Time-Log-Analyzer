package com.example.loganalyzer.repository;

import com.example.loganalyzer.model.Log;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends CrudRepository<Log, String> { // Use String for the ID type
    List<Log> findAll(); // Custom method to find all logs
}
