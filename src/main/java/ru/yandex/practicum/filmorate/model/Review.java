package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Review {
    private Long id;
    @NotNull
    private final User user;
    private final Film film;
    private String content;
    private Boolean isPositive;
    private int useful;
}
