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

    @Override
    public List<Event> getFeedList(Long userId) {
        String query =
                "SELECT * from events AS e " +
                        "WHERE e.user_id IN " +
                        "(SELECT fl.friend_id FROM friend_list AS fl " +
                        "WHERE fl.user_id = ?" +
                        "UNION " +
                        "SELECT fl.user_id FROM friend_list AS fl " +
                        "WHERE fl.friend_id = ? AND fl.state = true)";
        return jdbcTemplate.query(query, new EventMapper(), userId, userId);
    }

    private Map<String, Object> eventToParameters(Event event) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", event.getId());
        parameters.put("user_id", event.getUserId());
        parameters.put("type", event.getType());
        parameters.put("operation", event.getOperation());
        parameters.put("entity_id", event.getEntityId());
        parameters.put("timestamp", event.getTimestamp());
        return parameters;
    }
}
