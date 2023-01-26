package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {

    //TODO: Add LocalDateTime Formatting
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .type(rs.getString("type"))
                .operation(rs.getString("operation"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("timestamp"))
                .build();
    }
}
