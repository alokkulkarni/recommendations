package com.alok.utils;

import com.alok.dto.CategorisedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection", "unused"})
public class CategorisedTransactionFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(CategorisedTransactionFetcher.class);

    private static final String FETCH_QUERY =
            "SELECT transaction_id, customer_id, category, merchantCode, merchantName, transactionDesc, amount, amountType, timestamp FROM categorized_transactions WHERE processed = FALSE";

    public static List<CategorisedTransaction> fetchCategorisedTransactions() {
        List<CategorisedTransaction> transactions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(FETCH_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                CategorisedTransaction transaction = new CategorisedTransaction(
                        rs.getString("transaction_id"),
                        rs.getString("customer_id"),
                        rs.getString("category"),
                        rs.getString("merchantCode"),
                        rs.getString("merchantName"),
                        rs.getString("transactionDesc"),
                        rs.getDouble("amount"),
                        rs.getString("amountType"),
                        rs.getTimestamp("timestamp").getTime()
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
