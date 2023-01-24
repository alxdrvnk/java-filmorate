package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
public class Director {
    @With
    private int id;
    @NonNull
    @NotEmpty
    private String name;
}
