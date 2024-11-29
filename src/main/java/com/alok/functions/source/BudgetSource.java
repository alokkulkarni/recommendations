package com.alok.functions.source;

import com.alok.dto.BudgetComparison;
import com.alok.utils.BudgetComparisonFetcher;
import com.alok.utils.BudgetFetcher;
import com.example.flink.Budget;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BudgetSource implements SourceFunction<Budget> {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetSource.class);
    private volatile boolean isRunning = true;


    @Override
    public void run(SourceContext<Budget> ctx) throws Exception {
        while (isRunning) {
            try {
                List<Budget> transactions = BudgetFetcher.fetchBudgets();
                for (Budget transaction : transactions) {
                    ctx.collect(transaction);
                }
                Thread.sleep(60000); // Poll every 60 seconds
            } catch (InterruptedException e) {
                LOG.error("BudgetSource interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOG.error("Error in BudgetSource: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
