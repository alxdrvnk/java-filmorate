package ru.yandex.practicum.filmorate.error;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class FilmorateError {
    int status;
    List<String> errors;
}
