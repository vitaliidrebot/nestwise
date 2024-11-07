CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banks (
   id SERIAL PRIMARY KEY,
   code VARCHAR(20) NOT NULL,
   name VARCHAR(100) NOT NULL,
   description TEXT
);

CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    bank_account_id VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    currency_code INTEGER NOT NULL,
    balance BIGINT NOT NULL,
    credit_limit BIGINT,
    iban VARCHAR(34) NOT NULL,
    last_transaction_date TIMESTAMP,
    is_active BOOLEAN NOT NULL,
    user_id INTEGER,
    bank_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (bank_id) REFERENCES banks (id)
);

CREATE TABLE cards (
   id SERIAL PRIMARY KEY,
   title VARCHAR(100) NOT NULL,
   description TEXT,
   card_number VARCHAR(16) NOT NULL,
   account_id INTEGER,
   FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE transaction_category_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE CHECK (name IN ('income', 'outcome', 'internal transfer'))
);

CREATE TABLE transaction_category (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category_type_id INTEGER NOT NULL,
    FOREIGN KEY (category_type_id) REFERENCES transaction_category_type (id)
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    description TEXT,
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    action_type CHAR(1),
    category_id INTEGER,
    account_id INTEGER,
    FOREIGN KEY (category_id) REFERENCES transaction_category (id),
    FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE goals(
    id SERIAL PRIMARY KEY,
    parent_id INTEGER,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INTEGER NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users (id),
    FOREIGN KEY (parent_id) REFERENCES goals (id)
);

CREATE TABLE goal_items (
    id SERIAL PRIMARY KEY,
    goal_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (goal_id) REFERENCES goals (id)
);

CREATE TABLE item_providers (
   id SERIAL PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   contact_info TEXT,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE goal_item_variations (
    id SERIAL PRIMARY KEY,
    goal_item_id INTEGER NOT NULL,
    item_provider_id INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (goal_item_id) REFERENCES goal_items (id),
    FOREIGN KEY (item_provider_id) REFERENCES item_providers (id)
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    goal_item_id INT NOT NULL,
    due_date TIMESTAMP,
    priority INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    FOREIGN KEY (goal_item_id) REFERENCES goal_items (id)
);

CREATE TABLE currencies (
    code INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    currency_code CHAR(3)
);

CREATE TABLE countries (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    currency_code INTEGER,
    FOREIGN KEY (currency_code) REFERENCES currencies (code)
);

CREATE TABLE exchange_rates (
    id SERIAL PRIMARY KEY,
    bank_id INT NOT NULL,
    currency_code_from INT NOT NULL,
    currency_code_to INT NOT NULL,
    date DATE NOT NULL,
    rate_buy DECIMAL(10, 4),
    rate_sell DECIMAL(10, 4),
    FOREIGN KEY (currency_code_from) REFERENCES currencies (code),
    FOREIGN KEY (currency_code_to) REFERENCES currencies (code)
);
