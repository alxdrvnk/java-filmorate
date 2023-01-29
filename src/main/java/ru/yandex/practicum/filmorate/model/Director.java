package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class Director {
    @With
    private int id;
    @NotBlank
    private String name;
}
