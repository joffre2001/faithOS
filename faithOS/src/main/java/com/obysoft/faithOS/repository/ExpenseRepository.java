package com.obysoft.faithOS.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.faithOS.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByChurchIdOrderByExpenseDateDesc(Long churchId);
    List<Expense> findAllByChurchIdAndExpenseDateBetween(Long churchId, LocalDate from, LocalDate to);
    Optional<Expense> findByIdAndChurchId(Long id, Long churchId);
}
