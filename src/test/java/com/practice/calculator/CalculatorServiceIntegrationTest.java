package com.practice.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorServiceIntegrationTest {

    CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService(new Calculator());
    }

    @Test
    public void testDescribeSum() {
        assertEquals("The sum of 2 and 3 is 5", calculatorService.describeSum(2, 3));
    }

    @Test
    public void testDescribeProduct() {
        assertEquals("The product of 3 and 4 is 12", calculatorService.describeProduct(3, 4));
    }

    @Test
    public void testDescribeSumWithNegativeNumbers() {
        assertEquals("The sum of -2 and 3 is 1", calculatorService.describeSum(-2, 3));
    }

    @Test
    public void testDescribeSumWithZero() {
        assertEquals("The sum of 0 and 5 is 5", calculatorService.describeSum(0, 5));
    }
}