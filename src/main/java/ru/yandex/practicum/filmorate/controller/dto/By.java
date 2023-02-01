package ru.yandex.practicum.filmorate.controller.dto;

import lombok.Data;

@Data
public class By{
    private boolean director;
    private boolean title;


    public By setBy(String by) {
        if (by.contains("director")) {
            director = true;
        }
        if (by.contains("title")) {
            title = true;
        }
        return this;
    }
}
