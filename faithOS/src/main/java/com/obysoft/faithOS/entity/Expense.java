package com.obysoft.faithOS.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String description;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal amount;
    @Column(name = "expense_date", nullable = false) private LocalDate expenseDate;
    @Column(nullable = false) private String category;
    private String payee;
    @Column(length = 1000) private String notes;
    @ManyToOne(optional = false) @JoinColumn(name = "church_id", nullable = false) private Church church;

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Church getChurch() { return church; }
    public void setChurch(Church church) { this.church = church; }
}
