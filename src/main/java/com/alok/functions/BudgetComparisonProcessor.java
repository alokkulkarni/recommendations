package com.alok.functions;

import com.alok.dto.BudgetComparison;
import com.alok.dto.CategorisedTransaction;
import com.alok.utils.BudgetFetcher;
import com.alok.utils.PostgresUtils;
import com.example.flink.Budget;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "DuplicatedCode"})
public class BudgetComparisonProcessor extends KeyedBroadcastProcessFunction<String,CategorisedTransaction, Budget, BudgetComparison> {
    private final MapStateDescriptor<String, Budget> budgetStateDescriptor;
    private transient MapState<String, Budget> budgetState;

    private final Map<String, Double> aggregatedSpends = new HashMap<>();

    public BudgetComparisonProcessor(MapStateDescriptor<String, Budget> budgetStateDescriptor) {
        this.budgetStateDescriptor = budgetStateDescriptor;
    }

    private void loadBudgetsFromPostgres() {
        try {
            List<Budget> budgets = BudgetFetcher.fetchBudgets();
            for (Budget budget : budgets) {
                String key = budget.getCustomerId().toString() + "_" + budget.getCategory().toString();
                budgetState.put(key, budget);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load budgets from PostgreSQL", e);
        }
    }

    @Override
    public void processBroadcastElement(Budget budget, Context ctx, Collector<BudgetComparison> out) {
        try {
            String key = budget.getCustomerId() + "_" + budget.getCategory();
            ctx.getBroadcastState(budgetStateDescriptor).put(key, budget);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process budget update", e);
        }
    }

    @Override
    public void processElement(CategorisedTransaction transaction, ReadOnlyContext ctx, Collector<BudgetComparison> out) {
        try {
            budgetState = getRuntimeContext().getMapState(budgetStateDescriptor);
            loadBudgetsFromPostgres();
            ReadOnlyBroadcastState<String, Budget> budgets = ctx.getBroadcastState(budgetStateDescriptor);
//            LocalDate transactionDate = LocalDate.parse(String.valueOf(transaction.getTimestamp()));
            LocalDate transactionDate = Instant.ofEpochMilli(transaction.getTimestamp())
                    .atZone(ZoneId.systemDefault()) // Use system's default timezone
                    .toLocalDate();
            String key = transaction.getCustomerId() + "_" + transaction.getCategory();

            if (budgets.contains(key)) {
                String aggregationKey = generateAggregationKey(transaction.getCustomerId(), transaction.getCategory(), transactionDate);

                // Aggregate transaction amount
                aggregatedSpends.put(
                        aggregationKey,
                        aggregatedSpends.getOrDefault(aggregationKey, 0.0) + transaction.getAmount()
                );

                Budget budget = budgets.get(key);
                double totalSpent = aggregatedSpends.get(aggregationKey);
                double budgetTillDate = computeBudgetTillDate(budget.getMonthlyBudget(), transactionDate);

                String flag = totalSpent > budgetTillDate ? "OVERSPENT" : "UNDERSPENT";

                BudgetComparison comparison = new BudgetComparison(
                        transaction.getCustomerId(),
                        transaction.getCategory(),
                        transactionDate.getMonthValue(),
                        transactionDate.getYear(),
                        totalSpent,
                        budgetTillDate,
                        flag
                );

                PostgresUtils.writeBudgetComparison(comparison);
                PostgresUtils.markTransactionAsProcessed(transaction.getTransactionId());
                out.collect(comparison);
            } else if (budgetState.contains(key)) {
                String aggregationKey = generateAggregationKey(transaction.getCustomerId(), transaction.getCategory(), transactionDate);

                // Aggregate transaction amount
                aggregatedSpends.put(
                        aggregationKey,
                        aggregatedSpends.getOrDefault(aggregationKey, 0.0) + transaction.getAmount()
                );

                Budget budget = budgetState.get(key);
                double totalSpent = aggregatedSpends.get(aggregationKey);
                double budgetTillDate = computeBudgetTillDate(budget.getMonthlyBudget(), transactionDate);

                String flag = totalSpent > budgetTillDate ? "OVERSPENT" : "UNDERSPENT";

                BudgetComparison comparison = new BudgetComparison(
                        transaction.getCustomerId(),
                        transaction.getCategory(),
                        transactionDate.getMonthValue(),
                        transactionDate.getYear(),
                        totalSpent,
                        budgetTillDate,
                        flag
                );

                PostgresUtils.writeBudgetComparison(comparison);
                PostgresUtils.markTransactionAsProcessed(transaction.getTransactionId());
                out.collect(comparison);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process categorised transaction", e);
        }
    }

    private String generateAggregationKey(String customerId, String category, LocalDate transactionDate) {
        return customerId + "_" + category + "_" + transactionDate.getMonthValue() + "_" + transactionDate.getYear();
    }

    private double computeBudgetTillDate(double monthlyBudget, LocalDate transactionDate) {
        LocalDate startOfMonth = transactionDate.withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        int daysElapsed = today.isBefore(transactionDate) ? today.getDayOfMonth() : transactionDate.getDayOfMonth();
        int daysInMonth = transactionDate.lengthOfMonth();

        // Calculate the prorated budget till the date
        return (monthlyBudget / daysInMonth) * daysElapsed;
    }
}