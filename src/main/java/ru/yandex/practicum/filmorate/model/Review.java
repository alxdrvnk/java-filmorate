package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {
    private Long id;
    private final User user;
    private final Film film;
    private String content;
    private Boolean isPositive;
    private int useful;
}
