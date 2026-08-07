package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validate(film);

        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }

        if (!filmStorage.exists(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        return filmStorage.update(film);
    }

    public void delete(Long id) {
        if (!filmStorage.exists(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        filmStorage.delete(id);
    }


    public void addLike(Long filmId, Long userId) {
        log.debug("FilmService: добавление лайка filmId={}, userId={}", filmId, userId);

        Film film = findById(filmId);
        userService.findById(userId);

        if (film.getLikes().contains(userId)) {
            log.warn("FilmService: пользователь {} уже лайкнул фильм {}", userId, filmId);
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        film.getLikes().add(userId);
        filmStorage.update(film);

        log.info("FilmService: пользователь {} лайкнул фильм {}", userId, filmId);
    }


    public void removeLike(Long filmId, Long userId) {
        log.debug("FilmService: удаление лайка filmId={}, userId={}", filmId, userId);

        Film film = findById(filmId);

        if (!film.getLikes().contains(userId)) {
            log.debug("FilmService: пользователь {} не ставил лайк этому фильму", userId);
            return;  // ← возвращаем 200 OK
        }

        film.getLikes().remove(userId);
        filmStorage.update(film);

        log.info("FilmService: пользователь {} убрал лайк с фильма {}", userId, filmId);
    }

    public List<Film> getTopPopularFilms(int count) {
        log.debug("FilmService: получение топ-{} популярных фильмов", count);

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Integer mpaId = film.getMpa().getId();
            if (mpaId < 1 || mpaId > 5) {
                throw new NotFoundException("Рейтинг с id " + mpaId + " не найден");  // ← 404
            }
            film.setMpaRatingId(mpaId);
        }

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            film.setMpaRatingId(film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() < 1 || genre.getId() > 6) {
                    throw new NotFoundException("Жанр с id " + genre.getId() + " не найден");  // ← 404
                }
            }
        }

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Integer mpaId = film.getMpa().getId();
            if (mpaId < 1 || mpaId > 5) {
                throw new ValidationException("MPA рейтинг должен быть от 1 до 5");
            }
            film.setMpaRatingId(mpaId);
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() < 1 || genre.getId() > 6) {
                    throw new ValidationException("Жанр должен быть от 1 до 6");
                }
            }
        }

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        LocalDate minDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }
    }
}
