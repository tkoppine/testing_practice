package com.practice.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class CalculatorMockTest {

    @Mock
    Calculator calculator;

    @InjectMocks
    CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDescribeSum() {
        when(calculator.add(2, 3)).thenReturn(5);
        String result = calculatorService.describeSum(2, 3);
        assertEquals("The sum of 2 and 3 is 5", result);
        verify(calculator).add(2, 3);
    }

    @Test
    public void testDescribeProduct() {
        when(calculator.multiply(3, 4)).thenReturn(12);
        String result = calculatorService.describeProduct(3, 4);
        assertEquals("The product of 3 and 4 is 12", result);
        verify(calculator).multiply(3, 4);
    }
}