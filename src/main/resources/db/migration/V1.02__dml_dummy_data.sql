INSERT INTO users (username, email) VALUES
    ('john_doe', 'john@example.com'),
    ('jane_smith', 'jane@example.com'),
    ('vitalii', 'vitalii@example.com');

INSERT INTO banks (name, code, description) VALUES
    ('Kredobank', 'kredobank', 'АТ "Кредобанк"'),
    ('Monobank', 'monobank', 'АТ "Універсал Банк"'),
    ('Privatbank', 'privatbank', 'АТ КБ "Приватбанк"');

INSERT INTO accounts (bank_account_id, title, description, currency_code, balance, credit_limit, iban, last_transaction_date, is_active, user_id, bank_id) VALUES
    ('154565654', 'Primary Checking Account', 'Main checking account for daily transactions', 980, 1000000, 500000, 'UA81*********************4321', '2023-10-01 12:00:00', TRUE, 1, 1),
    ('ssgfg_sgghdswr', 'Savings Account', 'High-interest savings account', 980, 5000000, 0, 'UA81*********************1234',  '2023-10-01 12:00:00', TRUE, 2, 2);

INSERT INTO cards (title, description, card_number, account_id) VALUES
    ('Visa Gold', 'Gold credit card with high limit', '************1111', 1),
    ('MasterCard Platinum', 'Platinum credit card with rewards', '************0004', 1),
    ('Visa Classic', 'Classic debit card', '************1112', 2),
    ('MasterCard Standard', 'Standard debit card', '************0005', 2);

INSERT INTO transaction_category_type (name) VALUES
    ('income'),
    ('outcome'),
    ('internal transfer');

INSERT INTO transaction_category (title, description, category_type_id) VALUES
    ('Salary', 'Income from employment', 1),
    ('Bonds', 'Income from bonds', 1),
    ('Dividends', 'Income from dividends', 1),
    ('Rent', 'Income from rental properties', 1),
    ('Freelance', 'Income from freelance work', 1),
    ('Groceries', 'Expenses for groceries', 2),
    ('Transfer', 'Internal transfer between accounts', 3);

INSERT INTO transactions (amount, description, action_type, category_id, account_id) VALUES
    (1500.00, 'Monthly salary', 'C', 1, 1),
    (-200.00, 'Grocery shopping', 'D', 2, 1),
    (500.00, 'Transfer to savings', 'T', 3, 1);

INSERT INTO goals (name, parent_id, description, start_date, end_date, created_by) VALUES
    ('Villa', null, 'Save money for Villa', '2023-01-01 00:00:00', '2025-12-31 23:59:59', 1),
    ('Buy a House', 1, 'Save money to buy a house', '2023-01-01 00:00:00', '2025-12-31 23:59:59', 1),
    ('Buy Land', 1, 'Save money to buy land', '2023-01-01 00:00:00', '2025-12-31 23:59:59', 1),
    ('Communications', 1, 'Save money to install communications', '2023-01-01 00:00:00', '2025-12-31 23:59:59', 1),
    ('Vacation', null, 'Save for a vacation', '2023-06-01 00:00:00', '2023-12-31 23:59:59', 2);

INSERT INTO goal_items (goal_id, name, description) VALUES
    (2, 'Base Price', 'Down payment for the house'),
    (2, 'Kitchen Price', 'Down payment for the kitchen'),
    (2, 'Floor Price', 'Down payment for the floor'),
    (3, 'Land Price', 'Down payment for the land price'),
    (4, 'Electricity', 'Down payment for the electricity'),
    (4, 'Water supply', 'Down payment for the water supply'),
    (5, 'Flight Tickets', 'Flight tickets for vacation');

INSERT INTO item_providers (name, contact_info) VALUES
    ('Provider A', 'contact@providerA.com'),
    ('Provider B', 'contact@providerB.com');

INSERT INTO goal_item_variations (goal_item_id, item_provider_id, price) VALUES
    (1, 1, 19500.00),
    (1, 2, 20000.00),
    (2, 1, 2900.00),
    (2, 2, 3000.00),
    (3, 1, 950.00),
    (3, 2, 1000.00),
    (4, 1, 9500.00),
    (4, 2, 10000.00),
    (5, 1, 950.00),
    (5, 2, 1000.00),
    (6, 1, 950.00),
    (6, 2, 1000.00),
    (7, 1, 950.00),
    (7, 2, 1000.00);

INSERT INTO tasks (name, description, goal_item_id, due_date, priority, status, created_date, created_by) VALUES
    ('Find Real Estate Agent', 'Find a reliable real estate agent', 1, '2023-02-01 00:00:00', 1, 'Pending', '2023-01-01 00:00:00', 'john_doe'),
    ('Book Flights', 'Book flight tickets for vacation', 2, '2023-07-01 00:00:00', 2, 'Pending', '2023-06-01 00:00:00', 'jane_smith')

