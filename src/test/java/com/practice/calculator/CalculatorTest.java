package com.practice.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorTest {

    Calculator cal = new Calculator();

    @Test
    public void addMethodTest() {
        assertEquals(10, cal.add(5, 5));
    }

    @Test
    public void subtractMethodTest() {
        assertEquals(0, cal.substraction(5, 5));
    }

    @Test
    public void multiplyMethodTest() {
        assertEquals(25, cal.multiply(5, 5));
    }

    @Test
    public void divideMethodTest() {
        assertEquals(1, cal.division(5, 5));
    }

    @Test
    public void divideByZeroTest() {
        assertThrows(ArithmeticException.class, () -> cal.division(5, 0));
    }
}