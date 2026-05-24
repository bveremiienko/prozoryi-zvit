# Деплой безкоштовно: GitHub → Neon (PostgreSQL) → Render (додаток)

Покрокова інструкція для **ПрозорийЗвіт**. Потрібні: акаунт GitHub, [neon.tech](https://neon.tech), [render.com](https://render.com).

---

## Огляд

```
GitHub (код)  →  Render збирає Docker  →  Spring Boot
                      ↓
                 Neon PostgreSQL (БД + Flyway міграції)
```

- **Neon** — безкоштовна PostgreSQL у хмарі.
- **Render** — безкоштовний Web Service з вашого `Dockerfile`.
- Профіль Spring у хмарі: **`demo`** (не `local`).

---

## Крок 0. Перевірка локально (необов’язково)

```powershell
cd "c:\Users\bogdan\web3 project"
docker compose up --build
```

Відкрийте http://localhost:8080/campaigns — якщо працює, до деплою все готово.

---

## Крок 1. Git на комп’ютері

### 1.1. Встановити Git

Якщо ще немає: https://git-scm.com/download/win  
Після встановлення у PowerShell:

```powershell
git --version
```

### 1.2. Ім’я та email (один раз)

```powershell
git config --global user.name "Ваше Ім'я"
git config --global user.email "ваш@email.com"
```

### 1.3. Ініціалізувати репозиторій у папці проєкту

```powershell
cd "c:\Users\bogdan\web3 project"
git init
git branch -M main
```

### 1.4. Перший коміт

```powershell
git add .
git status
```

Переконайтесь, що **немає** `.env`, паролів, папки `data/` з завантаженнями. Вони в `.gitignore`.

```powershell
git commit -m "ПрозорийЗвіт: демо-платформа прозорості зборів"
```

---

## Крок 2. Репозиторій на GitHub

### 2.1. Створити репозиторій

1. Увійдіть на https://github.com  
2. **+** → **New repository**  
3. Ім’я, наприклад: `prozoryi-zvit`  
4. **Public** (для безкоштовного Render зручніше)  
5. **Не** ставте галочки «Add README» / «Add .gitignore» — репо має бути порожнім.  
6. **Create repository**

### 2.2. Підключити remote і запушити

На сторінці GitHub скопіюйте URL (HTTPS), наприклад  
`https://github.com/ВАШ_ЛОГІН/prozoryi-zvit.git`

```powershell
cd "c:\Users\bogdan\web3 project"
git remote add origin https://github.com/ВАШ_ЛОГІН/prozoryi-zvit.git
git push -u origin main
```

Якщо просить логін — використайте **Personal Access Token** замість пароля:  
GitHub → **Settings** → **Developer settings** → **Personal access tokens** → **Generate new token (classic)** → права `repo`.

### 2.3. Оновлення коду пізніше

```powershell
git add .
git commit -m "Опис змін"
git push
```

Render після push **сам перезбере** сервіс (якщо увімкнено Auto-Deploy).

---

## Крок 3. База даних на Neon

### 3.1. Реєстрація

1. https://neon.tech → **Sign up** (можна через GitHub).  
2. **New Project**  
3. Назва: `prozoryi-zvit`  
4. Регіон: ближче до Європи (наприклад Frankfurt).  
5. PostgreSQL **16** — за замовчуванням ок.

### 3.2. Дані для підключення

На Dashboard проєкту:

- **Connection string** (інколи показують як URI):
  ```
  postgresql://USER:PASSWORD@ep-xxxxx.region.aws.neon.tech/neondb?sslmode=require
  ```

Запишіть окремо:

| Що | Приклад |
|----|---------|
| Host | `ep-xxxxx.region.aws.neon.tech` |
| Database | `neondb` (або як у вас названо) |
| User | `neondb_owner` |
| Password | довгий рядок |
| Port | `5432` |

### 3.3. JDBC URL для Spring Boot

Render/ Spring потребують формат **JDBC**:

```text
jdbc:postgresql://ep-xxxxx.region.aws.neon.tech/neondb?sslmode=require
```

Правило: `jdbc:postgresql://` + **host** + `/` + **ім’я БД** + `?sslmode=require`  
(без `postgresql://user:pass@` всередині — логін/пароль окремими змінними).

### 3.4. Перевірка (необов’язково)

У Neon → **SQL Editor** → виконайте:

```sql
SELECT 1;
```

Після першого запуску додатку Flyway створить таблиці сам.

---

## Крок 4. Деплой на Render

### 4.1. Реєстрація

1. https://render.com → **Get Started** (логін через GitHub зручно).  
2. Дозвольте доступ до репозиторію `prozoryi-zvit`.

### 4.2. Новий Web Service

1. **Dashboard** → **New +** → **Web Service**  
2. **Connect** ваш репозиторій `prozoryi-zvit`  
3. Налаштування:

| Поле | Значення |
|------|----------|
| **Name** | `prozoryi-zvit` (або будь-яке) |
| **Region** | Frankfurt / Oregon — не критично |
| **Branch** | `main` |
| **Runtime** | **Docker** |
| **Dockerfile Path** | `./Dockerfile` (за замовчуванням) |
| **Instance Type** | **Free** |

### 4.3. Змінні середовища (Environment)

Розділ **Environment Variables** → **Add Environment Variable**:

| Key | Value |
|-----|--------|
| `SPRING_PROFILES_ACTIVE` | `demo` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://ВАШ_HOST/neondb?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | user з Neon |
| `SPRING_DATASOURCE_PASSWORD` | password з Neon |
| `DEMO_ORGANIZER_PASSWORD` | **надійний пароль** (не `demo123` у публічному інтернеті) |
| `APP_PUBLIC_BASE_URL` | спочатку залиште порожнім або `https://prozoryi-zvit.onrender.com` — **після першого деплою** підставте точний URL з Render |
| `APP_UPLOAD_DIR` | `/app/data/uploads` |

**Важливо:** не додавайте профіль `local` — лише `demo`.

### 4.4. Health check (рекомендовано)

У **Advanced**:

- **Health Check Path:** `/campaigns`

### 4.5. Створити сервіс

**Create Web Service** → почнеться build (Maven у Docker, 5–15 хв).

Логи: вкладка **Logs**. Успіх — рядки на кшталт `Started TransparencyApplication` і Flyway `Successfully applied 3 migrations`.

### 4.6. Публічний URL

Після деплою зверху з’явиться посилання:

```text
https://prozoryi-zvit.onrender.com
```

(ім’я залежить від **Name** сервісу).

1. Відкрийте `/campaigns` — мають бути 3 демо-кампанії.  
2. **Environment** → оновіть `APP_PUBLIC_BASE_URL` на цей HTTPS URL → **Save** → **Manual Deploy** → **Deploy latest commit** (щоб QR і «копіювати посилання» були правильні).

---

## Крок 5. Перевірка після деплою

| Перевірка | URL |
|-----------|-----|
| Реєстр | `https://ВАШ-СЕРВІС.onrender.com/campaigns` |
| Організатори | `.../organizers` |
| Логін | `.../login` — `organizer@demo.local` + ваш `DEMO_ORGANIZER_PASSWORD` |
| Verify hash | `.../reports/1/verify` |

Якщо **502 / Application failed to respond**:

- У логах Render шукайте помилку Flyway або підключення до БД.  
- Перевірте JDBC URL і `sslmode=require`.  
- Перевірте, що `SPRING_PROFILES_ACTIVE=demo`.

Якщо сторінка **довго вантажиться** після простою — нормально для Free tier (cold start ~30–60 с).

---

## Крок 6. Автодеплой з GitHub

За замовчуванням у Render увімкнено **Auto-Deploy** при push у `main`.

```powershell
# змінили код локально
git add .
git commit -m "Оновлення UI"
git push
```

Render перезбере образ і перезапустить сервіс.

---

## Обмеження безкоштовного тарифу

1. **Сон** — після ~15 хв без трафіку сервіс «засинає»; перший запит повільний.  
2. **Файли звітів** — зберігаються на диску контейнера; після redeploy **можуть зникнути**. Демо-звіти з seed залишаться в БД, нові завантаження — ні.  
3. **Neon free** — ліміти на об’єм і compute; для демо гранту зазвичай достатньо.

---

## Що вказати в заявці Web3 Resilience Lab

```text
Демо: https://ВАШ-СЕРВІС.onrender.com
Кампанія: https://ВАШ-СЕРВІС.onrender.com/campaigns/dopomoga-medychne-obladnannya
```

---

## Типові помилки

| Симптом | Рішення |
|---------|---------|
| `Connection refused` до БД | Неправильний host/JDBC; потрібен `sslmode=require` |
| `password authentication failed` | Перекопіюйте пароль з Neon; без пробілів |
| Порожній сайт / 404 на `/` | Головна `/` є; спробуйте `/campaigns` |
| QR веде на localhost | Оновіть `APP_PUBLIC_BASE_URL` і redeploy |
| Build failed у Docker | Локально: `docker compose build`; дивіться Logs на Render |
| `local` profile | Має бути лише `demo` у змінних Render |

---

## Короткий чеклист

- [ ] Git init + commit + push на GitHub  
- [ ] Neon project + JDBC URL + user/password  
- [ ] Render Web Service (Docker, Free)  
- [ ] Env: `demo`, datasource, `DEMO_ORGANIZER_PASSWORD`, `APP_*`  
- [ ] `/campaigns` відкривається  
- [ ] `APP_PUBLIC_BASE_URL` = фінальний HTTPS URL  
- [ ] Логін організатора працює  

---

Контакт програми: hello@web3lab.org.ua
