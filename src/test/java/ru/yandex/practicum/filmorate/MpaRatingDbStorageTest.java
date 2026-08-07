package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mapper.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpaRating.MpaRatingDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRatingDbStorage.class, MpaRatingRowMapper.class})
class MpaRatingDbStorageTest {

    private final MpaRatingDbStorage mpaRatingStorage;

    @Test
    @Sql("classpath:data.sql")
    void testFindAllMpaRatings() {
        Collection<MpaRating> ratings = mpaRatingStorage.findAll();

        assertThat(ratings).hasSize(5);
        assertThat(ratings).extracting(MpaRating::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    @Sql("classpath:data.sql")
    void testFindMpaRatingById() {
        Optional<MpaRating> rating = mpaRatingStorage.findById(1);

        assertThat(rating).isPresent();
        assertThat(rating.get().getName()).isEqualTo("G");
    }

    @Test
    @Sql("classpath:data.sql")
    void testFindMpaRatingByIdNotFound() {
        Optional<MpaRating> rating = mpaRatingStorage.findById(999);

        assertThat(rating).isEmpty();
    }
}
