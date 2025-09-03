package com.CashFlow.Dashboard.Repository;

import com.CashFlow.Dashboard.Entities.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT FUNCTION('TO_CHAR', t.date, 'YYYY-MM') as month, " +
           "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as totalIncome, " +
           "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as totalExpense " +
           "FROM Transaction t " +
           "GROUP BY FUNCTION('TO_CHAR', t.date, 'YYYY-MM') " +
           "ORDER BY month")
    List<Object[]> getMonthlyTrends();

    @Query("SELECT t.category, SUM(t.amount) " +
           "FROM Transaction t " +
           "WHERE t.type = 'EXPENSE' " +
           "GROUP BY t.category")
    List<Object[]> getExpenseCategoryBreakdown();

    @Query("SELECT FUNCTION('TO_CHAR', t.date, 'YYYY-MM') as month, " +
           "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as totalIncome, " +
           "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as totalExpense " +
           "FROM Transaction t " +
           "GROUP BY FUNCTION('TO_CHAR', t.date, 'YYYY-MM') " +
           "ORDER BY month")
    List<Object[]> getCashFlow();
}
