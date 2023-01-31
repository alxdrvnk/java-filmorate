package ru.yandex.practicum.filmorate.controller.dto;

import lombok.Data;

@Data
public class By{
    private boolean director;
    private boolean title;
    private boolean likes;
    private boolean year;

    public By setBy(String by) {
        if (by.contains("director")) {
            director = true;
        }
        if (by.contains("title")) {
            title = true;
        }
        if (by.contains("likes")) {
            likes = true;
        }
        if (by.contains("year")) {
            year = true;
        }
        return this;
    }

    public By setSortBy(String by){
        return setBy(by);
    }
}
