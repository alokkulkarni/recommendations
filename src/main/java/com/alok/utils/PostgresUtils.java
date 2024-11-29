package com.alok.utils;


import com.alok.dto.BudgetComparison;
import com.alok.dto.CategorisedTransaction;
import com.alok.dto.HighValueTransactionInsight;
import com.alok.dto.Insight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.sql.DriverManager.getConnection;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection", "unused"})
public class PostgresUtils {


    private static final Logger LOG = LoggerFactory.getLogger(PostgresUtils.class);

    public static Timestamp convertToTimestamp(String timestampStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new Timestamp(dateFormat.parse(timestampStr).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing timestamp: " + timestampStr, e);
        }
    }

    public static void writeCategorizedTransaction(CategorisedTransaction transaction) {
        try (Connection connection = getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + Constants.CATEGORIZED_TRANSACTIONS_TABLE + " (transaction_id, customer_id, category, merchantCode, merchantName, transactionDesc, amount, amountType, timestamp)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            statement.setString(1, transaction.getTransactionId());
            statement.setString(2, transaction.getCustomerId());
            statement.setString(3, transaction.getCategory());
            statement.setString(4, transaction.getMerchantCode());
            statement.setString(5, transaction.getMerchantName());
            statement.setString(6, transaction.getTransactionDesc());
            statement.setDouble(7, transaction.getAmount());
            statement.setString(8, transaction.getAmountType());
            statement.setTimestamp(9, new Timestamp(transaction.getTimestamp()));
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to write categorized transaction to PostgreSQL: {}", e.getMessage(), e);
            throw new RuntimeException("PostgreSQL connection failed", e);
        }
    }

    public static void markTransactionAsProcessed(String transactionId) throws SQLException {
        String query = "UPDATE " + Constants.CATEGORIZED_TRANSACTIONS_TABLE + " SET processed = TRUE WHERE transaction_id = ?";
        try (Connection connection = getConnection(Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, transactionId);
            statement.executeUpdate();
        }
    }


    public static void writeBudgetComparison(BudgetComparison comparison) {
        try (Connection connection = getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO budget_comparisons (customer_id, category, month, year, spent_amount, budget, flag) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)" +
                            "ON CONFLICT (customer_id, category, month, year) DO UPDATE SET spent_amount = EXCLUDED.spent_amount, budget = EXCLUDED.budget, flag = EXCLUDED.flag"
            );
            statement.setString(1, comparison.getCustomerId());
            statement.setString(2, comparison.getCategory());
            statement.setInt(3, comparison.getMonth());
            statement.setInt(4, comparison.getYear());
            statement.setDouble(5, comparison.getSpentAmount());
            statement.setDouble(6, comparison.getBudget());
            statement.setString(7, comparison.getFlag());

            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to write budget comparison to PostgreSQL: {}", e.getMessage(), e);
            throw new RuntimeException("PostgreSQL connection failed", e);
        }
    }

    private static final String INSERT_QUERY =
            "INSERT INTO customer_insights (customer_id, category, month, year, insight_message, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)" +
                    "ON CONFLICT (customer_id, category, month, year) DO UPDATE SET insight_message = EXCLUDED.insight_message";

    public static void writeInsight(Insight insight) {
        try (Connection connection = getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(INSERT_QUERY);
            statement.setString(1, insight.getCustomerId());
            statement.setString(2, insight.getCategory());
            statement.setInt(3, insight.getMonth());
            statement.setInt(4, insight.getYear());
            statement.setString(5, insight.getInsightMessage());
            statement.setTimestamp(6, java.sql.Timestamp.valueOf(insight.getCreatedAt()));

            statement.executeUpdate();
        } catch (Exception e) {
            LOG.error("Failed to write insight to PostgreSQL: {}", e.getMessage(), e);
            throw new RuntimeException("PostgreSQL connection failed", e);
        }
    }

    private static final String HIGH_VALUE_TRANSACTION_INSERT_QUERY =
            "INSERT INTO high_value_transactions (customer_id, category, transaction_type, transaction_amount, time_period, period_date, insight_message, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static void writeInsight(HighValueTransactionInsight insight) {
        try (Connection connection = getConnection(
                Constants.POSTGRES_URL, Constants.POSTGRES_USER, Constants.POSTGRES_PASSWORD)) {

            PreparedStatement statement = connection.prepareStatement(HIGH_VALUE_TRANSACTION_INSERT_QUERY);
            statement.setString(1, insight.getCustomerId());
            statement.setString(2, insight.getCategory());
            statement.setString(3, insight.getTransactionType());
            statement.setDouble(4, insight.getTransactionAmount());
            statement.setString(5, insight.getTimePeriod());
            statement.setDate(6, java.sql.Date.valueOf(insight.getPeriodDate().toLocalDate()));
            statement.setString(7, insight.getInsightMessage());
            statement.setDate(8, java.sql.Date.valueOf(insight.getCreatedAt().toLocalDate()));

            statement.executeUpdate();
        } catch (Exception e) {
            LOG.error("Failed to write high-value insight to PostgreSQL: {}", e.getMessage(), e);
            throw new RuntimeException("PostgreSQL connection failed", e);
        }
    }
}
