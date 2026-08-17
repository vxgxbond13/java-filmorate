package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmorateApplicationTests {

    private Film film;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Начало");
        film.setDescription("Фильм о снах");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage, userService);
    }

    // ========== Name ==========

    @Test
    void validNameShouldPass() {
        film.setName("Крепкий орешек");
        assertDoesNotThrow(() -> filmService.create(film));
    }

    @Test
    void nullNameShouldThrow() {
        film.setName(null);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    void emptyNameShouldThrow() {
        film.setName("");
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    void blankNameShouldThrow() {
        film.setName("   ");
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    // ========== Description ==========

    @Test
    void validDescriptionShouldPass() {
        film.setDescription("Короткое описание");
        assertDoesNotThrow(() -> filmService.create(film));
    }

    @Test
    void nullDescriptionShouldPass() {
        film.setDescription(null);
        assertDoesNotThrow(() -> filmService.create(film));
    }

    @Test
    void descriptionExactly200ShouldPass() {
        String desc = "a".repeat(200);
        film.setDescription(desc);
        assertDoesNotThrow(() -> filmService.create(film));
    }

    @Test
    void descriptionMoreThan200ShouldThrow() {
        String desc = "a".repeat(201);
        film.setDescription(desc);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Описание не может быть длиннее 200 символов", ex.getMessage());
    }

    // ========== Release Date ==========

    @Test
    void validReleaseDateShouldPass() {
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertDoesNotThrow(() -> filmService.create(film));
    }

    @Test
    void releaseDateOnMinDateShouldPass() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> filmService.create(film));
    }

    @Test
    void nullReleaseDateShouldThrow() {
        film.setReleaseDate(null);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void releaseDateBeforeMinDateShouldThrow() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    // ========== Duration ==========

    @Test
    void validDurationShouldPass() {
        film.setDuration(120);
        assertDoesNotThrow(() -> filmService.create(film));
    }

    @Test
    void nullDurationShouldThrow() {
        film.setDuration(null);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void zeroDurationShouldThrow() {
        film.setDuration(0);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void negativeDurationShouldThrow() {
        film.setDuration(-10);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmService.create(film));
        assertEquals("Продолжительность должна быть положительным числом", ex.getMessage());
    }

    @Test
    void durationOneShouldPass() {
        film.setDuration(1);
        assertDoesNotThrow(() -> filmService.create(film));
    }
}
