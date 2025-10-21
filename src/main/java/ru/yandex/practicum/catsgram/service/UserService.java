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

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        validateUser(user);
        if (user.getEmail() == null) {
            throw new ConditionsNotMetException("Email не может быть пустым");
        }
        if (users.containsValue(user)) {
            throw new DuplicatedDataException("Этот Email уже используется");
        }
        if (user.getUsername() == null) {
            user.setUsername(user.getEmail());
        }
        if (user.getPassword() == null) {
            throw new ConditionsNotMetException("Password не может быть пустым");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }


        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (users.containsValue(newUser) && newUser.getEmail().equals(oldUser.getEmail())) {
                throw new DuplicatedDataException("Этот Email уже используется");
            }

            validateUser(newUser);

            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() != null) {
                oldUser.setUsername(newUser.getUsername());
            }
            if (newUser.getPassword() != null) {
                oldUser.setPassword(newUser.getPassword());
            }

            oldUser.setRegistrationDate(newUser.getRegistrationDate());

            return oldUser;
        }
        throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
    }

    private void validateUser(User user) {

        if (user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email не может быть пустым");
        }
        if (user.getUsername().isBlank()) {
            user.setUsername(user.getEmail());
        }
        if (user.getPassword().isBlank()) {
            throw new ConditionsNotMetException("Password не может быть пустым");
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
