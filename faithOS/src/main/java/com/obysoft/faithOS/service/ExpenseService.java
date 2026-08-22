package com.obysoft.faithOS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.dto.ExpenseRequest;
import com.obysoft.faithOS.dto.ExpenseResponse;
import com.obysoft.faithOS.entity.Expense;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.ExpenseRepository;

@Service
public class ExpenseService {
    private final ExpenseRepository repository;
    private final CurrentChurchService current;

    public ExpenseService(ExpenseRepository repository, CurrentChurchService current) {
        this.repository = repository;
        this.current = current;
    }

    public List<ExpenseResponse> all() {
        return repository.findAllByChurchIdOrderByExpenseDateDesc(current.church().getId())
                .stream().map(this::response).toList();
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        Expense expense = new Expense();
        apply(expense, request);
        expense.setChurch(current.church());
        return response(repository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = find(id);
        apply(expense, request);
        return response(repository.save(expense));
    }

    @Transactional
    public void delete(Long id) { repository.delete(find(id)); }

    private Expense find(Long id) {
        return repository.findByIdAndChurchId(id, current.church().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found."));
    }

    private void apply(Expense expense, ExpenseRequest request) {
        expense.setDescription(request.description().trim());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setCategory(request.category().trim());
        expense.setPayee(request.payee());
        expense.setNotes(request.notes());
    }

    private ExpenseResponse response(Expense expense) {
        return new ExpenseResponse(expense.getId(), expense.getDescription(), expense.getAmount(),
                expense.getExpenseDate(), expense.getCategory(), expense.getPayee(), expense.getNotes());
    }
}
