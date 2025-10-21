package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        validateUser(user);
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email не может быть пустым");
        }
        if (users.containsValue(user)) {
            throw new DuplicatedDataException("Этот Email уже используется");
        }
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ConditionsNotMetException("Password не может быть пустым");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser == null || newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
        }


        // нормализуем входные значения
        String incomingEmail = newUser.getEmail() != null ? newUser.getEmail().trim() : null;
        String incomingUsername = newUser.getUsername() != null ? newUser.getUsername().trim() : null;
        String incomingPassword = newUser.getPassword(); // пароль лучше не триммить

        if (incomingEmail != null && !Objects.equals(incomingEmail, oldUser.getEmail())) {
            boolean duplicate = users.values().stream()
                    .anyMatch(u -> Objects.equals(u.getEmail(), incomingEmail)
                            && !Objects.equals(u.getId(), newUser.getId()));
            if (duplicate) {
                throw new DuplicatedDataException("Этот Email уже используется");
            }
        }

        // валидации и применение изменений
        if (incomingEmail != null) {
            if (incomingEmail.isBlank()) {
                throw new ConditionsNotMetException("Email не может быть пустым");
            }
            oldUser.setEmail(incomingEmail);
        }

        if (incomingUsername != null) {
            if (incomingUsername.isBlank()) {
                throw new ConditionsNotMetException("Username не может быть пустым");
            }
            oldUser.setUsername(incomingUsername);
        }

        if (incomingPassword != null) {
            if (incomingPassword.isBlank()) {
                throw new ConditionsNotMetException("Password не может быть пустым");
            }
            oldUser.setPassword(incomingPassword);
        }

        // дату регистрации не трогаем
        return oldUser;
    }

    // если всё же оставляешь вспомогательную валидацию
    private void validateUser(User user) {
        // главная правка – сначала проверяем на null
        String username = user.getUsername();
        if (username == null || username.isBlank()) {
            String email = user.getEmail();
            if (email == null || email.isBlank()) {
                throw new ConditionsNotMetException("Email обязателен, если Username пуст");
            }
            user.setUsername(email.trim());
        }
    }



    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
