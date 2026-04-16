USE canteen_management;

INSERT INTO users (name, email, password, role, wallet_balance)
VALUES
    ('Dhyan Admin', 'dhyan@canteen.com', 'dhyan', 'ADMIN', 0.00),
    ('Student One', 'student@canteen.com', 'password123', 'USER', 500.00);

INSERT INTO menu_items (item_name, price, category, available, description)
VALUES
    ('Veg Sandwich', 45.00, 'FOOD', TRUE, 'Fresh sandwich with vegetables and chutney'),
    ('Masala Dosa', 70.00, 'FOOD', TRUE, 'South Indian dosa with potato masala'),
    ('Cold Coffee', 60.00, 'BEVERAGE', TRUE, 'Chilled coffee with milk'),
    ('Fruit Bowl', 55.00, 'FOOD', TRUE, 'Seasonal fruit serving');
