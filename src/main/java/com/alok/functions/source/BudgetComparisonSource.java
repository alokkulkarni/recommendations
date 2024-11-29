package com.alok.functions.source;

import com.alok.dto.BudgetComparison;
import com.alok.utils.BudgetComparisonFetcher;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings({"deprecation", "BusyWait", "unused"})
public class BudgetComparisonSource implements SourceFunction<BudgetComparison> {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetComparisonSource.class);
    private volatile boolean isRunning = true;


    @Override
    public void run(SourceContext<BudgetComparison> ctx) throws Exception {
        while (isRunning) {
            try {
                List<BudgetComparison> transactions = BudgetComparisonFetcher.fetchBudgetComparison();
                for (BudgetComparison transaction : transactions) {
                    ctx.collect(transaction);
                }
                Thread.sleep(60000); // Poll every 60 seconds
            } catch (InterruptedException e) {
                LOG.error("CategorisedTransactionSource interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOG.error("Error in CategorisedTransactionSource: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
