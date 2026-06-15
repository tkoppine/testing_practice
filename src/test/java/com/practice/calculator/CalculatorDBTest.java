package com.practice.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
public class CalculatorDBTest {

    // Testcontainers starts a real PostgreSQL Docker container
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("calculator_test")
            .withUsername("test")
            .withPassword("test");

    // Feeds the container's DB URL into Spring's datasource config
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    CalculatorService calculatorService;

    @Autowired
    CalculationHistoryRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    void testAddSavesToDatabase() {
        calculatorService.describeSum(2, 3);

        List<CalculationHistory> history = repository.findByOperation("add");
        assertEquals(1, history.size());
        assertEquals(2, history.get(0).getOperandA());
        assertEquals(3, history.get(0).getOperandB());
        assertEquals(5, history.get(0).getResult());
    }

    @Test
    void testMultiplySavesToDatabase() {
        calculatorService.describeProduct(3, 4);

        List<CalculationHistory> history = repository.findByOperation("multiply");
        assertEquals(1, history.size());
        assertEquals(12, history.get(0).getResult());
    }

    @Test
    void testMultipleOperationsSavedSeparately() {
        calculatorService.describeSum(1, 2);
        calculatorService.describeSum(3, 4);
        calculatorService.describeProduct(2, 5);

        assertEquals(2, repository.findByOperation("add").size());
        assertEquals(1, repository.findByOperation("multiply").size());
        assertEquals(3, repository.findAll().size());
    }
}