package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
@EqualsAndHashCode
public class Director {
    @With
    private int id;
    @NotNull
    @NotBlank
    private String name;
}
