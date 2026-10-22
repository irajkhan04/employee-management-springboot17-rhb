package com.example.assignment.config;

import com.example.assignment.entity.Department;
import com.example.assignment.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedDepartments(DepartmentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Department("Engineering"));
                repository.save(new Department("Finance"));
            }
        };
    }
}
