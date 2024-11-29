package com.alok.config;

import com.alok.utils.Constants;
import com.alok.kafka.avro.model.Transaction;
import com.example.flink.Budget;
import com.example.flink.Category;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.flink.formats.avro.AvroDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

@SuppressWarnings({"deprecation", "unused"})
public class KafkaSources {

    public static FlinkKafkaConsumer<Transaction> getTransactionConsumer(Properties kafkaProps) {
        kafkaProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        return new FlinkKafkaConsumer<>(
                Constants.TRANSACTIONS_TOPIC,
                AvroDeserializationSchema.forSpecific(Transaction.class),
                kafkaProps
        );
    }

    public static FlinkKafkaConsumer<Category> getCategoryConsumer(Properties kafkaProps) {
        kafkaProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        return new FlinkKafkaConsumer<>(
                Constants.CATEGORIES_TOPIC,
                AvroDeserializationSchema.forSpecific(Category.class),
                kafkaProps
        );
    }

    public static FlinkKafkaConsumer<Budget> getCategoryBudgetConsumer(Properties kafkaProps) {
        kafkaProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        return new FlinkKafkaConsumer<>(
                Constants.CATEGORIES_BUDGET_TOPIC,
                AvroDeserializationSchema.forSpecific(Budget.class),
                kafkaProps
        );
    }
}
