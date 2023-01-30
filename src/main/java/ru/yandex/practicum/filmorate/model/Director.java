package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
@EqualsAndHashCode
public class Director {
    @With
    int id;
    @NotNull
    @NotBlank
    String name;
}
