package com.practice.calculator;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {
    List<CalculationHistory> findByOperation(String operation);
}