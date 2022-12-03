package ru.yandex.practicum.filmorate.error;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FilmorateError {
    private int status;
    private List<String> errors;
}
