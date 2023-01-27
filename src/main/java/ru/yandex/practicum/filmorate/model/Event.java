package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@Value
@Builder
public class Event {

    @With
    Long id;
    Long userId;
    Long entityId;
    String type;
    String operation;
    LocalDateTime timestamp;
}
