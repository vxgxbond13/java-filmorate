\# 🎬 Filmorate



\*\*Filmorate\*\* — это приложение для управления фильмами, пользователями и их взаимодействиями (лайки, друзья). 

Проект реализован на \*\*Java + Spring Boot\*\* с использованием \*\*JDBC\*\* и \*\*SQL\*\*.



\---



\## 📚 Содержание



\- \[Технологии](#технологии)

\- \[Схема базы данных](#схема-базы-данных)

\- \[Описание таблиц](#описание-таблиц)

\- \[Примеры запросов](#примеры-запросов)

\- \[Структура проекта](#структура-проекта)

\- \[Как запустить](#как-запустить)



\---



\## 🛠️ Технологии



| Технология | Версия | Описание |

|------------|--------|----------|

| \*\*Java\*\* | 21 | Основной язык программирования |

| \*\*Spring Boot\*\* | 3.5.x | Фреймворк для создания приложений |

| \*\*Spring JDBC\*\* | - | Работа с базой данных через JdbcTemplate |

| \*\*H2 Database\*\* | 2.2.x | Встроенная база данных для разработки |

| \*\*SQL\*\* | - | Язык запросов |

| \*\*Lombok\*\* | 1.18.x | Генерация кода (геттеры, сеттеры, конструкторы) |

| \*\*JUnit\*\* | 5 | Тестирование |



\---



\## 🗄️ Схема базы данных



!\[Схема базы данных]

(images/diagram.png)





> \*Диаграмма https://dbdiagram.io/d/6a70ec4b829f06bdc8745b27



\---



\## 📋 Описание таблиц



\### 1. `users` — Пользователи



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `id` | BIGINT | PK, AUTO\_INCREMENT | Уникальный идентификатор |

| `email` | VARCHAR(255) | NOT NULL, UNIQUE | Электронная почта |

| `login` | VARCHAR(100) | NOT NULL, UNIQUE | Логин пользователя |

| `name` | VARCHAR(255) | - | Имя пользователя |

| `birthday` | DATE | NOT NULL | Дата рождения |



\---



\### 2. `friendship\_statuses` — Статусы дружбы



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `id` | INTEGER | PK, AUTO\_INCREMENT | Уникальный идентификатор |

| `name` | VARCHAR(20) | NOT NULL, UNIQUE | Код статуса (PENDING, CONFIRMED) |

| `display\_name` | VARCHAR(50) | NOT NULL | Отображаемое имя |

| `description` | VARCHAR(255) | - | Описание статуса |



\---



\### 3. `friendships` — Дружба



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `user\_id` | BIGINT | PK, FK → users.id | ID пользователя |

| `friend\_id` | BIGINT | PK, FK → users.id | ID друга |

| `status\_id` | INTEGER | NOT NULL, FK → friendship\_statuses.id | Статус дружбы |

| `created\_at` | TIMESTAMP | DEFAULT CURRENT\_TIMESTAMP | Дата создания |

| `updated\_at` | TIMESTAMP | DEFAULT CURRENT\_TIMESTAMP | Дата обновления |



\---



\### 4. `mpa\_ratings` — MPA Рейтинги



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `id` | INTEGER | PK, AUTO\_INCREMENT | Уникальный идентификатор |

| `name` | VARCHAR(10) | NOT NULL, UNIQUE | Название рейтинга (G, PG, PG-13, R, NC-17) |



\---



\### 5. `films` — Фильмы



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `id` | BIGINT | PK, AUTO\_INCREMENT | Уникальный идентификатор |

| `name` | VARCHAR(255) | NOT NULL | Название фильма |

| `description` | TEXT | - | Описание |

| `release\_date` | DATE | NOT NULL | Дата релиза |

| `duration` | INTEGER | NOT NULL | Продолжительность (минуты) |

| `mpa\_rating\_id` | INTEGER | FK → mpa\_ratings.id | ID рейтинга MPA |



\---



\### 6. `genres` — Жанры



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `id` | INTEGER | PK, AUTO\_INCREMENT | Уникальный идентификатор |

| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Название жанра |



\---



\### 7. `film\_genres` — Связь фильмов с жанрами (многие-ко-многим)



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `film\_id` | BIGINT | PK, FK → films.id | ID фильма |

| `genre\_id` | INTEGER | PK, FK → genres.id | ID жанра |



\---



\### 8. `likes` — Лайки



| Колонка | Тип | Ограничения | Описание |

|---------|-----|-------------|----------|

| `film\_id` | BIGINT | PK, FK → films.id | ID фильма |

| `user\_id` | BIGINT | PK, FK → users.id | ID пользователя |

| `created\_at` | TIMESTAMP | DEFAULT CURRENT\_TIMESTAMP | Дата лайка |



\---



\## 🔍 Примеры запросов



\### Получить все фильмы с жанрами и рейтингом



```sql

SELECT 

&#x20;   f.id,

&#x20;   f.name,

&#x20;   f.description,

&#x20;   f.release\_date,

&#x20;   f.duration,

&#x20;   mr.name AS mpa\_rating,

&#x20;   GROUP\_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') AS genres

FROM films f

LEFT JOIN mpa\_ratings mr ON f.mpa\_rating\_id = mr.id

LEFT JOIN film\_genres fg ON f.id = fg.film\_id

LEFT JOIN genres g ON fg.genre\_id = g.id

GROUP BY f.id

ORDER BY f.id;

