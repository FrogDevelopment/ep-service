package fr.frogdevelopment.ep.implementation.timetables;

import fr.frogdevelopment.ep.model.Location;
import fr.frogdevelopment.ep.model.Timetable;
import java.util.Arrays;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TimetablesRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Timetable> scheduleRowMapper = (rs, rowNum) -> Timetable.builder()
            .start(rs.getTimestamp("from_datetime").toLocalDateTime())
            .end(rs.getTimestamp("to_datetime").toLocalDateTime())
            .location(Location.valueOf(rs.getString("location")))
            .teamCode(rs.getString("team_code"))
            .volunteers(Arrays.asList(rs.getArray("volunteers").getArray()))
            .build();

    public TimetablesRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Timetable> getGroupedSchedulesByTeam() {
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
    public List<Timetable> getGroupedSchedulesByTeam(String teamCode) {
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void changeLocation(Timetable timetable) {
        var sql = "UPDATE schedules"
                + " SET location = :location"
                + " WHERE from_datetime = :from_datetime"
                + " AND to_datetime = :to_datetime"
                + " AND team_code = :team_code"
                + " AND volunteer_ref IN (:volunteer_refs);";

        var paramSource = new MapSqlParameterSource();
        paramSource.addValue("from_datetime", timetable.getStart());
        paramSource.addValue("to_datetime", timetable.getEnd());
        paramSource.addValue("team_code", timetable.getTeamCode());
        paramSource.addValue("volunteer_refs", timetable.getVolunteers());
        paramSource.addValue("location", timetable.getLocation().name());

        jdbcTemplate.update(sql, paramSource);
    }

}
