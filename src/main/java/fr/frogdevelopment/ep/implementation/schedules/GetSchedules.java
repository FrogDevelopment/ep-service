package fr.frogdevelopment.ep.implementation.schedules;

import fr.frogdevelopment.ep.model.Schedule;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetSchedules {

    private final JdbcTemplate jdbcTemplate;

    public GetSchedules(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Schedule> call() {
        var sql = "SELECT * FROM schedules ORDER BY from_datetime";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Schedule.builder()
                .id(rs.getInt("schedule_id"))
                .from(rs.getTimestamp("from_datetime").toLocalDateTime())
                .to(rs.getTimestamp("to_datetime").toLocalDateTime())
                .where(rs.getString("location"))
                .teamCode(rs.getString("team_code"))
                .build());
    }
}
