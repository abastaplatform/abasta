-- ============================================================================
-- CREACIÓ DE BASE DE DADES
-- ============================================================================
CREATE DATABASE IF NOT EXISTS abasta_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE abasta_db;

-- ============================================================================
-- TAULA: COMPANIES
-- ============================================================================
CREATE TABLE companies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(50) UNIQUE NOT NULL COMMENT 'NIF/CIF',
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    city VARCHAR(100),
    postal_code VARCHAR(20),
    status ENUM('ACTIVE', 'INACTIVE', 'PENDING') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Empreses clients de la plataforma';

-- ============================================================================
-- TAULA: USERS
-- ============================================================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    company_id BIGINT NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt hash',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    phone VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    email_verification_expires TIMESTAMP NULL,
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,

    INDEX idx_company_id (company_id),
    INDEX idx_email (email),
    INDEX idx_company_email (company_id, email),
    INDEX idx_role (role),
    INDEX idx_email_verification_token (email_verification_token),
    INDEX idx_password_reset_token (password_reset_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Usuaris de les empreses';

-- ============================================================================
-- TAULA: SUPPLIERS
-- ============================================================================
CREATE TABLE suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    company_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    contact_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (company_id) REFERENCES companies(id),

    INDEX idx_company_id (company_id),
    INDEX idx_company_active (company_id, is_active),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Proveïdors de les empreses';

-- ============================================================================
-- TABLA: PRODUCTS
-- ============================================================================
CREATE TABLE products (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   uuid VARCHAR(255) NOT NULL UNIQUE,
   supplier_id BIGINT NOT NULL,
   category VARCHAR(255),
   name VARCHAR(255) NOT NULL,
   description TEXT,
   price DECIMAL(10, 2) NOT NULL,
   volume DECIMAL(10, 2),
   unit VARCHAR(50) COMMENT 'kg, litres, unitats, caixes, etc.',
   image_url VARCHAR(500),
   is_active BOOLEAN DEFAULT TRUE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

   FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
   INDEX idx_supplier_id (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Catàleg de productes per proveïdor';

-- ============================================================================
-- TAULA: ORDERS
-- ============================================================================
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    company_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'SENT', 'CONFIRMED', 'REJECTED', 'COMPLETED', 'CANCELLED','DELETED')
        DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    notes TEXT,
    delivery_date DATE,
    notification_method ENUM('EMAIL', 'WHATSAPP', 'BOTH'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (company_id) REFERENCES companies(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (user_id) REFERENCES users(id),

    INDEX idx_company_id (company_id),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_company_status_date (company_id, status, created_at),
    INDEX idx_supplier_status (supplier_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Comandes a proveïdors';

-- ============================================================================
-- TAULA: ORDER_ITEMS
-- ============================================================================
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id),

    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Productes afegits a la comanda.';