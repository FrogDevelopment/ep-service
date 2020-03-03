package fr.frogdevelopment.ep.implementation.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.frogdevelopment.ep.model.Timetable;
import fr.frogdevelopment.ep.model.Volunteer;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

        return jdbcTemplate.query(sql, volunteerWithSchedulesRowMapper());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Volunteer> getWithSchedules(String teamCode) {
        var sql = "SELECT v.volunteer_ref, v.last_name, v.first_name, v.team_code,"
                + "       json_agg(json_build_object("
                + "               'start', s.from_datetime,"
                + "               'end', s.to_datetime,"
                + "               'location', s.location"
                + "           )) AS schedules"
                + " FROM volunteers v"
                + "         INNER JOIN schedules s ON v.volunteer_ref = s.volunteer_ref"
                + " WHERE s.team_code = :teamCode"
                + " GROUP BY v.volunteer_ref, v.last_name, v.first_name, v.team_code"
                + " ORDER BY v.last_name, v.first_name;";

        var paramSources = new MapSqlParameterSource("teamCode", teamCode);

        return jdbcTemplate.query(sql, paramSources, volunteerWithSchedulesRowMapper());
    }

    private RowMapper<Volunteer> volunteerWithSchedulesRowMapper() {
        return (rs, rowNum) -> Volunteer.builder()
                .ref(rs.getString("volunteer_ref"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .teamCode(rs.getString("team_code"))
                .timetables(getSchedules(rs.getString("timetables")))
                .build();
    }

    private Set<Timetable> getSchedules(String schedulesJson) {
        Set<Timetable> timetables = Collections.emptySet();
        if (StringUtils.isNotBlank(schedulesJson)) {
            try {
                timetables = objectMapper.readValue(schedulesJson, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                log.error("Error timetables", e);
            }
        }
        return timetables;
    }

    @Data
    @Builder
    public static class TimeSlot {

        private LocalDateTime start;
        private LocalDateTime end;
    }
}
