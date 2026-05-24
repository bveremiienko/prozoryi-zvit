# ПрозорийЗвіт — демо-платформа прозорості благодійних зборів

Пілот для [Web3 Resilience Lab](https://web3lab.org.ua): публічний реєстр кампаній з планом витрат, етапними звітами та журналом змін. **Платформа не приймає платежі.**

## Швидкий старт (Docker)

```bash
cd "c:\Users\bogdan\web3 project"
docker compose up --build
```

Відкрийте: **http://localhost:8080**

| Що | URL |
|----|-----|
| Реєстр зборів | http://localhost:8080/campaigns |
| Каталог організаторів | http://localhost:8080/organizers |
| Профіль організатора (демо) | http://localhost:8080/organizers/bf-zdorovya-ukrainy |
| Демо-кампанія (основна) | http://localhost:8080/campaigns/dopomoga-medychne-obladnannya |
| Прострочений звіт | http://localhost:8080/campaigns/generator-osbb-kyiv |
| Завершена кампанія | http://localhost:8080/campaigns/navchannya-veteraniv-it |

### Кабінет організатора

- **URL:** http://localhost:8080/login  
- **Email:** `organizer@demo.local`  
- **Пароль:** `demo123` (або `DEMO_ORGANIZER_PASSWORD` у docker-compose)

## Локальна розробка (без Docker app)

1. PostgreSQL 16:

```bash
docker compose up postgres -d
```

2. Запуск:

```bash
cd transparency-platform
mvn spring-boot:run -Dspring-boot.run.profiles=local,demo
```

Профіль `demo` активує seed-дані (Flyway V2).

## Сценарій демо (2–3 хв)

1. `/campaigns` — реєстр, статуси довіри.  
2. `/organizers` — профілі ініціаторів з історією зборів та індексом прозорості.  
3. `/campaigns/dopomoga-medychne-obladnannya` — чеклист прозорості, **план vs факт**, QR + копіювання посилання, звіти, SHA-256, audit log.  
4. `/campaigns/generator-osbb-kyiv` — статус «Прострочений звіт».  
5. `/reports/1/verify` — перевірка хешу звіту (Web3 trust).  
6. Увійти → додати кілька рядків витрат у звіті → перевірити audit на публічній сторінці.

## Стек

- Java 21, Spring Boot 3.4, Thymeleaf, Spring Security  
- PostgreSQL 16, Flyway  
- Локальне сховище вкладень (`./data/uploads`)

## Тести

```bash
cd transparency-platform
mvn test
```

## Структура

```
transparency-platform/   # Spring Boot додаток
docker-compose.yml
Dockerfile
docs/concept-v0.1.md
```

## Деплой (безкоштовно)

Покроково **GitHub → Neon (PostgreSQL) → Render (Docker)**:

**[docs/deploy-neon-render.md](docs/deploy-neon-render.md)**

Локально: `docker compose build`. У хмарі профіль `SPRING_PROFILES_ACTIVE=demo` і змінні `SPRING_DATASOURCE_*`, `APP_PUBLIC_BASE_URL`, `DEMO_ORGANIZER_PASSWORD`.

Контакт програми: hello@web3lab.org.ua
