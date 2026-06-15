package com.practice.calculator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/add")
    public String add(@RequestParam int a, @RequestParam int b) {
        return calculatorService.describeSum(a, b);
    }

    @GetMapping("/multiply")
    public String multiply(@RequestParam int a, @RequestParam int b) {
        return calculatorService.describeProduct(a, b);
    }

    @GetMapping("/subtract")
    public String subtract(@RequestParam int a, @RequestParam int b) {
        return calculatorService.describeSubtraction(a, b);
    }

    @GetMapping("/divide")
    public ResponseEntity<String> divide(@RequestParam int a, @RequestParam int b) {
        try {
            return ResponseEntity.ok(calculatorService.describeDivision(a, b));
        } catch (ArithmeticException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}