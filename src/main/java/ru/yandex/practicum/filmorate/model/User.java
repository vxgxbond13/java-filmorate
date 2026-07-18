package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

/**
 * User.
 */
@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public void validate() {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        if (login == null || login.isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (name == null || name.isBlank()) {
            name = login;
        }

        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
