# 🎬 Filmorate

**Filmorate** — это приложение для управления фильмами, пользователями и их взаимодействиями (лайки, друзья). 
Проект реализован на **Java + Spring Boot**.

---

## 📚 Содержание

- [Технологии](#технологии)
- [Схема базы данных](#схема-базы-данных)
- [Описание таблиц](#описание-таблиц)


---

## 🛠️ Технологии

| Технология | Версия | Описание |
|------------|--------|----------|
| **Java** | 11+ | Основной язык программирования |
| **Spring Boot** | 2.7.x | Фреймворк для создания приложений |
| **SQL** | - | Язык запросов |
| **Lombok** | - | Генерация кода |
| **JUnit** | 5 | Тестирование |

---

## 🗄️ Схема базы данных

![Схема базы данных] 
<img width="1275" height="810" alt="Untitled" src="https://github.com/user-attachments/assets/0e15975a-1c47-4bd9-86aa-d4e44e7d9800" />



> *Диаграмма https://dbdiagram.io/d/6a70ec4b829f06bdc8745b27

---

## 📋 Описание таблиц

### 1. `users` — Пользователи

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Уникальный идентификатор |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | Электронная почта |
| `login` | VARCHAR(100) | NOT NULL, UNIQUE | Логин пользователя |
| `name` | VARCHAR(255) | - | Имя пользователя |
| `birthday` | DATE | NOT NULL | Дата рождения |

---

### 2. `friendship_statuses` — Статусы дружбы

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| `id` | INTEGER | PK, AUTO_INCREMENT | Уникальный идентификатор |
| `name` | VARCHAR(20) | NOT NULL, UNIQUE | Код статуса (PENDING, CONFIRMED) |
| `display_name` | VARCHAR(50) | NOT NULL | Отображаемое имя |
| `description` | VARCHAR(255) | - | Описание статуса |

---

### 3. `friendships` — Дружба

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| `user_id` | BIGINT | PK, FK → users.id | ID пользователя |
| `friend_id` | BIGINT | PK, FK → users.id | ID друга |
| `status_id` | INTEGER | NOT NULL, FK → friendship_statuses.id | Статус дружбы |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Дата создания |
| `updated_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Дата обновления |

---

### 4. `films` — Фильмы

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Уникальный идентификатор |
| `name` | VARCHAR(255) | NOT NULL | Название фильма |
| `description` | TEXT | - | Описание |
| `release_date` | DATE | NOT NULL | Дата релиза |
| `duration` | INTEGER | NOT NULL | Продолжительность (минуты) |
| `mpa_rating` | VARCHAR(10) | - | Рейтинг MPA (G, PG, PG-13, R, NC-17) |

---

### 5. `genres` — Жанры

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| `id` | INTEGER | PK, AUTO_INCREMENT | Уникальный идентификатор |
| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Название жанра |

---

### 6. `film_genres` — Связь фильмов с жанрами (многие-ко-многим)

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| `film_id` | BIGINT | PK, FK → films.id | ID фильма |
| `genre_id` | INTEGER | PK, FK → genres.id | ID жанра |

---

### 7. `likes` — Лайки

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| `film_id` | BIGINT | PK, FK → films.id | ID фильма |
| `user_id` | BIGINT | PK, FK → users.id | ID пользователя |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Дата лайка |

---
