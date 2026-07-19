package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmorateApplicationTests {
    private Film film;
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Начало");
        film.setDescription("Фильм о снах");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        filmController = new FilmController();
    }

    // ========== Name ==========

    @Test
    void validNameShouldPass() {
        film.setName("Крепкий орешек");
        assertDoesNotThrow(() -> callValidate(film));
    }

    @Test
    void nullNameShouldThrow() {
        film.setName(null);
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    void emptyNameShouldThrow() {
        film.setName("");
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    void blankNameShouldThrow() {
        film.setName("   ");
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    // ========== Description ==========

    @Test
    void validDescriptionShouldPass() {
        film.setDescription("Короткое описание");
        assertDoesNotThrow(() -> callValidate(film));
    }

    @Test
    void nullDescriptionShouldPass() {
        film.setDescription(null);
        assertDoesNotThrow(() -> callValidate(film));
    }

    @Test
    void descriptionExactly200ShouldPass() {
        String desc = "a".repeat(200);
        film.setDescription(desc);
        assertDoesNotThrow(() -> callValidate(film));
    }

    @Test
    void descriptionMoreThan200ShouldThrow() {
        String desc = "a".repeat(201);
        film.setDescription(desc);
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Описание не может быть длиннее 200 символов", ex.getMessage());
    }

    // ========== Release Date ==========

    @Test
    void validReleaseDateShouldPass() {
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertDoesNotThrow(() -> callValidate(film));
    }

    @Test
    void releaseDateOnMinDateShouldPass() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> callValidate(film));
    }

    @Test
    void nullReleaseDateShouldThrow() {
        film.setReleaseDate(null);
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void releaseDateBeforeMinDateShouldThrow() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    // ========== Duration ==========

    @Test
    void validDurationShouldPass() {
        film.setDuration(120);
        assertDoesNotThrow(() -> callValidate(film));
    }

    @Test
    void nullDurationShouldThrow() {
        film.setDuration(null);
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void zeroDurationShouldThrow() {
        film.setDuration(0);
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void negativeDurationShouldThrow() {
        film.setDuration(-10);
        ValidationException ex = assertThrows(ValidationException.class, () -> callValidate(film));
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void durationOneShouldPass() {
        film.setDuration(1);
        assertDoesNotThrow(() -> callValidate(film));
    }

    // Helper method to access private validate method via reflection
    private void callValidate(Film film) {
        try {
            Method validateMethod = FilmController.class.getDeclaredMethod("validate", Film.class);
            validateMethod.setAccessible(true);
            validateMethod.invoke(filmController, film);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof ValidationException) {
                throw (ValidationException) cause;
            }
            throw new RuntimeException("Error calling validate method", e);
        }
    }
}
