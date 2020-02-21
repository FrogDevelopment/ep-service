package fr.frogdevelopment.ep.implementation.schedules;

import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// Fixme SPirng Jdbc Example
@Repository
public class GetSchedules {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Schedule> scheduleRowMapper = (rs, rowNum) -> Schedule.builder()
            .id(rs.getInt("schedule_id"))
            .from(rs.getTimestamp("from_datetime").toLocalDateTime())
            .to(rs.getTimestamp("to_datetime").toLocalDateTime())
            .where(Location.valueOf(rs.getString("location")))
            .teamCode(rs.getString("team_code"))
            .build();

    public GetSchedules(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> call() {
        var sql = "SELECT * FROM schedules ORDER BY from_datetime";

        return jdbcTemplate.query(sql, scheduleRowMapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> byLocation(Location location) {
        var sql = "SELECT * FROM schedules WHERE location = :location";

        var paramSources = new MapSqlParameterSource("location", location.toString());

        return jdbcTemplate.query(sql, paramSources, scheduleRowMapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> byTeam(String teamCode) {
        var sql = "SELECT * FROM schedules WHERE team_code = :teamCode";

        var paramSources = new MapSqlParameterSource("teamCode", teamCode);

        return jdbcTemplate.query(sql, paramSources, scheduleRowMapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> byDay(LocalDate localDate) {
        var sql = "SELECT * FROM schedules WHERE from_datetime BETWEEN :start AND :end";

        var paramSources = new MapSqlParameterSource();
        paramSources.addValue("start", localDate.atStartOfDay());
        paramSources.addValue("end", localDate.plusDays(1).atStartOfDay());

        return jdbcTemplate.query(sql, paramSources, scheduleRowMapper);
    }
}
