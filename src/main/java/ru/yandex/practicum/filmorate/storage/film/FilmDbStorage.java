package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public class FilmDbStorage implements FilmStorage{
    
    @Override
    public Collection<Film> findAll() {
        return List.of();
    }

    @Override
    public Film findById(Long id) {
        return null;
    }

    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public boolean exists(Long id) {
        return false;
    }
}
