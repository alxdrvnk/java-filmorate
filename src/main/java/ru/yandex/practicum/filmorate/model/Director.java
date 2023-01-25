package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class Director {
    @With
    private int id;
    @NonNull
    @NotBlank
    private String name;
}
