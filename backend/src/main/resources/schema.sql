-- =============================================================
-- FitVerse AI — Reference MySQL schema (Phase 3)
--
-- By default this project lets Hibernate create/update the schema
-- straight from the @Entity classes (spring.jpa.hibernate.ddl-auto=update
-- in application.properties) — that's the easiest way to get started and
-- this file does NOT run automatically.
--
-- If you'd rather manage the schema by hand instead:
--   1. Run this file against your database:
--        mysql -u root -p fitverse_db < src/main/resources/schema.sql
--   2. In application.properties, change:
--        spring.jpa.hibernate.ddl-auto=validate
--      (Hibernate will then only check your entities match this schema
--      on startup, and will refuse to start if they don't.)
--
-- Table order respects foreign-key dependencies (parents before children).
-- =============================================================

CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    email           VARCHAR(255)  NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    role            VARCHAR(20)   NOT NULL,
    avatar_url      VARCHAR(500),
    created_at      DATETIME      NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categories (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    slug            VARCHAR(100)  NOT NULL,
    description     VARCHAR(500),
    CONSTRAINT uq_categories_slug UNIQUE (slug)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200)  NOT NULL,
    brand           VARCHAR(100)  NOT NULL,
    description     TEXT          NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    sale_price      DECIMAL(10,2),
    category_id     BIGINT        NOT NULL,
    fit_confidence  INT,
    stock_quantity  INT           NOT NULL,
    material        VARCHAR(300),
    shipping_info   VARCHAR(300),
    returns_info    VARCHAR(300),
    care_info       VARCHAR(300),
    created_at      DATETIME      NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB;

CREATE INDEX idx_products_category_id ON products (category_id);

CREATE TABLE IF NOT EXISTS product_sizes (
    product_id      BIGINT        NOT NULL,
    size            VARCHAR(10)   NOT NULL,
    CONSTRAINT fk_product_sizes_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product_images (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT        NOT NULL,
    url             VARCHAR(500)  NOT NULL,
    sort_order      INT           NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_product_images_product_id ON product_images (product_id);

CREATE TABLE IF NOT EXISTS addresses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    full_name       VARCHAR(150)  NOT NULL,
    line1           VARCHAR(255)  NOT NULL,
    city            VARCHAR(100)  NOT NULL,
    zip             VARCHAR(20)   NOT NULL,
    country         VARCHAR(100)  NOT NULL,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_addresses_user_id ON addresses (user_id);

CREATE TABLE IF NOT EXISTS cart_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    product_id      BIGINT        NOT NULL,
    size            VARCHAR(10)   NOT NULL,
    quantity        INT           NOT NULL,
    added_at        DATETIME      NOT NULL,
    CONSTRAINT fk_cart_items_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_cart_items_user_id ON cart_items (user_id);

CREATE TABLE IF NOT EXISTS wishlist_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    product_id      BIGINT        NOT NULL,
    added_at        DATETIME      NOT NULL,
    CONSTRAINT uq_wishlist_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_wishlist_items_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_wishlist_items_user_id ON wishlist_items (user_id);

CREATE TABLE IF NOT EXISTS orders (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number            VARCHAR(30)   NOT NULL,
    user_id                 BIGINT        NOT NULL,
    subtotal                DECIMAL(10,2) NOT NULL,
    shipping                DECIMAL(10,2) NOT NULL,
    total                   DECIMAL(10,2) NOT NULL,
    status                  VARCHAR(20)   NOT NULL,
    created_at              DATETIME      NOT NULL,
    shipping_full_name      VARCHAR(150)  NOT NULL,
    shipping_line1          VARCHAR(255)  NOT NULL,
    shipping_city           VARCHAR(100)  NOT NULL,
    shipping_zip            VARCHAR(20)   NOT NULL,
    shipping_country        VARCHAR(100)  NOT NULL,
    CONSTRAINT uq_orders_order_number UNIQUE (order_number),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_created_at ON orders (created_at);

CREATE TABLE IF NOT EXISTS order_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT        NOT NULL,
    product_id      BIGINT        NOT NULL,
    product_name    VARCHAR(200)  NOT NULL,
    image_url       VARCHAR(500),
    size            VARCHAR(10)   NOT NULL,
    quantity        INT           NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB;

CREATE INDEX idx_order_items_order_id ON order_items (order_id);

CREATE TABLE IF NOT EXISTS reviews (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT        NOT NULL,
    user_id         BIGINT        NOT NULL,
    user_name       VARCHAR(100)  NOT NULL,
    rating          INT           NOT NULL,
    comment         TEXT          NOT NULL,
    created_at      DATETIME      NOT NULL,
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_reviews_product_id ON reviews (product_id);
