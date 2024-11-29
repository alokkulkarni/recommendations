package com.alok.utils;

import com.example.flink.Budget;
import com.example.flink.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryFetcher.class);

    private static final String FETCH_QUERY =
            "SELECT category, merchantName, merchantCode FROM category";

    public static List<Category> fetchBudgets() {
        List<Category> transactions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(FETCH_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Category transaction = new Category(
                        rs.getString("category"),
                        rs.getString("merchantName"),
                        rs.getString("merchantCode")
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
