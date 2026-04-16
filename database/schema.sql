CREATE DATABASE IF NOT EXISTS canteen_management;
USE canteen_management;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    wallet_balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(120) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(20) NOT NULL DEFAULT 'FOOD',
    available BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    CONSTRAINT chk_menu_price CHECK (price >= 0),
    CONSTRAINT chk_menu_category CHECK (category IN ('FOOD', 'BEVERAGE'))
);

ALTER TABLE menu_items
    ADD COLUMN IF NOT EXISTS category VARCHAR(20) NOT NULL DEFAULT 'FOOD';

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_order_total CHECK (total_amount >= 0),
    CONSTRAINT chk_order_status CHECK (status IN ('CONFIRMED', 'PREPARING', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE IF NOT EXISTS order_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    line_total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_order_details_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_details_item FOREIGN KEY (item_id) REFERENCES menu_items(id),
    CONSTRAINT chk_order_quantity CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    balance_after DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_wallet_type CHECK (transaction_type IN ('CREDIT', 'DEBIT')),
    CONSTRAINT chk_wallet_amount CHECK (amount > 0)
);

USE canteen_management;

DELETE FROM users WHERE email = 'dhyan@canteen.com';

INSERT INTO users (name, email, password, role, wallet_balance)
VALUES ('Dhyan Admin', 'dhyan@canteen.com', 'dhyan', 'ADMIN', 0.00);

UPDATE users
SET role = 'USER'
WHERE role = 'ADMIN' AND email <> 'dhyan@canteen.com';
