# OTP Service

## Описание проекта
Сервис для генерации и верификации одноразовых паролей (OTP) с возможностью отправки через различные каналы (email, SMS, Telegram, файл). Проект реализован на Java с использованием PostgreSQL.


## 📁Функциональность
- Регистрация и аутентификация пользователей

- Генерация OTP-кодов

- Верификация OTP-кодов

- Управление пользователями (для администраторов)

- Настройка параметров OTP (длина кода, время жизни)

## 📊 Технологии
- Язык: Java 17

- База данных: PostgreSQL

- HTTP сервер: встроенный JDK HttpServer

- Логирование: SLF4J + Logback

- Зависимости:

- PostgreSQL JDBC Driver

- Jackson (JSON)

- Jsmp (SMS)

- JavaMail (Email)

## 📂 Содержимое репозитория:

```
otp-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── otpservice/
│   │   │               ├── api/               # HTTP обработчики
│   │   │               ├── config/            # Конфигурация
│   │   │               ├── dao/               # Data Access Objects
│   │   │               ├── dto/               # Data Transfer Objects
│   │   │               ├── model/             # Сущности
│   │   │               ├── service/           # Бизнес-логика
│   │   │               └── Main.java          # Точка входа
│   │   └── resources/
│   │       ├── db/                            # SQL скрипты
│   │       └── *.properties                   # Конфигурации
└── pom.xml                                    # Зависимости

```
## ⚠️ Установка и запуск
### Требования
- JDK 17+

- PostgreSQL 12+

- Maven 3.8+

### Настройка базы данных
1.Создайте базу данных:

CREATE DATABASE otp_db;

2.Настройте доступ в src/main/resources/application.properties:

properties
db.url=jdbc:postgresql://localhost:5432/otp_db
db.username=postgres
db.password=yourpassword

## Сборка и запуск

mvn clean package

java -jar target/otp-service-1.0-SNAPSHOT.jar

## 📝 API Endpoints

### Аутентификация

- POST /api/auth/register - Регистрация пользователя

{
  "username": "testuser",
  "password": "password123",
  "role": "USER"
}

- POST /api/auth/login - Вход пользователя


{
  "username": "testuser",
  "password": "password123"
}

- OTP операции
POST /api/users - Запрос OTP-кода


{
  "operationId": "login",
  "channel": "EMAIL"
}


- POST /api/users/verify - Верификация OTP


{
  "operationId": "login",
  "code": "123456"
}
