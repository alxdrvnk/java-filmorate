package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component("eventDbStorage")
public class EventsDbStorage implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event create(Event event) {
        String query = "INSERT INTO events(user_id, type, operation, entity_id, event_time) " +
                "VALUES(?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"id"});
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, event.getEventType());
            stmt.setString(3, event.getOperation());
            stmt.setLong(4, event.getEntityId());
            stmt.setLong(5, event.getTimestamp());
            return stmt;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        return event.withEventId(id);
    }

    @Override
    public List<Event> getFeedList(Long userId) {
        String query =
                "SELECT * from events AS e " +
                        "WHERE e.user_id IN " +
                        "(SELECT fl.friend_id FROM friend_list AS fl " +
                        "WHERE fl.user_id = ? " +
                        "UNION " +
                        "SELECT fl.user_id FROM friend_list AS fl " +
                        "WHERE fl.friend_id = ? AND fl.state = true) OR e.user_id = ?";
        return jdbcTemplate.query(query, new EventMapper(), userId, userId, userId);
    }
}
