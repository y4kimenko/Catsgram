package ru.yandex.practicum.catsgram.exception;

public class InvalidParametersQueryString extends RuntimeException {
    public InvalidParametersQueryString(String message) {
        super(message);
    }
}
