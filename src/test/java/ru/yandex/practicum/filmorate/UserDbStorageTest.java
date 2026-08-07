package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@mail.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    @Sql("classpath:data.sql")
    void testCreateUser() {
        User created = userStorage.create(testUser);

        assertNotNull(created.getId());
        assertThat(created)
                .hasFieldOrPropertyWithValue("email", "test@mail.com")
                .hasFieldOrPropertyWithValue("login", "testuser")
                .hasFieldOrPropertyWithValue("name", "Test User");
    }

    @Test
    @Sql("classpath:data.sql")
    void testFindUserById() {
        User created = userStorage.create(testUser);

        User found = userStorage.findById(created.getId());

        assertThat(found)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", created.getId())
                .hasFieldOrPropertyWithValue("email", "test@mail.com");
    }

    @Test
    @Sql("classpath:data.sql")
    void testFindAllUsers() {
        userStorage.create(testUser);

        User secondUser = new User();
        secondUser.setEmail("second@mail.com");
        secondUser.setLogin("seconduser");
        secondUser.setName("Second User");
        secondUser.setBirthday(LocalDate.of(1999, 5, 15));
        userStorage.create(secondUser);

        assertThat(userStorage.findAll()).hasSize(2);
    }

    @Test
    @Sql("classpath:data.sql")
    void testUpdateUser() {
        User created = userStorage.create(testUser);

        created.setName("Updated Name");
        created.setEmail("updated@mail.com");
        User updated = userStorage.update(created);

        assertThat(updated)
                .hasFieldOrPropertyWithValue("name", "Updated Name")
                .hasFieldOrPropertyWithValue("email", "updated@mail.com");
    }

    @Test
    @Sql("classpath:data.sql")
    void testDeleteUser() {
        User created = userStorage.create(testUser);

        userStorage.delete(created.getId());

        User deleted = userStorage.findById(created.getId());
        assertThat(deleted).isNull();
    }

    @Test
    @Sql("classpath:data.sql")
    void testExistsUser() {
        User created = userStorage.create(testUser);

        assertThat(userStorage.exists(created.getId())).isTrue();
        assertThat(userStorage.exists(999L)).isFalse();
    }
}
