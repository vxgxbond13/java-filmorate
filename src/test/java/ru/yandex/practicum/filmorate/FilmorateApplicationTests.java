package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FilmorateApplicationTests {
    private Film film;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Начало");
        film.setDescription("Фильм о снах");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
    }

    // ========== Name ==========

    @Test
    void validNameShouldPass() {
        film.setName("Крепкий орешек");
        assertDoesNotThrow(film::validate);
    }

    @Test
    void nullNameShouldThrow() {
        film.setName(null);
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    void emptyNameShouldThrow() {
        film.setName("");
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    void blankNameShouldThrow() {
        film.setName("   ");
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    // ========== Description ==========

    @Test
    void validDescriptionShouldPass() {
        film.setDescription("Короткое описание");
        assertDoesNotThrow(film::validate);
    }

    @Test
    void nullDescriptionShouldPass() {
        film.setDescription(null);
        assertDoesNotThrow(film::validate);
    }

    @Test
    void descriptionExactly200ShouldPass() {
        String desc = "a".repeat(200);
        film.setDescription(desc);
        assertDoesNotThrow(film::validate);
    }

    @Test
    void descriptionMoreThan200ShouldThrow() {
        String desc = "a".repeat(201);
        film.setDescription(desc);
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Описание не может быть длиннее 200 символов", ex.getMessage());
    }

    // ========== Release Date ==========

    @Test
    void validReleaseDateShouldPass() {
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertDoesNotThrow(film::validate);
    }

    @Test
    void releaseDateOnMinDateShouldPass() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(film::validate);
    }

    @Test
    void nullReleaseDateShouldThrow() {
        film.setReleaseDate(null);
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void releaseDateBeforeMinDateShouldThrow() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    // ========== Duration ==========

    @Test
    void validDurationShouldPass() {
        film.setDuration(120);
        assertDoesNotThrow(film::validate);
    }

    @Test
    void nullDurationShouldThrow() {
        film.setDuration(null);
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void zeroDurationShouldThrow() {
        film.setDuration(0);
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void negativeDurationShouldThrow() {
        film.setDuration(-10);
        ValidationException ex = assertThrows(ValidationException.class, film::validate);
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void durationOneShouldPass() {
        film.setDuration(1);
        assertDoesNotThrow(film::validate);
    }
}
