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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private Long nextId = 1L;

    @GetMapping
    public List<User> findAll() {
        log.info("Запрос на получение всех пользователей. Количество: {}", users.size());
        return users;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        try {
            user.validate();
            user.setId(nextId++);
            users.add(user);
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
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(user.getId())) {
                    users.set(i, user);
                    log.info("Пользователь обновлён: id={}, login={}", user.getId(), user.getLogin());
                    return user;
                }
            }
            log.warn("Пользователь с id={} не найден", user.getId());
            throw new RuntimeException("Пользователь с id " + user.getId() + " не найден");
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }
}
