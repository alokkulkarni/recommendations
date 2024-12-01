package com.alok.utils;

import com.alok.dto.BudgetComparison;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection", "unused"})
public class BudgetComparisonFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(BudgetComparisonFetcher.class);

    private static final String FETCH_QUERY =
            "SELECT customer_id, category, month, year, spent_amount, budget, actual_monthly_budget, flag FROM budget_comparisons";

    public static List<BudgetComparison> fetchBudgetComparison() {
        List<BudgetComparison> transactions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(FETCH_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                BudgetComparison transaction = new BudgetComparison(
                        rs.getString("customer_id"),
                        rs.getString("category"),
                        rs.getInt("month"),
                        rs.getInt("year"),
                        rs.getDouble("spent_amount"),
                        rs.getDouble("budget"),
                        rs.getDouble("actual_monthly_budget"),
                        rs.getString("flag")
                );
                transactions.add(transaction);
            }
        } catch (Exception e) {
            LOG.error("Failed to fetch categorised transactions from PostgreSQL: {}", e.getMessage(), e);
            throw new RuntimeException("PostgreSQL connection failed", e);
        }

        return transactions;
    }
}
