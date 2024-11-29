package com.alok.functions.source;

import com.alok.dto.CategorisedTransaction;
import com.alok.utils.CategorisedTransactionFetcher;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings({"deprecation", "BusyWait"})
public class CategorisedTransactionSource implements SourceFunction<CategorisedTransaction> {
    private static final Logger LOG = LoggerFactory.getLogger(CategorisedTransactionSource.class);
    private volatile boolean isRunning = true;

    @Override
    public void run(SourceContext<CategorisedTransaction> ctx) {
        while (isRunning) {
            try {
                List<CategorisedTransaction> transactions = CategorisedTransactionFetcher.fetchCategorisedTransactions();
                for (CategorisedTransaction transaction : transactions) {
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
