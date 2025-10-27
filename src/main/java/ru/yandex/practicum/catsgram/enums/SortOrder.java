package ru.yandex.practicum.catsgram.enums;

import ru.yandex.practicum.catsgram.exception.InvalidParametersQueryString;

public enum SortOrder {
    ASCENDING, DESCENDING;

    // Преобразует строку в элемент перечисления
    public static SortOrder from(String order) {
        return switch (order.toLowerCase()) {
            case "ascending", "asc" -> ASCENDING;
            case "descending", "desc" -> DESCENDING;
            default -> throw new InvalidParametersQueryString("sort must be asc or desc");
        };
    }
}

