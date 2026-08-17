CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  login VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(255),
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friendship_statuses (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(20) UNIQUE NOT NULL,
  display_name VARCHAR(50) NOT NULL,
  description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS friendships (
  user_id BIGINT,
  friend_id BIGINT,
  status_id INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(10) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  release_date DATE NOT NULL,
  duration INTEGER NOT NULL,
  mpa_rating_id INTEGER,
  FOREIGN KEY (mpa_rating_id) REFERENCES mpa_ratings (id)
);

CREATE TABLE IF NOT EXISTS genres (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id BIGINT,
  genre_id INTEGER,
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id BIGINT,
  user_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (film_id, user_id)
);



ALTER TABLE friendships ADD FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE friendships ADD FOREIGN KEY (friend_id) REFERENCES users (id);
ALTER TABLE friendships ADD FOREIGN KEY (status_id) REFERENCES friendship_statuses (id);
ALTER TABLE film_genres ADD FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE film_genres ADD FOREIGN KEY (genre_id) REFERENCES genres (id);
ALTER TABLE likes ADD FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE likes ADD FOREIGN KEY (user_id) REFERENCES users (id);
