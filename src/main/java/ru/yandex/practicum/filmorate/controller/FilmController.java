package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final List<Film> films = new ArrayList<>();
    private Long nextId = 1L;

    @GetMapping
    public List<Film> findAll() {
        log.info("Запрос на получение всех фильмов. Количество: {}", films.size());
        return films;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Создание фильма: name={}", film.getName());
        try {
            film.validate();
            film.setId(nextId++);
            films.add(film);
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
            for (int i = 0; i < films.size(); i++) {
                if (films.get(i).getId().equals(film.getId())) {
                    films.set(i, film);
                    return film;
                }
            }
            log.warn("Фильм с id={} не найден", film.getId());
            throw new RuntimeException("Фильм с id " + film.getId() + " не найден");
        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage());
            throw e;
        }
    }

}
