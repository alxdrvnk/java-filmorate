package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    private String name;
    private String description;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate releaseDate;
    private int duration;
}