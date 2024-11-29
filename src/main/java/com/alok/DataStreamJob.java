/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alok;


import com.alok.config.KafkaConfig;
import com.alok.config.KafkaSources;
import com.alok.dto.BudgetComparison;
import com.alok.dto.CategorisedTransaction;
import com.alok.dto.Insight;
import com.alok.functions.*;
import com.alok.functions.source.BudgetComparisonSource;
import com.alok.functions.source.CategorisedTransactionSource;
import com.alok.kafka.avro.model.Transaction;
import com.example.flink.Budget;
import com.example.flink.Category;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.ZoneId;

// The main class that creates the Flink job and executes it. This class is responsible for setting up the Flink environment, creating the streams, and executing the job.
// The main method creates a StreamExecutionEnvironment and a StreamTableEnvironment, sets the local time zone to UTC, and enables checkpointing.
// It then creates state descriptors for categories, merchants, and budgets, and creates a stream of transactions from Kafka.
// The stream is connected to a broadcast stream of categories, and a TransactionCategorizer process function is applied to categorize the transactions.
// A stream of categorised transactions is created from a custom source that polls a database every 60 seconds for new transactions.
// The stream is connected to a broadcast stream of budgets, and a BudgetComparisonProcessor process function is applied to compare the transactions to the budgets.
// Finally, the job is executed with the name "Transaction Categorization Application".

@SuppressWarnings({"deprecation", "unused"})
public class DataStreamJob {

	private static final Logger LOG = LoggerFactory.getLogger(DataStreamJob.class);

	public static void main(String[] args) throws Exception {
		try {

			// Sets up the execution environment, which is the main entry point
			// to building Flink applications.
			final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
			tableEnv.getConfig().setLocalTimeZone(ZoneId.of("UTC"));
			env.enableCheckpointing(10000);

			// Create a state descriptor for categories
			MapStateDescriptor<String, Category> categoryStateDescriptor = new MapStateDescriptor<>(
					"categories",
					String.class,
					Category.class
			);

			// Create a state descriptor for budgets
			MapStateDescriptor<String, Budget> budgetStateDescriptor = new MapStateDescriptor<>(
					"budgets",
					String.class,
					Budget.class
			);


			// Create a stream of transactions from Kafka
			DataStream<Transaction> transactionStream = env.addSource(KafkaSources.getTransactionConsumer(KafkaConfig.getConsumerConfig("transactions-group")));
			KeyedStream<Transaction, String> keyedTransactionStream = transactionStream.keyBy(transaction -> transaction.getMerchantCode().toString());
			BroadcastStream<Category> categoryStream = env.addSource(KafkaSources.getCategoryConsumer(KafkaConfig.getConsumerConfig("categories-group"))).broadcast(categoryStateDescriptor);
			keyedTransactionStream
					.connect(categoryStream)
					.process(new TransactionCategorizer(categoryStateDescriptor));




			// Create a stream of categorised transactions from a custom source that polls a database every 60 seconds for new transactions.
			DataStream<CategorisedTransaction> budgetTransactionStream = env.addSource(new CategorisedTransactionSource());
			KeyedStream<CategorisedTransaction, String> keyedStream = budgetTransactionStream.keyBy(transaction -> transaction.getCustomerId() + "_" + transaction.getCategory());
			BroadcastStream<Budget> budgetBroadcastStream = env.addSource(KafkaSources.getCategoryBudgetConsumer(KafkaConfig.getConsumerConfig("budgets-group"))).broadcast(budgetStateDescriptor);
			keyedStream
					.connect(budgetBroadcastStream)
					.process(new BudgetComparisonProcessor(budgetStateDescriptor));


			// Create a stream of budget comparisons from a custom source that polls a database every 60 seconds for new budget comparisons.
			DataStream<BudgetComparison> budgetComparisonStream = env.addSource(new BudgetComparisonSource());
			DataStream<Insight> insightStream = budgetComparisonStream.flatMap(new InsightGenerator());

			// Execute program, beginning computation.
			env.execute("Transaction Categorization Application");

		} catch (Exception e) {
			LOG.error("Application failed to start: {}", e.getMessage(), e);
			throw new RuntimeException("Application execution failed", e);
		}
	}
}
