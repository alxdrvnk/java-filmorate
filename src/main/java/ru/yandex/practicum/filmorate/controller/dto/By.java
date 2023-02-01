package ru.yandex.practicum.filmorate.controller.dto;

import lombok.Data;

import java.util.Arrays;

@Data
public class By {
    private boolean director;
    private boolean title;


    public By setBy(String by) {
        by = by.toLowerCase();
        Arrays.stream(by.split(",")).forEach(s -> {
            if (s.equals("director")) {
                director = true;
            }
            if (s.equals("title")) {
                title = true;
            }
        });
        return this;
    }
}
