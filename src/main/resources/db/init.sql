-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Создание таблицы otp_codes
CREATE TABLE IF NOT EXISTS otp_codes (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id),
    operation_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    channel VARCHAR(50) NOT NULL
);

-- Создание таблицы otp_config
CREATE TABLE IF NOT EXISTS otp_config (
    id SERIAL PRIMARY KEY,
    code_length INTEGER NOT NULL,
    expiration_minutes INTEGER NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Вставка начальных данных
INSERT INTO otp_config (code_length, expiration_minutes, updated_at)
VALUES (6, 5, NOW());