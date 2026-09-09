-- Insert default categories
INSERT OR IGNORE INTO ewaste_categories (category_name, base_points_per_kg, is_hazardous_default) VALUES
('Laptop', 50.0, 0),
('Battery', 75.0, 1),
('Display', 60.0, 1),
('Desktop Computer', 45.0, 0),
('Mobile Phone', 80.0, 1),
('Printer', 40.0, 0);

-- Insert a default recycling center
INSERT OR IGNORE INTO recycling_centers (center_name, address, processing_capacity_kg) VALUES
('Central E-Waste Recycling Hub', '123 Green Street, Eco City, EC 12345', 5000.0);

-- Insert admin user (password: admin123 - hashed)
INSERT OR IGNORE INTO users (full_name, email, password_hash, role) VALUES
('System Administrator', 'admin@ewaste.com', '$2a$10$YourHashedPasswordHere', 'ADMIN');

-- Insert sample collectors
INSERT OR IGNORE INTO users (full_name, email, password_hash, role) VALUES
('John Doe', 'john.collector@ewaste.com', '$2a$10$YourHashedPasswordHere', 'COLLECTOR'),
('Jane Smith', 'jane.collector@ewaste.com', '$2a$10$YourHashedPasswordHere', 'COLLECTOR');

INSERT OR IGNORE INTO collectors (user_id, vehicle_type, max_capacity_kg, current_workload_kg, is_available) VALUES
(2, 'Van', 500.0, 0.0, 1),
(3, 'Truck', 1000.0, 0.0, 1);