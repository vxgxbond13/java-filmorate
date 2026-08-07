package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Начало");
        testFilm.setDescription("Фильм о снах");
        testFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        testFilm.setDuration(148);
        testFilm.setMpaRatingId(3); // PG-13
    }

    @Test
    @Sql("classpath:data.sql")
    void testCreateFilm() {
        Film created = filmStorage.create(testFilm);

        assertNotNull(created.getId());
        assertThat(created)
                .hasFieldOrPropertyWithValue("name", "Начало")
                .hasFieldOrPropertyWithValue("description", "Фильм о снах")
                .hasFieldOrPropertyWithValue("duration", 148)
                .hasFieldOrPropertyWithValue("mpaRatingId", 3);
    }

    @Test
    @Sql("classpath:data.sql")
    void testFindFilmById() {
        Film created = filmStorage.create(testFilm);

        Film found = filmStorage.findById(created.getId());

        assertThat(found)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", created.getId())
                .hasFieldOrPropertyWithValue("name", "Начало");
    }

    @Test
    @Sql("classpath:data.sql")
    void testFindAllFilms() {
        filmStorage.create(testFilm);

        Film secondFilm = new Film();
        secondFilm.setName("Шрек");
        secondFilm.setDescription("Зелёный огр");
        secondFilm.setReleaseDate(LocalDate.of(2001, 5, 18));
        secondFilm.setDuration(90);
        secondFilm.setMpaRatingId(1);
        filmStorage.create(secondFilm);

        assertThat(filmStorage.findAll()).hasSize(2);
    }

    @Test
    @Sql("classpath:data.sql")
    void testUpdateFilm() {
        Film created = filmStorage.create(testFilm);

        created.setName("Начало (обновлено)");
        created.setDuration(150);
        Film updated = filmStorage.update(created);

        assertThat(updated)
                .hasFieldOrPropertyWithValue("name", "Начало (обновлено)")
                .hasFieldOrPropertyWithValue("duration", 150);
    }

    @Test
    @Sql("classpath:data.sql")
    void testDeleteFilm() {
        Film created = filmStorage.create(testFilm);

        filmStorage.delete(created.getId());

        Film deleted = filmStorage.findById(created.getId());
        assertThat(deleted).isNull();
    }

    @Test
    @Sql("classpath:data.sql")
    void testExistsFilm() {
        Film created = filmStorage.create(testFilm);

        assertThat(filmStorage.exists(created.getId())).isTrue();
        assertThat(filmStorage.exists(999L)).isFalse();
    }
}
