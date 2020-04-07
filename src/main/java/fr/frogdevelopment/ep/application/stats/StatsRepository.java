package fr.frogdevelopment.ep.application.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Volunteer;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class StatsRepository {

    private final ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StatsRepository(ObjectMapper objectMapper,
                           NamedParameterJdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TimeSlot> getTimeSlots() {
        var sql = "SELECT day_of_week, start_time, end_time"
                + " FROM timetables"
                + " ORDER BY day_of_week, start_time";
        return jdbcTemplate.query(sql, (rs, rowNum) -> TimeSlot.builder()
                .dayOfWeek(DayOfWeek.valueOf(rs.getString("day_of_week")))
                .start(rs.getTime("start_time").toLocalTime())
                .end(rs.getTime("end_time").toLocalTime())
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Volunteer> getVolunteersWithSchedules() {
        var sql = "SELECT v.volunteer_ref, v.last_name, v.first_name, v.team_code,"
                + "       json_agg(json_build_object("
                + "               'dayOfWeek', t.day_of_week,"
                + "               'start', e.day_date + t.start_time,"
                + "               'end', e.day_date + t.end_time,"
                + "               'location', s.location"
                + "           )) AS schedules"
                + " FROM volunteers v"
                + "         INNER JOIN schedules s ON v.volunteer_ref = s.volunteer_ref"
                + "         INNER JOIN timetables t ON s.timetable_ref = t.timetable_ref"
                + "         INNER JOIN edition e ON t.day_of_week = e.day_of_week"
                + " GROUP BY v.volunteer_ref, v.last_name, v.first_name, v.team_code"
                + " ORDER BY v.last_name, v.first_name;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Volunteer.builder()
                .ref(rs.getString("volunteer_ref"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .teamCode(rs.getString("team_code"))
                .schedules(getSchedules(rs.getString("schedules")))
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TeamStats> getTeamsWithSchedules() {
        var sql = "SELECT v.team_code,"
                + "       json_agg(json_build_object("
                + "               'dayOfWeek', t.day_of_week,"
                + "               'start', e.day_date + t.start_time,"
                + "               'end', e.day_date + t.end_time,"
                + "               'location', s.location"
                + "           )) AS schedules"
                + " FROM volunteers v"
                + "         INNER JOIN schedules s ON v.volunteer_ref = s.volunteer_ref"
                + "         INNER JOIN timetables t ON s.timetable_ref = t.timetable_ref"
                + "         INNER JOIN edition e ON t.day_of_week = e.day_of_week"
                + " GROUP BY v.team_code;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> TeamStats.builder()
                .code(rs.getString("team_code"))
                .schedules(getSchedules(rs.getString("schedules")))
                .build());
    }

    private Set<Schedule> getSchedules(String schedulesJson) {
        Set<Schedule> schedules = Collections.emptySet();
        if (StringUtils.isNotBlank(schedulesJson)) {
            try {
                schedules = objectMapper.readValue(schedulesJson, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                log.error("Error schedules", e);
            }
        }
        schedules.stream()
                .filter(schedule -> schedule.getEnd().isBefore(schedule.getStart()))
                .forEach(schedule -> schedule.setEnd(schedule.getEnd().plusDays(1)));
        return schedules;
    }

}
