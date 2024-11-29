package com.alok.functions;

import com.alok.dto.BudgetComparison;
import com.alok.dto.Insight;
import com.alok.utils.PostgresUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;

public class InsightGenerator extends RichFlatMapFunction<BudgetComparison, Insight> {

    @Override
    public void flatMap(BudgetComparison budgetComparison, Collector<Insight> out) {
        try {
            String insightMessage = generateInsightMessage(budgetComparison);

            Insight insight = new Insight(
                    budgetComparison.getCustomerId(),
                    budgetComparison.getCategory(),
                    budgetComparison.getMonth(),
                    budgetComparison.getYear(),
                    insightMessage
            );

            // Save insight to PostgreSQL
            PostgresUtils.writeInsight(insight);

            // Emit insight for downstream processing if needed
            out.collect(insight);
        } catch (Exception e) {
            throw new RuntimeException("Error generating insights", e);
        }
    }

    private String generateInsightMessage(BudgetComparison budgetComparison) {
        String message;
        if ("OVERSPENT".equals(budgetComparison.getFlag())) {
            message = String.format("You have overspent by %.2f in %s for %s.",
                    budgetComparison.getSpentAmount() - budgetComparison.getBudget(),
                    budgetComparison.getCategory(),
                    getMonthName(budgetComparison.getMonth()));
        } else {
            message = String.format("Great job! You saved %.2f in %s for %s.",
                    budgetComparison.getBudget() - budgetComparison.getSpentAmount(),
                    budgetComparison.getCategory(),
                    getMonthName(budgetComparison.getMonth()));
        }
        return message;
    }

    private String getMonthName(int month) {
        return java.time.Month.of(month).name();
    }
}
