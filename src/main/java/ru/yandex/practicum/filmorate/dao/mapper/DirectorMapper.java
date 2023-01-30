package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = Director.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .build();
        return director;
    }
}
