package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaRatingStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingStorage mpaRatingStorage;

    @GetMapping
    public Collection<MpaRating> findAll() {
        log.info("GET /mpa - запрос всех рейтингов");
        return mpaRatingStorage.findAll();
    }

    @GetMapping("/{id}")
    public MpaRating findById(@PathVariable Integer id) {
        log.info("GET /mpa/{} - запрос рейтинга", id);
        return mpaRatingStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
    }
}
