# 🛍️ EasyShopMorrisEdition
Final Capstone of the YUU LTCA (Learn to Code Academy)

## Overview
EasyShop is a full-stack Java Spring Boot e-commerce web application that supports dynamic product listing, full CRUD operations on the backend, category mapping via foreign keys, and secure admin access using role-based authorization. The project is built on a MySQL database with a Java backend and a client-side HTML/JavaScript frontend. API interaction and testing was performed using Postman.

---

## 💡 Project Goals
- Create a RESTful API using Spring Boot with role-based authorization.
- Implement full CRUD functionality for managing products and categories.
- Store and retrieve product data using MySQL and DAOs.
- Serve dynamic content to a frontend application that uses Mustache templates.
- Ensure consistent UI with image paths and data binding.

---

## 🚀 Development Journey

### 1. 🧱 MySQL DAO Layer
We began by building our **MySQL DAO classes**, including:
- `MySqlProductDAO`
- `MySqlCategoryDAO`
- `MySqlUserDAO`

These classes handled the application’s SQL logic. Each DAO encapsulated responsibilities such as:
- Fetching all products
- Finding a product by ID
- Inserting new records
- Updating and deleting entries
- Validating credentials and roles for users

This layer ensured clean separation of database access logic from our controller logic and allowed us to inject dependencies using Spring.

---

### 2. 🌐 Controller Layer
Next, we created our REST controllers:
- `ProductsController`
- `CategoriesController`
- `AuthenticationController`

Each controller included request mappings like:
- `GET /products`
- `GET /products/{id}`
- `POST /products` (admin-only)
- `PUT /products/{id}` (admin-only)
- `DELETE /products/{id}` (admin-only)
- `GET /categories`
- `POST /login` (for JWT authentication)

We also implemented request validation and security features to ensure only authenticated admins could make changes to the product catalog.

---

### 3. 📬 Testing with Postman
We tested each endpoint using Postman:
- Constructed full JSON bodies for `POST` and `PUT`
- Authenticated using login and bearer tokens
- Verified status codes (200, 401, 403, etc.)
- Ensured consistency with MySQL constraints (e.g., `category_id` foreign keys)

📌 **Bug 2** appeared during testing: attempting a product update was inserting a new one. After tracing the flow, we corrected the logic inside `MySqlProductDAO` to properly use the `UPDATE` query tied to the correct `product_id`.

---

### 4. 🎨 Frontend Integration
The frontend resided in the `capstone-client-web-application` folder, which used Mustache templates like `index.html` and `product.html`.

We faced issues where images weren’t displaying. Diagnosis revealed:
- Product image filenames from the database (e.g., `"smartphone.jpg"`)
- Actual file path in project: `/images/products/smartphone.jpg`



