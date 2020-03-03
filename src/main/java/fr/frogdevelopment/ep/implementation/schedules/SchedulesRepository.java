package fr.frogdevelopment.ep.implementation.schedules;

import fr.frogdevelopment.ep.model.Schedule;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SchedulesRepository {

    private static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ISO_TIME;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SchedulesRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<DayOfWeek, List<Schedule>> getPlanning() {
        var sql = "SELECT * FROM planning";

        return jdbcTemplate.query(sql, rs -> {
            var result = new HashMap<DayOfWeek, List<Schedule>>();

            while (rs.next()) {
                var start = rs.getTimestamp("start_datetime").toLocalDateTime();
                var end = rs.getTimestamp("end_datetime").toLocalDateTime();
                var expectedBracelet = rs.getInt("expected_bracelet");
                var expectedFouille = rs.getInt("expected_fouille");
                var expectedLitiges = rs.getInt("expected_litiges");

                // computed for UI
                var title = String.format("%s - %s", start.format(ISO_TIME), end.format(ISO_TIME));
                var duration = (double) Duration.between(start, end).toMinutes() / 60;
                var expectedTotal = expectedBracelet + expectedFouille + expectedLitiges;

                result.computeIfAbsent(start.getDayOfWeek(), dayOfWeek -> new ArrayList<>())
                        .add(Schedule.builder()
                                .id(rs.getInt("planning_id"))
                                .start(start)
                                .end(end)
                                .expectedBracelet(expectedBracelet)
                                .expectedFouille(expectedFouille)
                                .expectedLitiges(expectedLitiges)
                                .description(rs.getString("description"))
                                .title(title)
                                .duration(duration)
                                .expectedTotal(expectedTotal)
                                .build());
            }

            return result;
        });
    }
}
