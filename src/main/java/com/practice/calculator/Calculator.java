package com.practice.calculator;

import org.springframework.stereotype.Component;

@Component
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int substraction(int a, int b) {
        return a - b;
    }

    public int division(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }
}