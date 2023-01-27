package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper implements RowMapper<Event> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .type(rs.getString("type"))
                .operation(rs.getString("operation"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(LocalDateTime.parse(rs.getString("timestamp"), formatter))
                .build();
    }
}
