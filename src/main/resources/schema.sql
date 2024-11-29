
CREATE TABLE categorized_transactions (
                                         transaction_id VARCHAR(255) NOT NULL,      -- Corresponds to "transactionId"
                                         customer_id VARCHAR(255) NOT NULL,        -- Corresponds to "customerId"
                                         category VARCHAR(255) NOT NULL,           -- Corresponds to "category"
                                         merchantCode VARCHAR(50) NOT NULL,       -- Corresponds to "merchantCode"
                                         merchantName VARCHAR(255),               -- Corresponds to "merchantName"
                                         transactionDesc TEXT,                    -- Corresponds to "transactionDesc"
                                         amount NUMERIC(15, 2) NOT NULL,-- Corresponds to "amount"
                                         amountType VARCHAR(10) NOT NULL,         -- Corresponds to "amountType"
                                         timestamp TIMESTAMP NOT NULL , -- Corresponds to "timestamp"
                                         processed BOOLEAN DEFAULT FALSE        -- Corresponds to "processed"
);

CREATE TABLE budget_comparisons (
                                    id SERIAL PRIMARY KEY,
                                    customer_id VARCHAR(255),
                                    category VARCHAR(255),
                                    month INT,
                                    year INT,
                                    spent_amount DOUBLE PRECISION,
                                    budget DOUBLE PRECISION,
                                    flag VARCHAR(10), -- Values: "OVERSPENT", "UNDERSPENT"
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE(customer_id, category, month, year)
);


CREATE TABLE customer_insights (
                                   id SERIAL PRIMARY KEY,
                                   customer_id VARCHAR(255),
                                   category VARCHAR(255),
                                   month INT,
                                   year INT,
                                   insight_message TEXT,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE(customer_id, category, month, year)
);

CREATE TABLE high_value_transaction_insights (
                                                 id SERIAL PRIMARY KEY,
                                                 customer_id VARCHAR(255),
                                                 category VARCHAR(255),
                                                 transaction_type VARCHAR(10), -- "DEBIT" or "CREDIT"
                                                 transaction_amount DOUBLE PRECISION,
                                                 time_period VARCHAR(20), -- "DAILY", "MONTHLY", or "YEARLY"
                                                 period_date DATE, -- Relevant date for the time period
                                                 insight_message TEXT,
                                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE budget (
                        customer_id VARCHAR(255) NOT NULL,       -- Corresponds to "customerId" in Avro schema
                        category VARCHAR(255) NOT NULL,          -- Corresponds to "category" in Avro schema
                        monthly_budget NUMERIC(15, 2) NOT NULL  -- Corresponds to "monthlyBudget" in Avro schema
);

CREATE TABLE category (
                        category VARCHAR(255) NOT NULL,       -- Corresponds to "customerId" in Avro schema
                        merchantName VARCHAR(255) NOT NULL,          -- Corresponds to "category" in Avro schema
                        merchantCode VARCHAR(255) NOT NULL  -- Corresponds to "monthlyBudget" in Avro schema
);


