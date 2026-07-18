package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей. Количество: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        try {
            user.validate();
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Пользователь создан: id={}, login={}", user.getId(), user.getLogin());
            return user;
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Обновление пользователя: id={}, login={}", user.getId(), user.getLogin());
        try {
            user.validate();

            if (user.getId() == null) {
                log.warn("Id пользователя не указан");
                throw new RuntimeException("Id пользователя должен быть указан");
            }

            if (!users.containsKey(user.getId())) {
                log.warn("Пользователь с id={} не найден", user.getId());
                throw new RuntimeException("Пользователь с id " + user.getId() + " не найден");
            }

            users.put(user.getId(), user);
            log.info("Пользователь обновлён: id={}, login={}", user.getId(), user.getLogin());
            return user;

        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
