package com.alok.functions;


import com.alok.dto.CategorisedTransaction;
import com.alok.kafka.avro.model.Transaction;
import com.alok.utils.CategoryFetcher;
import com.alok.utils.PostgresUtils;
import com.example.flink.Category;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused")
public class TransactionCategorizer extends KeyedBroadcastProcessFunction<String, Transaction, Category, CategorisedTransaction> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionCategorizer.class);


    private final MapStateDescriptor<String, Category> categoryStateDescriptor;
    private transient MapState<String, Category> categoryState;

    public TransactionCategorizer(MapStateDescriptor<String, Category> categoryStateDescriptor) {
        this.categoryStateDescriptor = categoryStateDescriptor;
    }

    private void loadCategoriesFromPostgres() {
        try {
            List<Category> catagories = CategoryFetcher.fetchBudgets();
            for (Category category : catagories) {
                String key = category.getMerchantCode().toString();
                categoryState.put(key, category);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load categories from PostgreSQL", e);
        }
    }

    @Override
    public void processBroadcastElement(Category category, Context ctx, Collector<CategorisedTransaction> out) throws Exception {
        try {
            System.out.println("Processing broadcast element: " + category.getMerchantCode() + " " + category.getCategory() + " " + category.getMerchantName());
            String key = category.getMerchantCode().toString();
            ctx.getBroadcastState(categoryStateDescriptor).put(key, category);
        } catch (Exception e) {
            LOG.error("Failed to update broadcast state with category: {}", e.getMessage(), e);
            throw new RuntimeException("Category broadcast update failed", e);
        }
    }

    @Override
    public void processElement(Transaction transaction, ReadOnlyContext ctx, Collector<CategorisedTransaction> out) throws Exception {
        try {
            categoryState = getRuntimeContext().getMapState(categoryStateDescriptor);
            loadCategoriesFromPostgres();

            System.out.println("Processing transaction: " + transaction.getTransactionId() + " " + transaction.getCustomerId() + " " + transaction.getMerchantCode() + " " + transaction.getTransactionDesc() + " " + transaction.getAmount() + " " + transaction.getAmountType() + " " + transaction.getTimestamp());
            ReadOnlyBroadcastState<String, Category> categories = ctx.getBroadcastState(categoryStateDescriptor);
            String key = transaction.getMerchantCode().toString();

            if (categories.contains(key)) {
                Category category = categories.get(transaction.getMerchantCode().toString());

                String categoryName = category.getCategory().toString();
                String merchantName = category.getMerchantName().toString();
                System.out.println("Categorizing transaction: " + transaction.getTransactionId() + " with category: " + categoryName + " and merchant: " + merchantName);

                CategorisedTransaction categorizedTransaction = new CategorisedTransaction(
                        transaction.getTransactionId().toString(),
                        transaction.getCustomerId().toString(),
                        categoryName,
                        transaction.getMerchantCode().toString(),
                        merchantName,
                        transaction.getTransactionDesc().toString(),
                        transaction.getAmount(),
                        transaction.getAmountType().toString(),
                        transaction.getTimestamp()
                );

                PostgresUtils.writeCategorizedTransaction(categorizedTransaction);
                out.collect(categorizedTransaction);
            } else if (categoryState.contains(key)) {
                Category category = categoryState.get(key);

                String categoryName = category.getCategory().toString();
                String merchantName = category.getMerchantName().toString();
                System.out.println("Categorizing transaction: " + transaction.getTransactionId() + " with category: " + categoryName + " and merchant: " + merchantName);

                CategorisedTransaction categorizedTransaction = new CategorisedTransaction(
                        transaction.getTransactionId().toString(),
                        transaction.getCustomerId().toString(),
                        categoryName,
                        transaction.getMerchantCode().toString(),
                        merchantName,
                        transaction.getTransactionDesc().toString(),
                        transaction.getAmount(),
                        transaction.getAmountType().toString(),
                        transaction.getTimestamp()
                );

                PostgresUtils.writeCategorizedTransaction(categorizedTransaction);
                out.collect(categorizedTransaction);
            }
        } catch (Exception e) {
            LOG.error("Failed to process transaction: {}", e.getMessage(), e);
            throw new RuntimeException("Transaction processing failed", e);
        }
    }
}
