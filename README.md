# CanteenManagement

This workspace contains a full-stack starter for a canteen management system with wallet integration:

- `backend/`: Spring Boot + Java + MariaDB API for auth, menu, wallet, orders, and admin reporting
- `frontend/`: Vite + React UI for users and admins
- `database/`: MariaDB schema and sample seed data

## Core Modules

- User registration and login
- Wallet top-up and transaction history
- Food and beverage browsing with cart-based ordering
- Wallet deduction during order placement
- Admin menu and beverage management, user listing, order view, and daily sales summary

## Backend Run

1. Create a MariaDB database and run [`database/schema.sql`](/Users/dhyanpatel/Documents/New project/database/schema.sql)
2. Optionally run [`database/seed.sql`](/Users/dhyanpatel/Documents/New project/database/seed.sql)
3. Update database credentials in [`backend/src/main/resources/application.yml`](/Users/dhyanpatel/Documents/New project/backend/src/main/resources/application.yml)
4. Open [`backend/pom.xml`](/Users/dhyanpatel/Documents/New project/backend/pom.xml) in IntelliJ and run `CanteenManagementApplication`

Default seeded logins:

- Admin: `dhyan@canteen.com` / `dhyan`
- User: `student@canteen.com` / `password123`

## Frontend Run

1. Open [`frontend/`](/Users/dhyanpatel/Documents/New project/frontend)
2. Install dependencies with `npm install`
3. Run `npm run dev`
4. Ensure the backend is running on `http://localhost:8080`
