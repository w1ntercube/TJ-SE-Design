-- 暂时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除所有相关表（无顺序要求）
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS rental_orders;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS admins;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 用户表
CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(20) NOT NULL UNIQUE,
  avatar_url VARCHAR(255),
  reputation_score INT NOT NULL,
  is_banned BOOLEAN NOT NULL
);

-- 2. 管理员表
CREATE TABLE admins (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- 3. 商品表
CREATE TABLE products (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  rental_price DECIMAL(10,2) NOT NULL,
  rental_stock INT NOT NULL,
  seller_id BIGINT NOT NULL,
  is_active BOOLEAN NOT NULL,
  image_path VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. 购物车表
CREATE TABLE carts (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 5. 父类订单表（抽象）
CREATE TABLE orders (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  total_price DECIMAL(10,2) NOT NULL,
  order_status ENUM('PENDING','PAID','CANCELLED','COMPLETED') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  address VARCHAR(255) NOT NULL,
  order_type VARCHAR(31) NOT NULL,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 6. 子类：购买订单
CREATE TABLE purchase_orders (
  id BIGINT NOT NULL PRIMARY KEY,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  FOREIGN KEY (id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 7. 子类：租借订单
CREATE TABLE rental_orders (
  id BIGINT NOT NULL PRIMARY KEY,
  product_id BIGINT NOT NULL,
  rental_start DATE,
  rental_end DATE,
  rental_duration_days INT NOT NULL,
  deposit DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL,
  FOREIGN KEY (id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 8. 评论表
CREATE TABLE reviews (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comment TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);
