package com.alok.utils;

import com.alok.dto.BudgetComparison;
import com.example.flink.Budget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BudgetFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetFetcher.class);

    private static final String FETCH_QUERY =
            "SELECT customer_id, category, monthly_budget FROM budget";

    public static List<Budget> fetchBudgets() {
        List<Budget> transactions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(FETCH_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Budget transaction = new Budget(
                        rs.getString("customer_id"),
                        rs.getString("category"),
                        rs.getDouble("monthly_budget")
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
