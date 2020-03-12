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

    private final RowMapper<Schedule> scheduleRowMapper = (rs, rowNum) -> {
        var dayDate = rs.getDate("day_date").toLocalDate();
        var startTime = rs.getTime("start_time").toLocalTime();
        var endTime = rs.getTime("end_time").toLocalTime();
        var start = dayDate.atTime(startTime);
        var end = dayDate.atTime(endTime);
        if (end.isBefore(start)) {
            end = end.plusDays(1);
        }
        return Schedule.builder()
                .start(start)
                .end(end)
                .location(Location.valueOf(rs.getString("location")))
                .teamCode(rs.getString("team_code"))
                .build();
    };

    public SchedulesRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> getGroupedSchedulesByTeam() {
        var sql = "SELECT DISTINCT"
                + " e.day_date,"
                + " t.start_time,"
                + " t.end_time,"
                + " s.location,"
                + " v.team_code"
                + " FROM schedules s"
                + " INNER JOIN volunteers v ON s.volunteer_ref = v.volunteer_ref"
                + " INNER JOIN timetables t ON s.timetable_ref = t.timetable_ref"
                + " INNER JOIN edition e ON t.day_of_week = e.day_of_week"
                + " ORDER BY e.day_date, t.start_time;";

        return jdbcTemplate.query(sql, scheduleRowMapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> getGroupedSchedulesByTeam(String teamCode) {
        var sql = "SELECT DISTINCT"
                + " e.day_date,"
                + " t.start_time,"
                + " t.end_time,"
                + " s.location,"
                + " v.team_code"
                + " FROM schedules s"
                + " INNER JOIN timetables t ON s.timetable_ref = t.timetable_ref"
                + " INNER JOIN volunteers v ON s.volunteer_ref = v.volunteer_ref"
                + " INNER JOIN edition e ON t.day_of_week = e.day_of_week"
                + " WHERE v.team_code = :teamCode"
                + " ORDER BY e.day_date, t.start_time;";

        var paramSource = new MapSqlParameterSource("teamCode", teamCode);

        return jdbcTemplate.query(sql, paramSource, scheduleRowMapper);
    }

}
