***Project:9 Canteen Management System***

**Project Overview**

The Canteen Management System with Wallet Integration is a database-driven project designed to simplify food ordering and payment management within a college or organization canteen. The system allows users to place orders, manage their digital wallet, and track transactions efficiently.

This project focuses on implementing core DBMS concepts such as normalization, relationships, constraints, transactions, and secure data handling.

**Objectives**

	•	Digitize the canteen ordering process
	•	Reduce manual billing errors
	•	Enable cashless payments through a wallet system
	•	Maintain proper records of users, orders, and transactions

**Features**

**User Module**

	•	User registration and login
	•	View menu items
	•	Add items to cart
	•	Place orders
	•	Check wallet balance
	•	View transaction history

**Wallet System**
	
	•	Add money to wallet
	•	Automatic deduction after order confirmation
	•	Prevent orders if balance is insufficient
	•	Secure transaction logging

**Admin Module**

	•	Add, update, delete menu items
	•	Manage users
	•	View all orders
	•	Track daily sales and transactions

**Database Design**

**Main Tables**

	•	Users (UserID, Name, Email, Password, Role)
	•	Menu (ItemID, ItemName, Price, Availability)
	•	Orders (OrderID, UserID, Date, TotalAmount, Status)
	•	OrderDetails (OrderDetailID, OrderID, ItemID, Quantity)
	•	Wallet (WalletID, UserID, Balance)
	•	Transactions (TransactionID, WalletID, Amount, Type, Date)

**Concepts Used**

	•	Primary & Foreign Keys
	•	Entity-Relationship (ER) Model
	•	Normalization (up to 3NF)
	•	Constraints (NOT NULL, UNIQUE, CHECK)
	•	Transaction Management

**Technologies Used**

	•	Database: MySQL / PostgreSQL / SQL
	•	Backend: (Specify if used – e.g., PHP / Java / Python)
	•	Frontend: (Specify if used – e.g., HTML, CSS, JavaScript)

**System Workflow**
	
	1.	User registers and logs in.
	2.	User adds money to wallet.
	3.	User selects items from menu and places order.
	4.	System checks wallet balance.
	5.	If sufficient balance → amount deducted and order confirmed.
	6.	Transaction recorded in database.
