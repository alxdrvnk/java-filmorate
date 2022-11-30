package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NonNull
    private String name;
    @NonNull
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate releaseDate;
    @NonNull
    private int duration;
}