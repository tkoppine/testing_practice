package com.practice.calculator;

import javax.persistence.*;

@Entity
@Table(name = "calculation_history")
public class CalculationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operation;
    private int operandA;
    private int operandB;
    private int result;

    public CalculationHistory() {}

    public CalculationHistory(String operation, int operandA, int operandB, int result) {
        this.operation = operation;
        this.operandA = operandA;
        this.operandB = operandB;
        this.result = result;
    }

    public Long getId() { return id; }
    public String getOperation() { return operation; }
    public int getOperandA() { return operandA; }
    public int getOperandB() { return operandB; }
    public int getResult() { return result; }
}
