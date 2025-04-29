CREATE TABLE IF NOT EXISTS currency (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL,
    rate_to_base DECIMAL(15,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS account (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    currency_id INT,
    start_balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (currency_id) REFERENCES currency(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    timestamp DATE NOT NULL,
    category_id INT,
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS external_transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    external_transaction_id INT,
    source_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transaction(id) ON DELETE CASCADE,
    FOREIGN KEY (external_transaction_id) REFERENCES external_transaction(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS recurring_transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    category_id INT,
    amount DECIMAL(15,2) NOT NULL,
    interval_type VARCHAR(20) NOT NULL, -- DAILY, WEEKLY, MONTHLY
    next_execution_date DATE NOT NULL,
    end_date DATE,
    description VARCHAR(255),
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS goal (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    target_amount DECIMAL(19,2) NOT NULL,
    target_date DATE,
    account_id INT NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id)
);
