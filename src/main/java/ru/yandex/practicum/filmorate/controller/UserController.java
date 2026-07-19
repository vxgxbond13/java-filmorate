package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
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
        validate(user);


        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя по id={}", id);
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Обновление пользователя: id={}, login={}", user.getId(), user.getLogin());
        try {
            validate(user);

            if (user.getId() == null) {
                log.warn("Id пользователя не указан");
                throw new ValidationException("Id пользователя должен быть указан");
            }

            if (!users.containsKey(user.getId())) {
                log.warn("Пользователь с id={} не найден", user.getId());
                throw new NotFoundException("Пользователь с id " + user.getId() + " не найден"); // ✅ 404
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

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
