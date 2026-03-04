# SG Customer Hub

Spring Boot 3 + MyBatis-Plus customer CRM with Singapore holiday greeting mail and employee blog homepage.

## Features
- Employee login (role-based redirect to `/me/blog` / `/customers` / `/mail/logs`)
- Customer management (name, phone, email, birthday, hobbies, preferred language, multiple social accounts)
- Daily greeting scheduler at `09:00 Asia/Singapore`
- Singapore holiday + birthday mail sending with dedupe log and i18n mail templates (EN/ZH)
- Public blog page and employee personal blog management page
- REST API for customer/blog/mail modules
- Server-side pagination for customer/blog/mail lists
- Role-based permissions (`ADMIN`, `SALES`, `MARKETING`, `OPS`)
- Mobile + Pad responsive pages

## Stack
- Java 17+
- Spring Boot 3.3.x
- MyBatis-Plus
- MySQL 8
- Thymeleaf + Bootstrap 5
- Spring Mail (SMTP)

## Setup
1. Create DB and tables:
   - Run `sql/init.sql`
2. Configure datasource and SMTP in `src/main/resources/application.yml`
3. Start:
   - `mvn spring-boot:run`
4. Open:
   - Public blog: `http://localhost:8088/blog`
   - Login: `http://localhost:8088/login`

## Default account
- username: `admin`
- password: `password`

## Key routes
- `/me/blog` employee first page after login
- `/customers` customer CRUD
- `/mail/logs` mail logs and manual trigger
- `/blog` public blog
- `/api/customers` customer REST API
- `/api/blog/posts` public blog REST API
- `/api/me/blog/posts` employee blog REST API
- `/api/mail/logs` mail log REST API

## Notes
- All business date calculation uses `Asia/Singapore`.
- Singapore holiday table should be maintained yearly in `sg_holiday_calendar`.
