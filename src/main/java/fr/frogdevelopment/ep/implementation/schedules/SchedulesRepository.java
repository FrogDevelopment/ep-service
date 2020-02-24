package fr.frogdevelopment.ep.implementation.schedules;

import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import java.util.Arrays;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SchedulesRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

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

        return jdbcTemplate.query(sql, (rs, rowNum) -> Schedule.builder()
                .start(rs.getTimestamp("from_datetime").toLocalDateTime())
                .end(rs.getTimestamp("to_datetime").toLocalDateTime())
                .location(Location.valueOf(rs.getString("location")))
                .teamCode(rs.getString("team_code"))
                .volunteers(Arrays.asList((String[]) rs.getArray("volunteers").getArray()))
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void changeLocation(Schedule schedule) {
        var sql = "UPDATE schedules"
                + " SET location = :location"
                + " WHERE from_datetime = :from_datetime"
                + " AND to_datetime = :to_datetime"
                + " AND team_code = :team_code"
                + " AND volunteer_ref in (:volunteer_refs);";

        var paramSource = new MapSqlParameterSource();
        paramSource.addValue("from_datetime", schedule.getStart());
        paramSource.addValue("to_datetime", schedule.getEnd());
        paramSource.addValue("team_code", schedule.getTeamCode());
        paramSource.addValue("volunteer_refs", schedule.getVolunteers());
        paramSource.addValue("location", schedule.getLocation().name());

        jdbcTemplate.update(sql, paramSource);
    }

}
