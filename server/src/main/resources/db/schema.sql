PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('CUSTOMER', 'COLLECTOR', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ewaste_categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT UNIQUE NOT NULL,
    base_points_per_kg REAL NOT NULL,
    is_hazardous_default INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ewaste_items (
    item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER NOT NULL,
    model_name TEXT NOT NULL,
    weight_kg REAL NOT NULL CHECK (weight_kg > 0),
    is_hazardous INTEGER NOT NULL,
    waste_condition TEXT NOT NULL,
    specific_attributes TEXT,
    FOREIGN KEY (category_id) REFERENCES ewaste_categories(category_id)
);

CREATE TABLE IF NOT EXISTS collectors (
    collector_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL UNIQUE,
    vehicle_type TEXT NOT NULL,
    max_capacity_kg REAL NOT NULL,
    current_workload_kg REAL DEFAULT 0.0,
    is_available INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS pickup_requests (
    pickup_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    collector_id INTEGER,
    current_state TEXT NOT NULL,
    priority_score REAL DEFAULT 0.0,
    address TEXT NOT NULL,
    scheduled_date TEXT NOT NULL,
    preferred_time TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(user_id),
    FOREIGN KEY (collector_id) REFERENCES collectors(collector_id)
);

CREATE TABLE IF NOT EXISTS pickup_items (
    pickup_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    pickup_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    FOREIGN KEY (pickup_id) REFERENCES pickup_requests(pickup_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES ewaste_items(item_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recycling_centers (
    center_id INTEGER PRIMARY KEY AUTOINCREMENT,
    center_name TEXT NOT NULL,
    address TEXT NOT NULL,
    processing_capacity_kg REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS processing_records (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    pickup_id INTEGER NOT NULL,
    center_id INTEGER NOT NULL,
    workflow_type TEXT NOT NULL,
    points_awarded INTEGER NOT NULL,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pickup_id) REFERENCES pickup_requests(pickup_id),
    FOREIGN KEY (center_id) REFERENCES recycling_centers(center_id)
);

CREATE TABLE IF NOT EXISTS rewards (
    reward_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    points_earned INTEGER NOT NULL,
    balance INTEGER NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    is_read INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);