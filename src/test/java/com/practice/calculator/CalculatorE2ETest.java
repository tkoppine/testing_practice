package com.practice.calculator;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculatorE2ETest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testAdd() {
        given()
            .param("a", 2)
            .param("b", 3)
        .when()
            .get("/api/calculator/add")
        .then()
            .statusCode(200)
            .body(equalTo("The sum of 2 and 3 is 5"));
    }

    @Test
    void testMultiply() {
        given()
            .param("a", 3)
            .param("b", 4)
        .when()
            .get("/api/calculator/multiply")
        .then()
            .statusCode(200)
            .body(equalTo("The product of 3 and 4 is 12"));
    }

    @Test
    void testSubtract() {
        given()
            .param("a", 5)
            .param("b", 3)
        .when()
            .get("/api/calculator/subtract")
        .then()
            .statusCode(200)
            .body(equalTo("The subtraction of 5 and 3 is 2"));
    }

    @Test
    void testDivide() {
        given()
            .param("a", 10)
            .param("b", 2)
        .when()
            .get("/api/calculator/divide")
        .then()
            .statusCode(200)
            .body(equalTo("The division of 10 and 2 is 5"));
    }

    @Test
    void testDivideByZero() {
        given()
            .param("a", 5)
            .param("b", 0)
        .when()
            .get("/api/calculator/divide")
        .then()
            .statusCode(400)
            .body(equalTo("Cannot divide by zero"));
    }
}