package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component("eventDbStorage")
public class EventsDbStorage implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event create(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id");
        Long eventId = simpleJdbcInsert.executeAndReturnKey(eventToParameters(event)).longValue();
        return event.withEventId(eventId);
    }

    @Override
    public List<Event> getFeedList(Long userId) {
        String query =
                "SELECT * from events AS e " +
                "WHERE e.user_id = ? " +
                "OR e.user_id IN (SELECT fl.friend_id FROM friend_list fl WHERE fl.user_id = ?) AND e.entity_id = ? " +
                "ORDER BY e.event_time";

        return jdbcTemplate.query(query, new EventMapper(), userId, userId, userId);
    }

    private Map<String, Object> eventToParameters(Event event) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", event.getEventId());
        parameters.put("user_id", event.getUserId());
        parameters.put("entity_id", event.getEntityId());
        parameters.put("type", event.getEventType());
        parameters.put("operation", event.getOperation());
        parameters.put("event_time",
                new Timestamp(event.getTimestamp()).toLocalDateTime());
        return parameters;
    }
}
