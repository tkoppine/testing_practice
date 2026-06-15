package com.practice.calculator;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    private final Calculator calculator;
    private final CalculationHistoryRepository repository;

    public CalculatorService(Calculator calculator) {
        this.calculator = calculator;
        this.repository = null;
    }

    public CalculatorService(Calculator calculator, CalculationHistoryRepository repository) {
        this.calculator = calculator;
        this.repository = repository;
    }

    public String describeSum(int a, int b) {
        int result = calculator.add(a, b);
        if (repository != null) repository.save(new CalculationHistory("add", a, b, result));
        return "The sum of " + a + " and " + b + " is " + result;
    }

    public String describeProduct(int a, int b) {
        int result = calculator.multiply(a, b);
        if (repository != null) repository.save(new CalculationHistory("multiply", a, b, result));
        return "The product of " + a + " and " + b + " is " + result;
    }

    public String describeSubtraction(int a, int b) {
        int result = calculator.substraction(a, b);
        if (repository != null) repository.save(new CalculationHistory("subtract", a, b, result));
        return "The subtraction of " + a + " and " + b + " is " + result;
    }

    public String describeDivision(int a, int b) {
        int result = calculator.division(a, b);
        if (repository != null) repository.save(new CalculationHistory("divide", a, b, result));
        return "The division of " + a + " and " + b + " is " + result;
    }
}
