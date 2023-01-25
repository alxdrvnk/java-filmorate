package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.sql.Timestamp;

@Value
@Builder
public class Event {

    @With
    Long id;
    Long userId;
    Long entityId;
    String type;
    String operation;
    Timestamp timestamp;
}
