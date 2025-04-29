package com.podlive.tracker.goal.service;

import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.repository.AccountRepository;
import com.podlive.tracker.goal.dto.GoalDTO;
import com.podlive.tracker.goal.model.Goal;
import com.podlive.tracker.goal.repository.GoalRepository;
import com.podlive.tracker.transaction.model.Transaction;
import com.podlive.tracker.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public GoalService(GoalRepository goalRepository, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Goal createGoal(GoalDTO goalDTO) {
        Account account = accountRepository.findById(goalDTO.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Goal goal = new Goal();
        goal.setName(goalDTO.getName());
        goal.setTargetAmount(goalDTO.getTargetAmount());
        goal.setAccount(account);

        return goalRepository.save(goal);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goal getGoalById(Integer id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found"));
    }

    public BigDecimal getGoalProgress(Integer goalId) {
        Goal goal = getGoalById(goalId);

        BigDecimal currentSavings = transactionRepository.findByAccount(goal.getAccount()).stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return currentSavings
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean isOverspending(Integer goalId, BigDecimal spendingLimit) {
        Goal goal = getGoalById(goalId);

        BigDecimal totalSpending = transactionRepository.findByAccount(goal.getAccount()).stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();

        return totalSpending.compareTo(spendingLimit) > 0;
    }
}
