package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.utils.FilmorateEventOperation;
import ru.yandex.practicum.filmorate.utils.FilmorateEventType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventsService {

    private final EventDao storage;

    public Event create(Long userId, Long entityId, FilmorateEventType type, FilmorateEventOperation operation) {

        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(String.valueOf(type))
                .operation(String.valueOf(operation))
                .timestamp(Timestamp.valueOf(LocalDateTime.now()).getTime())
                .build();
        event = storage.create(event);
        log.info(String.format("EventHandler: Add Event. Data: %s", event));
        return event;
    }

    public List<Event> getFeed(Long userId) {
        return storage.getFeedList(userId);
    }
}
