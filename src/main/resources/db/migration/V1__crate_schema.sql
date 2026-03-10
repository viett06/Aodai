CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(15),
    address TEXT,

    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    last_login TIMESTAMP,

    failed_login_attempts INT,
    locked_until TIMESTAMP,

    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,

    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),

    deleted BOOLEAN DEFAULT FALSE
);

-- =========================
-- INVALIDATE TOKENS
-- =========================
CREATE TABLE invalidate_tokens (
    token VARCHAR(500) PRIMARY KEY,
    expired_at TIMESTAMP NOT NULL
);

-- =========================
-- REFRESH TOKENS
-- =========================
CREATE TABLE refresh_tokens (
    refresh_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id UUID NOT NULL,

    device_fingerprint VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),

    expiry_date TIMESTAMP NOT NULL,

    revoked BOOLEAN DEFAULT FALSE,
    revoke_reason TEXT,
    revoke_at TIMESTAMP,

    create_at TIMESTAMP,

    CONSTRAINT fk_refresh_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- MFA OTP
-- =========================
CREATE TABLE mfa_otp (
    mfa_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,

    otp_hash VARCHAR(255) NOT NULL,
    type VARCHAR(20),

    expired_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP NOT NULL,

    attempt_count INT NOT NULL DEFAULT 0,
    used BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_mfa_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- CATEGORY
-- =========================
CREATE TABLE category (
    category_id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),

    create_at TIMESTAMP,
    update_at TIMESTAMP
);

-- =========================
-- PRODUCTS
-- =========================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    description TEXT,
    brand VARCHAR(100) NOT NULL,
    image VARCHAR(255) NOT NULL,

    price NUMERIC(10,2) NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    category_id BIGINT NOT NULL,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES category(category_id)
);

-- =========================
-- INVENTORY
-- =========================
CREATE TABLE inventory (
    inventory_id BIGSERIAL PRIMARY KEY,

    quantity INT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    product_id BIGINT UNIQUE NOT NULL,

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =========================
-- CART
-- =========================
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,

    user_id UUID NOT NULL UNIQUE,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- CART ITEMS
-- =========================
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,

    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    quantity INT,
    price NUMERIC(10,2) NOT NULL,

    created_at TIMESTAMP,

    CONSTRAINT fk_cartitem_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,

    CONSTRAINT fk_cartitem_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =========================
-- ORDERS
-- =========================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    order_number VARCHAR(50) NOT NULL UNIQUE,

    order_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,

    total_amount NUMERIC(10,2) NOT NULL,

    shipping_address TEXT NOT NULL,
    billing_address TEXT,

    notes TEXT,

    cancel_reason TEXT,
    cancelled_by VARCHAR(20),
    cancel_date TIMESTAMP,

    source_cart_id BIGINT,
    source_order_id BIGINT,

    user_id UUID NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- ORDER ITEMS
-- =========================
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    product_name VARCHAR(255) NOT NULL,

    quantity INT NOT NULL,

    unit_price NUMERIC(10,2) NOT NULL,
    total_price NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,

    CONSTRAINT fk_orderitem_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =========================
-- ORDER HISTORY
-- =========================
CREATE TABLE order_history (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,

    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,

    notes TEXT,
    changed_by VARCHAR(50),

    created_at TIMESTAMP,

    CONSTRAINT fk_history_order
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- =========================
-- REVIEW
-- =========================
CREATE TABLE review (
    review_id BIGSERIAL PRIMARY KEY,

    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),

    comment VARCHAR(1000),

    created_at TIMESTAMP,

    product_id BIGINT,
    order_item BIGINT NOT NULL,
    user_id UUID NOT NULL,

    CONSTRAINT fk_review_product
        FOREIGN KEY (product_id) REFERENCES products(id),

    CONSTRAINT fk_review_orderitem
        FOREIGN KEY (order_item) REFERENCES order_items(id),

    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================
-- PAYMENTS
-- =========================
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL UNIQUE,

    payment_method VARCHAR(20) NOT NULL,

    amount NUMERIC(10,2) NOT NULL,

    note_content TEXT,
    qrUrl TEXT,

    status VARCHAR(20),

    transaction_id VARCHAR(255),

    payment_date TIMESTAMP,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- =========================
-- SHIPMENTS
-- =========================
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL UNIQUE,

    shipping_method VARCHAR(100) NOT NULL,
    tracking_number VARCHAR(50),

    status VARCHAR(20),

    estimated_delivery DATE,
    actual_delivery DATE,

    shipping_address TEXT NOT NULL,

    shipping_cost NUMERIC(10,2) NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_shipment_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
);