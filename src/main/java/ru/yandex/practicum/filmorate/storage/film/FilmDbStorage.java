package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final MpaRatingRowMapper mpaRatingRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         FilmRowMapper filmRowMapper,
                         GenreRowMapper genreRowMapper,
                         MpaRatingRowMapper mpaRatingRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.mpaRatingRowMapper = mpaRatingRowMapper;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        for (Film film : films) {
            loadGenres(film);
            loadMpaRating(film);
            loadLikes(film);  // ← ДОБАВЛЕНО
        }

        return films;
    }

    @Override
    public Film findById(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> result = jdbcTemplate.query(sql, filmRowMapper, id);
        if (result.isEmpty()) {
            return null;
        }
        Film film = result.get(0);
        loadGenres(film);
        loadMpaRating(film);
        loadLikes(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpaRatingId() != null ? film.getMpaRatingId() : 1);
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(id);

        saveGenres(film);
        saveLikes(film);  // ← ДОБАВЛЕНО

        loadGenres(film);
        loadMpaRating(film);
        loadLikes(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpaRatingId() != null ? film.getMpaRatingId() : 1,
                film.getId()
        );

        // Обновляем жанры
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);

        // Обновляем лайки
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", film.getId());
        saveLikes(film);  // ← ДОБАВЛЕНО

        loadGenres(film);
        loadMpaRating(film);
        loadLikes(film);  // ← ДОБАВЛЕНО

        return film;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public boolean exists(Long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private void loadGenres(Film film) {
        String sql = "SELECT g.* FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.id";
        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void loadMpaRating(Film film) {
        if (film.getMpaRatingId() != null && film.getMpaRatingId() > 0) {
            String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
            List<MpaRating> result = jdbcTemplate.query(sql, mpaRatingRowMapper, film.getMpaRatingId());
            if (!result.isEmpty()) {
                film.setMpa(result.get(0));
            }
        }
    }

    private void loadLikes(Film film) {  // ← НОВЫЙ МЕТОД
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
        film.setLikes(new HashSet<>(likes));
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            batchArgs.add(new Object[]{film.getId(), genre.getId()});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs); // <- Только один запрос к БД, после цикла //
    }

    private void saveLikes(Film film) {
        if (film.getLikes() == null || film.getLikes().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Long userId : film.getLikes()) {
            batchArgs.add(new Object[]{film.getId(), userId});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs); // <- Только один запрос к БД, после цикла //
    }
}
