-- ============================================================================
-- Finance Management System - Complete Fresh Schema DDL (Aiven MySQL 8.0)
-- 
-- Database: Aiven MySQL (defaultdb or custom schema)
-- Generated for fresh database initialization with full support for:
--   - Core Business Entities (Users, Customers, Suppliers, Purchases, Sales)
--   - Stock Management & Inventory Tracking
--   - Financials (Purchase/Sale Payments, Expenses, Profit Distribution)
--   - Multi-Partner Profit Sharing & Withdrawals
--   - Investment Management Feature
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. USERS
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `owner_name` VARCHAR(255) NOT NULL,
    `username` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `viewable_password` VARCHAR(255) NULL,
    `mobile_number` VARCHAR(10) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `enabled` BIT(1) NOT NULL DEFAULT b'1',
    `account_non_locked` BIT(1) NOT NULL DEFAULT b'1',
    `account_non_expired` BIT(1) NOT NULL DEFAULT b'1',
    `credentials_non_expired` BIT(1) NOT NULL DEFAULT b'1',
    `failed_login_attempts` INT NOT NULL DEFAULT 0,
    `lock_time` DATETIME(6) NULL,
    `deleted` BIT(1) NOT NULL DEFAULT b'0',
    `first_login` BIT(1) NOT NULL DEFAULT b'1',
    `opening_balance` DECIMAL(15, 2) NULL DEFAULT 0.00,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_users_public_id` (`public_id`),
    UNIQUE KEY `uk_users_username` (`username`),
    UNIQUE KEY `uk_users_email` (`email`),
    UNIQUE KEY `uk_users_mobile_number` (`mobile_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. USER ADDRESS
CREATE TABLE IF NOT EXISTS `user_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `house_no` VARCHAR(20) NOT NULL,
    `society_name` VARCHAR(100) NOT NULL,
    `area` VARCHAR(100) NOT NULL,
    `city` VARCHAR(100) NOT NULL,
    `pincode` VARCHAR(10) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `country` VARCHAR(100) NOT NULL DEFAULT 'India',
    `user_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_address_user` (`user_id`),
    CONSTRAINT `fk_user_address_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. REFRESH TOKENS
CREATE TABLE IF NOT EXISTS `refresh_tokens` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `token` VARCHAR(500) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `expiry_date` DATETIME(6) NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refresh_tokens_token` (`token`),
    KEY `idx_refresh_tokens_user` (`user_id`),
    CONSTRAINT `fk_refresh_tokens_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. CUSTOMERS
CREATE TABLE IF NOT EXISTS `customers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `customer_name` VARCHAR(150) NOT NULL,
    `mobile_number` VARCHAR(15) NOT NULL,
    `contact_person` VARCHAR(100) NULL,
    `alternate_mobile_number` VARCHAR(15) NULL,
    `email` VARCHAR(150) NULL,
    `gst_number` VARCHAR(15) NULL,
    `user_id` BIGINT NOT NULL,
    `opening_balance` DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    `payment_terms` INT NOT NULL DEFAULT 30,
    `is_active` BIT(1) NOT NULL DEFAULT b'1',
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_customers_public_id` (`public_id`),
    UNIQUE KEY `uk_customer_user_mobile` (`user_id`, `mobile_number`),
    UNIQUE KEY `uk_customer_user_email` (`user_id`, `email`),
    UNIQUE KEY `uk_customer_user_gst` (`user_id`, `gst_number`),
    KEY `idx_customer_name` (`customer_name`),
    KEY `idx_customer_mobile` (`mobile_number`),
    KEY `idx_customer_gst` (`gst_number`),
    KEY `idx_customer_active` (`is_active`),
    KEY `idx_customer_user` (`user_id`),
    CONSTRAINT `fk_customers_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. CUSTOMER ADDRESS
CREATE TABLE IF NOT EXISTS `customer_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `customer_id` BIGINT NOT NULL,
    `address_line_1` VARCHAR(150) NOT NULL,
    `address_line_2` VARCHAR(150) NULL,
    `city` VARCHAR(100) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `country` VARCHAR(100) NOT NULL DEFAULT 'India',
    `pincode` VARCHAR(6) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_customer_address_customer` (`customer_id`),
    KEY `idx_customer_address_city` (`city`),
    KEY `idx_customer_address_pincode` (`pincode`),
    CONSTRAINT `fk_customer_address_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. SUPPLIERS
CREATE TABLE IF NOT EXISTS `suppliers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `supplier_name` VARCHAR(150) NOT NULL,
    `mobile_number` VARCHAR(15) NOT NULL,
    `contact_person` VARCHAR(100) NULL,
    `alternate_mobile_number` VARCHAR(15) NULL,
    `email` VARCHAR(150) NULL,
    `gst_number` VARCHAR(15) NULL,
    `opening_balance` DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    `payment_terms` INT NULL DEFAULT 30,
    `user_id` BIGINT NOT NULL,
    `is_active` BIT(1) NOT NULL DEFAULT b'1',
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_suppliers_public_id` (`public_id`),
    UNIQUE KEY `uk_supplier_user_mobile` (`user_id`, `mobile_number`),
    UNIQUE KEY `uk_supplier_user_gst` (`user_id`, `gst_number`),
    UNIQUE KEY `uk_supplier_user_email` (`user_id`, `email`),
    KEY `idx_supplier_name` (`supplier_name`),
    KEY `idx_supplier_active` (`is_active`),
    KEY `idx_supplier_user` (`user_id`),
    CONSTRAINT `fk_suppliers_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. SUPPLIER ADDRESS
CREATE TABLE IF NOT EXISTS `supplier_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `supplier_id` BIGINT NOT NULL,
    `address_line_1` VARCHAR(150) NOT NULL,
    `address_line_2` VARCHAR(150) NULL,
    `city` VARCHAR(100) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `country` VARCHAR(100) NOT NULL DEFAULT 'India',
    `pincode` VARCHAR(6) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supplier_address_supplier` (`supplier_id`),
    CONSTRAINT `fk_supplier_address_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. PURCHASES
CREATE TABLE IF NOT EXISTS `purchases` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `supplier_id` BIGINT NOT NULL,
    `raw_material` VARCHAR(150) NOT NULL,
    `weight` DECIMAL(15, 3) NOT NULL,
    `unit` VARCHAR(20) NOT NULL DEFAULT 'KG',
    `rate_per_unit` DECIMAL(15, 2) NOT NULL,
    `gst_percentage` DECIMAL(5, 2) NOT NULL DEFAULT 18.00,
    `amount` DECIMAL(15, 2) NOT NULL,
    `gst_amount` DECIMAL(15, 2) NOT NULL,
    `total_amount` DECIMAL(15, 2) NOT NULL,
    `purchase_number` VARCHAR(30) NOT NULL,
    `supplier_invoice_number` VARCHAR(50) NULL,
    `purchase_date` DATE NOT NULL,
    `purchase_status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_purchases_public_id` (`public_id`),
    UNIQUE KEY `uk_purchase_user_number` (`user_id`, `purchase_number`),
    KEY `idx_purchase_supplier` (`supplier_id`),
    KEY `idx_purchase_date` (`purchase_date`),
    KEY `idx_purchase_supplier_invoice` (`supplier_invoice_number`),
    KEY `idx_purchase_status` (`purchase_status`),
    KEY `idx_purchase_payment_status` (`payment_status`),
    KEY `idx_purchase_raw_material` (`raw_material`),
    KEY `idx_purchase_user` (`user_id`),
    CONSTRAINT `fk_purchases_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_purchases_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. PURCHASE PAYMENTS
CREATE TABLE IF NOT EXISTS `purchase_payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `purchase_id` BIGINT NOT NULL,
    `amount_paid` DECIMAL(15, 2) NOT NULL,
    `payment_date` DATE NOT NULL,
    `payment_mode` VARCHAR(20) NOT NULL,
    `payment_number` VARCHAR(30) NOT NULL,
    `reference_number` VARCHAR(100) NULL,
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_purchase_payments_public_id` (`public_id`),
    UNIQUE KEY `uk_pur_pay_user_num` (`user_id`, `payment_number`),
    KEY `idx_payment_purchase` (`purchase_id`),
    KEY `idx_payment_date` (`payment_date`),
    KEY `idx_payment_user` (`user_id`),
    CONSTRAINT `fk_purchase_payments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_purchase_payments_purchase` FOREIGN KEY (`purchase_id`) REFERENCES `purchases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. SALES
CREATE TABLE IF NOT EXISTS `sales` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `raw_material` VARCHAR(100) NOT NULL,
    `weight` DECIMAL(15, 3) NOT NULL,
    `unit` VARCHAR(20) NOT NULL DEFAULT 'KG',
    `rate_per_unit` DECIMAL(15, 2) NOT NULL,
    `gst_percentage` DECIMAL(5, 2) NOT NULL DEFAULT 18.00,
    `amount` DECIMAL(15, 2) NOT NULL,
    `gst_amount` DECIMAL(15, 2) NOT NULL,
    `total_amount` DECIMAL(15, 2) NOT NULL,
    `sale_number` VARCHAR(30) NOT NULL,
    `customer_invoice_number` VARCHAR(50) NULL,
    `sale_date` DATE NOT NULL,
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sales_public_id` (`public_id`),
    UNIQUE KEY `uk_sale_user_number` (`user_id`, `sale_number`),
    KEY `idx_sale_customer` (`customer_id`),
    KEY `idx_sale_date` (`sale_date`),
    KEY `idx_sale_invoice` (`customer_invoice_number`),
    KEY `idx_sale_user` (`user_id`),
    CONSTRAINT `fk_sales_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_sales_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. SALE PAYMENTS
CREATE TABLE IF NOT EXISTS `sale_payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `sale_id` BIGINT NOT NULL,
    `amount_received` DECIMAL(15, 2) NOT NULL,
    `payment_date` DATE NOT NULL,
    `payment_mode` VARCHAR(20) NOT NULL,
    `payment_number` VARCHAR(30) NOT NULL,
    `reference_number` VARCHAR(100) NULL,
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sale_payments_public_id` (`public_id`),
    UNIQUE KEY `uk_sale_payment_user_number` (`user_id`, `payment_number`),
    KEY `idx_sale_payment_sale` (`sale_id`),
    KEY `idx_sale_payment_date` (`payment_date`),
    KEY `idx_sale_payment_user` (`user_id`),
    CONSTRAINT `fk_sale_payments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_sale_payments_sale` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. EXPENSES
CREATE TABLE IF NOT EXISTS `expenses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(15, 2) NOT NULL,
    `expense_date` DATE NOT NULL,
    `payment_mode` VARCHAR(20) NOT NULL,
    `expense_number` VARCHAR(30) NOT NULL,
    `reference_number` VARCHAR(100) NULL,
    `description` VARCHAR(255) NULL,
    `remarks` VARCHAR(500) NULL,
    `is_active` BIT(1) NOT NULL DEFAULT b'1',
    `user_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_expenses_public_id` (`public_id`),
    UNIQUE KEY `uk_expense_user_number` (`user_id`, `expense_number`),
    KEY `idx_expense_category` (`category`),
    KEY `idx_expense_date` (`expense_date`),
    KEY `idx_expense_active` (`is_active`),
    KEY `idx_expense_user` (`user_id`),
    CONSTRAINT `fk_expenses_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. STOCKS
CREATE TABLE IF NOT EXISTS `stocks` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `raw_material` VARCHAR(150) NOT NULL,
    `unit` VARCHAR(20) NOT NULL DEFAULT 'KG',
    `current_quantity` DECIMAL(15, 3) NOT NULL DEFAULT 0.000,
    `minimum_stock_level` DECIMAL(15, 3) NOT NULL DEFAULT 0.000,
    `is_active` BIT(1) NOT NULL DEFAULT b'1',
    `user_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `version` BIGINT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stocks_public_id` (`public_id`),
    UNIQUE KEY `uk_stock_user_raw_mat_unit` (`user_id`, `raw_material`, `unit`),
    KEY `idx_stock_raw_material` (`raw_material`),
    KEY `idx_stock_unit` (`unit`),
    KEY `idx_stock_active` (`is_active`),
    KEY `idx_stock_user` (`user_id`),
    CONSTRAINT `fk_stocks_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. STOCK TRANSACTIONS
CREATE TABLE IF NOT EXISTS `stock_transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `stock_id` BIGINT NOT NULL,
    `transaction_type` VARCHAR(30) NOT NULL,
    `quantity` DECIMAL(15, 3) NOT NULL,
    `unit` VARCHAR(20) NOT NULL,
    `reference_number` VARCHAR(50) NOT NULL,
    `transaction_date` DATETIME(6) NOT NULL,
    `remarks` VARCHAR(500) NULL,
    `created_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stock_transactions_public_id` (`public_id`),
    UNIQUE KEY `uk_stock_reference_type_user` (`user_id`, `reference_number`, `transaction_type`),
    KEY `idx_stk_tx_stock` (`stock_id`),
    KEY `idx_stk_tx_stock_date` (`stock_id`, `transaction_date`),
    KEY `idx_stk_tx_type` (`transaction_type`),
    KEY `idx_stk_tx_reference` (`reference_number`),
    KEY `idx_stk_tx_date` (`transaction_date`),
    KEY `idx_stk_tx_user` (`user_id`),
    CONSTRAINT `fk_stock_transactions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_stock_transactions_stock` FOREIGN KEY (`stock_id`) REFERENCES `stocks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. DOCUMENT SEQUENCES
CREATE TABLE IF NOT EXISTS `document_sequences` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `document_type` VARCHAR(30) NOT NULL,
    `doc_year` INT NOT NULL,
    `current_number` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_seq_user_type_year` (`user_id`, `document_type`, `doc_year`),
    KEY `idx_document_sequence_user` (`user_id`),
    CONSTRAINT `fk_document_sequences_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. PARTNERS
CREATE TABLE IF NOT EXISTS `partners` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `partner_name` VARCHAR(150) NOT NULL,
    `mobile_number` VARCHAR(15) NOT NULL,
    `email` VARCHAR(150) NULL,
    `share_percentage` DECIMAL(5, 2) NOT NULL,
    `joining_date` DATE NOT NULL,
    `is_active` BIT(1) NOT NULL DEFAULT b'1',
    `user_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_partners_public_id` (`public_id`),
    UNIQUE KEY `uk_partner_user_mobile` (`user_id`, `mobile_number`),
    KEY `idx_partner_active` (`is_active`),
    KEY `idx_partner_user` (`user_id`),
    CONSTRAINT `fk_partners_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. PROFIT DISTRIBUTIONS
CREATE TABLE IF NOT EXISTS `profit_distributions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `from_date` DATE NOT NULL,
    `to_date` DATE NOT NULL,
    `total_revenue` DECIMAL(15, 2) NOT NULL,
    `total_purchase_cost` DECIMAL(15, 2) NOT NULL,
    `total_expenses` DECIMAL(15, 2) NOT NULL,
    `net_profit` DECIMAL(15, 2) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_profit_distributions_public_id` (`public_id`),
    UNIQUE KEY `uk_distribution_user_period` (`user_id`, `from_date`, `to_date`),
    KEY `idx_profit_distribution_user` (`user_id`),
    CONSTRAINT `fk_profit_distributions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. PARTNER PROFIT SHARES
CREATE TABLE IF NOT EXISTS `partner_profit_shares` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `distribution_id` BIGINT NOT NULL,
    `partner_id` BIGINT NOT NULL,
    `share_percentage_at_distribution` DECIMAL(5, 2) NOT NULL,
    `share_amount` DECIMAL(15, 2) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_partner_profit_shares_public_id` (`public_id`),
    KEY `idx_share_distribution` (`distribution_id`),
    KEY `idx_share_partner` (`partner_id`),
    CONSTRAINT `fk_partner_profit_shares_distribution` FOREIGN KEY (`distribution_id`) REFERENCES `profit_distributions` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_partner_profit_shares_partner` FOREIGN KEY (`partner_id`) REFERENCES `partners` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. PARTNER PROFIT WITHDRAWALS
CREATE TABLE IF NOT EXISTS `partner_profit_withdrawals` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `partner_id` BIGINT NOT NULL,
    `withdrawal_date` DATE NOT NULL,
    `amount` DECIMAL(15, 2) NOT NULL,
    `available_before_withdrawal` DECIMAL(15, 2) NOT NULL,
    `remaining_after_withdrawal` DECIMAL(15, 2) NOT NULL,
    `payment_method` VARCHAR(50) NULL,
    `reference_number` VARCHAR(100) NULL,
    `notes` VARCHAR(500) NULL,
    `created_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_partner_profit_withdrawals_public_id` (`public_id`),
    KEY `idx_withdrawal_user` (`user_id`),
    KEY `idx_withdrawal_partner` (`partner_id`),
    KEY `idx_withdrawal_date` (`withdrawal_date`),
    CONSTRAINT `fk_partner_profit_withdrawals_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_partner_profit_withdrawals_partner` FOREIGN KEY (`partner_id`) REFERENCES `partners` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. INVESTMENTS
CREATE TABLE IF NOT EXISTS `investments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `public_id` BINARY(16) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `partner_id` BIGINT NOT NULL,
    `amount` DECIMAL(15, 2) NOT NULL,
    `investment_date` DATE NOT NULL,
    `description` VARCHAR(500) NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_investments_public_id` (`public_id`),
    KEY `idx_investment_user` (`user_id`),
    KEY `idx_investment_partner` (`partner_id`),
    KEY `idx_investment_date` (`investment_date`),
    CONSTRAINT `fk_investments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    CONSTRAINT `fk_investments_partner` FOREIGN KEY (`partner_id`) REFERENCES `partners` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 21. DOCUMENT SEQUENCES
CREATE TABLE IF NOT EXISTS `document_sequences` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `document_type` VARCHAR(30) NOT NULL,
    `doc_year` INT NOT NULL,
    `current_number` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_seq_user_type_year` (`user_id`, `document_type`, `doc_year`),
    CONSTRAINT `fk_doc_seq_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- End of Schema Definition
-- ============================================================================
