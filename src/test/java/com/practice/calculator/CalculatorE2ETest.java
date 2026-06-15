package com.practice.calculator;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculatorE2ETest {

    // Testcontainers starts real PostgreSQL Docker container
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("calculator_test")
            .withUsername("test")
            .withPassword("test");

    // Injects container DB URL into Spring datasource config
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // Spring Boot starts on a random port
    @LocalServerPort
    int port;

    @Autowired
    CalculationHistoryRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        repository.deleteAll();
    }

    // -------------------------------------------------------
    // REST Assured calls real HTTP endpoint
    // Asserts HTTP response
    // Then checks the DB was updated correctly
    // -------------------------------------------------------

    @Test
    void testAdd() {
        given()
            .param("a", 2).param("b", 3)
        .when()
            .get("/api/calculator/add")
        .then()
            .statusCode(200)
            .body(equalTo("The sum of 2 and 3 is 5"));

        List<CalculationHistory> history = repository.findByOperation("add");
        assertEquals(1, history.size());
        assertEquals(2, history.get(0).getOperandA());
        assertEquals(3, history.get(0).getOperandB());
        assertEquals(5, history.get(0).getResult());
    }

    @Test
    void testMultiply() {
        given()
            .param("a", 3).param("b", 4)
        .when()
            .get("/api/calculator/multiply")
        .then()
            .statusCode(200)
            .body(equalTo("The product of 3 and 4 is 12"));

        List<CalculationHistory> history = repository.findByOperation("multiply");
        assertEquals(1, history.size());
        assertEquals(12, history.get(0).getResult());
    }

    @Test
    void testSubtract() {
        given()
            .param("a", 5).param("b", 3)
        .when()
            .get("/api/calculator/subtract")
        .then()
            .statusCode(200)
            .body(equalTo("The subtraction of 5 and 3 is 2"));

        List<CalculationHistory> history = repository.findByOperation("subtract");
        assertEquals(1, history.size());
        assertEquals(2, history.get(0).getResult());
    }

    @Test
    void testDivide() {
        given()
            .param("a", 10).param("b", 2)
        .when()
            .get("/api/calculator/divide")
        .then()
            .statusCode(200)
            .body(equalTo("The division of 10 and 2 is 5"));

        List<CalculationHistory> history = repository.findByOperation("divide");
        assertEquals(1, history.size());
        assertEquals(5, history.get(0).getResult());
    }

    @Test
    void testDivideByZero() {
        given()
            .param("a", 5).param("b", 0)
        .when()
            .get("/api/calculator/divide")
        .then()
            .statusCode(400)
            .body(equalTo("Cannot divide by zero"));

        assertEquals(0, repository.findByOperation("divide").size());
    }

    @Test
    void testMultipleOperationsStoredSeparately() {
        given().param("a", 1).param("b", 2).when().get("/api/calculator/add");
        given().param("a", 3).param("b", 4).when().get("/api/calculator/add");
        given().param("a", 2).param("b", 5).when().get("/api/calculator/multiply");

        assertEquals(2, repository.findByOperation("add").size());
        assertEquals(1, repository.findByOperation("multiply").size());
        assertEquals(3, repository.findAll().size());
    }
}
