package fr.frogdevelopment.ep.implementation.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Volunteer;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class StatsRepository {

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public StatsRepository(ObjectMapper objectMapper,
                           JdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TimeSlot> getTimeSlots() {
        var sql = "SELECT DISTINCT from_datetime, to_datetime FROM schedules ORDER BY from_datetime";
        return jdbcTemplate.query(sql, (rs, rowNum) -> TimeSlot.builder()
                .start(rs.getTimestamp("from_datetime").toLocalDateTime())
                .end(rs.getTimestamp("to_datetime").toLocalDateTime())
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Volunteer> getAllWithSchedules() {
        var sql = "SELECT v.volunteer_ref, v.last_name, v.first_name, v.team_code,"
                + "       json_agg(json_build_object("
                + "               'start', s.from_datetime,"
                + "               'end', s.to_datetime,"
                + "               'location', s.location"
                + "           )) AS schedules"
                + " FROM volunteers v"
                + "         INNER JOIN schedules s ON v.volunteer_ref = s.volunteer_ref"
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
        return schedules;
    }

    @Data
    @Builder
    public static class TimeSlot {

        private LocalDateTime start;
        private LocalDateTime end;
    }
}
