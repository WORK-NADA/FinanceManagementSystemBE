-- ============================================================
-- STEP 1: Add user_id columns as NULLABLE first
-- ============================================================

ALTER TABLE stock_transactions ADD COLUMN user_id BIGINT NULL;
ALTER TABLE profit_distributions ADD COLUMN user_id BIGINT NULL;
ALTER TABLE document_sequences ADD COLUMN user_id BIGINT NULL;

-- ============================================================
-- STEP 2: Backfill
-- stock_transactions → derive from the related stock's user_id
-- profit_distributions, document_sequences → backfill to admin
-- ============================================================

UPDATE stock_transactions st
    JOIN stocks s ON st.stock_id = s.id
    SET st.user_id = s.user_id;

UPDATE profit_distributions SET user_id = (SELECT user_id FROM users WHERE role = 'ADMIN' LIMIT 1);
UPDATE document_sequences    SET user_id = (SELECT user_id FROM users WHERE role = 'ADMIN' LIMIT 1);

-- ============================================================
-- STEP 3: Make columns NOT NULL (using MySQL MODIFY COLUMN)
-- ============================================================

ALTER TABLE stock_transactions MODIFY COLUMN user_id BIGINT NOT NULL;
ALTER TABLE profit_distributions MODIFY COLUMN user_id BIGINT NOT NULL;
ALTER TABLE document_sequences MODIFY COLUMN user_id BIGINT NOT NULL;

-- ============================================================
-- STEP 4: Add FK constraints
-- ============================================================

ALTER TABLE stock_transactions
    ADD CONSTRAINT fk_st_user FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE profit_distributions
    ADD CONSTRAINT fk_pd_user FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE document_sequences
    ADD CONSTRAINT fk_ds_user FOREIGN KEY (user_id) REFERENCES users(user_id);

-- ============================================================
-- STEP 5: Drop old unique constraints and add new composite ones
-- ============================================================

-- ProfitDistribution: (from_date, to_date) → (user_id, from_date, to_date)
ALTER TABLE profit_distributions DROP INDEX uk_distribution_period;
ALTER TABLE profit_distributions
    ADD CONSTRAINT uk_distribution_user_period UNIQUE (user_id, from_date, to_date);

-- DocumentSequence: (document_type, year) → (user_id, document_type, year)
ALTER TABLE document_sequences DROP INDEX uk_document_type_year;
ALTER TABLE document_sequences
    ADD CONSTRAINT uk_seq_user_type_year UNIQUE (user_id, document_type, year);

-- StockTransaction: add user_id index
CREATE INDEX idx_stock_transaction_user ON stock_transactions (user_id);
