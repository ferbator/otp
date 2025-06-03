# OTP-сервис

Spring Boot-приложение для генерации, отправки и валидации OTP-кодов. Поддерживает отправку через Email, SMS, Telegram и сохранение в файл. Разделённый API для пользователей и администраторов.

## Функциональность

- Генерация OTP по операциям
- Валидация OTP-кодов
- Отправка по Email, SMS, Telegram
- Сохранение в файл
- Конфигурация OTP (время жизни, длина)
- Только один администратор
- Безопасность
  - JWT-токены
  - Аутентификацию пользователей
  - Разделение доступа на USER / ADMIN

## Стек технологий

- Java 21
- Spring Boot
- Spring Security + JWT
- PostgreSQL
- Lombok
- JPA (Hibernate)
- Jakarta Validation
- Telegram Bots API / SMPP эмулятор
- Gradle

Вот примеры API-запросов для твоего OTP-проекта с разделением на **пользовательское API** и **админское API**. Формат — `curl`, можно адаптировать под Postman или Swagger.

---

## Аутентификация

### Регистрация

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "user1", "password": "password", "role": "USER"}'
```

### Логин

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user1", "password": "password"}'
```

Ответ: `{ "token": "Bearer eyJhbGciOiJIUzI1Ni..." }`

---

## Пользовательское API (`/api`)

### Генерация OTP-кода

```bash
curl -X POST http://localhost:8080/api/send \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
        "recipient": "user1@example.com",
        "channel": "EMAIL",
        "operationId": "op123"
      }'
```

### Валидация OTP-кода

```bash
curl -X POST http://localhost:8080/api/validate \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
        "recipient": "user1@example.com",
        "channel": "EMAIL",
        "code": "123456"
      }'
```

---

## Админское API (`/api/admin`)

### Получить всех пользователей (кроме админов)

```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <ADMIN_JWT>"
```

### Удалить пользователя по ID

```bash
curl -X DELETE http://localhost:8080/api/admin/users/3 \
  -H "Authorization: Bearer <ADMIN_JWT>"
```

### Получить текущую OTP-конфигурацию

```bash
curl -X GET http://localhost:8080/api/admin/otp-config \
  -H "Authorization: Bearer <ADMIN_JWT>"
```

### Обновить конфигурацию OTP

```bash
curl -X PUT http://localhost:8080/api/admin/otp-config \
  -H "Authorization: Bearer <ADMIN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
        "codeLength": 6,
        "ttlMinutes": 5
      }'
```

---

## Сохранение в файл

Если указан канал `"FILE"`, код будет сохранён в файл (по умолчанию — в корень проекта):

```bash
-d '{
  "recipient": "file",
  "channel": "FILE",
  "operationId": "op987"
}'
```

---
