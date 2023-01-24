package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

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
        return event.withId(eventId);
    }

    private Map<String, Object> eventToParameters(Event event) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", event.getId());
        parameters.put("userId", event.getUserId());
        parameters.put("type", event.getType());
        parameters.put("operation", event.getOperation());
        parameters.put("entityId", event.getEntityId());
        parameters.put("useful", event.getUseful());
        parameters.put("timestamp", event.getTimestamp());
        return parameters;
    }

    @Override
    public Optional<Event> getBy(Long id) {
        String query =
                "SELECT * FROM events WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new EventMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public List<Event> getAll() {
        String query = "SELECT * FROM events";
        return jdbcTemplate.query(query, new EventMapper());
    }

    @Override
    public Event update(Event event) {
        String query = "UPDATE events SET userId = ?, type = ?, operation = ?, entityId = ?, useful = ?, timestamp = ?";
        jdbcTemplate.update(query,
                event.getUserId(),
                event.getType(),
                event.getOperation(),
                event.getEntityId(),
                event.getUseful(),
                event.getTimestamp());
        return event;
    }

    @Override
    public int deleteBy(Long id) {
        String query = "DELETE FROM event WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }
}
