package fr.frogdevelopment.ep.implementation.schedules;

import fr.frogdevelopment.ep.model.Location;
import fr.frogdevelopment.ep.model.Schedule;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SchedulesRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Schedule> scheduleRowMapper = (rs, rowNum) -> Schedule.builder()
//            .start(rs.getTime("from_datetime").toLocalTime())
//            .end(rs.getTime("to_datetime").toLocalTime())
            .location(Location.valueOf(rs.getString("location")))
//            .teamCode(rs.getString("team_code"))
            .build();

    public SchedulesRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> getGroupedSchedulesByTeam() {
        var sql = "SELECT from_datetime,"
                + " to_datetime,"
                + " location,"
                + " team_code,"
                + " array_agg(volunteer_ref) AS volunteers"
                + " FROM schedules"
                + " GROUP BY from_datetime, to_datetime, location, team_code"
                + " ORDER BY from_datetime;";

        return jdbcTemplate.query(sql, scheduleRowMapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> getGroupedSchedulesByTeam(String teamCode) {
        var sql = "SELECT from_datetime,"
                + " to_datetime,"
                + " location,"
                + " team_code,"
                + " array_agg(volunteer_ref) AS volunteers"
                + " FROM schedules"
                + " WHERE team_code = :teamCode"
                + " GROUP BY from_datetime, to_datetime, location, team_code"
                + " ORDER BY from_datetime;";

        var paramSource = new MapSqlParameterSource("teamCode", teamCode);

        return jdbcTemplate.query(sql, paramSource, scheduleRowMapper);
    }

}
