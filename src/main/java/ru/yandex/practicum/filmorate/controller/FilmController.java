package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
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

        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан: id={}, name={}", film.getId(), film.getName());
        return film;

    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Обновление фильма: id={}, name={}", film.getId(), film.getName());
        try {
            validate(film);

            if (film.getId() == null) {
                log.warn("Id фильма не указан");
                throw new ValidationException("Id фильма должен быть указан");
            }

            if (!films.containsKey(film.getId())) {
                log.warn("Фильм с id={} не найден", film.getId());
                throw new NotFoundException("Фильм с id " + film.getId() + " не найден"); // ✅ 404
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

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        LocalDate minDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }
    }

}
