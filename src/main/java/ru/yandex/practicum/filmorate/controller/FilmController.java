package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов. Количество: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Создание фильма: name={}", film.getName());
        try {
            film.validate();
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Фильм создан: id={}, name={}", film.getId(), film.getName());
            return film;
        } catch (Exception e) {
            log.error("Ошибка при создании фильма: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Обновление фильма: id={}, name={}", film.getId(), film.getName());
        try {
            film.validate();

            if (film.getId() == null) {
                log.warn("Id фильма не указан");
                throw new RuntimeException("Id фильма должен быть указан");
            }

            if (!films.containsKey(film.getId())) {
                log.warn("Фильм с id={} не найден", film.getId());
                throw new RuntimeException("Фильм с id " + film.getId() + " не найден");
            }

            films.put(film.getId(), film);
            log.info("Фильм обновлён: id={}, name={}", film.getId(), film.getName());
            return film;

        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
