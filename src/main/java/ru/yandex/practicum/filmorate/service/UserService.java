package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;


    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);

        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }

        if (!userStorage.exists(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        return userStorage.update(user);
    }

    public void delete(Long id) {
        if (!userStorage.exists(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userStorage.delete(id);
    }


    public void addFriend(Long userId, Long friendId) {
        log.debug("UserService: добавление в друзья userId={}, friendId={}", userId, friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = findById(userId);
        User friend = findById(friendId);

        if (user.getFriends().contains(friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("UserService: пользователи {} и {} стали друзьями", userId, friendId);
    }


    public void removeFriend(Long userId, Long friendId) {
        log.debug("UserService: удаление из друзей userId={}, friendId={}", userId, friendId);

        User user = findById(userId);
        User friend = findById(friendId);

        if (!user.getFriends().contains(friendId)) {
            log.debug("UserService: пользователи {} и {} не являются друзьями, удаление игнорируется", userId, friendId);
            return;
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("UserService: пользователи {} и {} больше не друзья", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        log.debug("UserService: получение списка друзей userId={}", userId);

        User user = findById(userId);

        if (user.getFriends().isEmpty()) {
            return new HashSet<>();
        }

        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        log.debug("UserService: получение общих друзей userId={}, otherUserId={}", userId, otherUserId);

        User user = findById(userId);
        User otherUser = findById(otherUserId);

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }


    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
