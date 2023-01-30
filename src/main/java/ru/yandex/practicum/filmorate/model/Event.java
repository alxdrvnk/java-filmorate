package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
public class Event {

    @With
    Long eventId;
    Long userId;
    Long entityId;
    String eventType;
    String operation;
    Long timestamp;
}
