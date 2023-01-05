package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Mpa {
    Long id;
    String name;
}
