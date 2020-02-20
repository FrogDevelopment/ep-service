package fr.frogdevelopment.ep.implementation.schedules;

import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetSchedules {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Schedule> scheduleRowMapper = (rs, rowNum) -> Schedule.builder()
            .id(rs.getInt("schedule_id"))
            .from(rs.getTimestamp("from_datetime").toLocalDateTime())
            .to(rs.getTimestamp("to_datetime").toLocalDateTime())
            .where(Location.valueOf(rs.getString("location")))
            .teamCode(rs.getString("team_code"))
            .build();

    public GetSchedules(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> call() {
        var sql = "SELECT * FROM schedules ORDER BY from_datetime";

        return namedParameterJdbcTemplate.query(sql, scheduleRowMapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> forLocation(Location location) {
        var sql = "SELECT * FROM schedules WHERE location = :location ORDER BY from_datetime";

        var paramSources = new MapSqlParameterSource("location", location.toString());

        return namedParameterJdbcTemplate.query(sql, paramSources, scheduleRowMapper);
    }
}
