package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("type"))
                .operation(rs.getString("operation"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getLong("event_time"))
                .build();
    }
}
