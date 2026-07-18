package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (description != null && description.length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        LocalDate minDate = LocalDate.of(1895, 12, 28);
        if (releaseDate == null || releaseDate.isBefore(minDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (duration == null || duration <= 0) {
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }
    }

}
