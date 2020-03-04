package fr.frogdevelopment.ep.implementation.timetables;

import fr.frogdevelopment.ep.model.Timetable;
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
public class TimetablesRepository {

    private static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ISO_TIME;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TimetablesRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<DayOfWeek, List<Timetable>> getPlanning() {
        var sql = "SELECT * FROM timetables";

        return jdbcTemplate.query(sql, rs -> {
            var result = new HashMap<DayOfWeek, List<Timetable>>();

            while (rs.next()) {
                var dayOfWeek = DayOfWeek.valueOf(rs.getString("day_of_week"));
                var start = rs.getTime("start_time").toLocalTime();
                var end = rs.getTime("end_time").toLocalTime();
                var expectedBracelet = rs.getInt("expected_bracelet");
                var expectedFouille = rs.getInt("expected_fouille");
                var expectedLitiges = rs.getInt("expected_litiges");

                // computed for UI
                var title = String.format("%s - %s", start.format(ISO_TIME), end.format(ISO_TIME));
                var duration = (double) Duration.between(start, end).toMinutes() / 60;
                var expectedTotal = expectedBracelet + expectedFouille + expectedLitiges;

                result.computeIfAbsent(dayOfWeek, key -> new ArrayList<>())
                        .add(Timetable.builder()
                                .id(rs.getInt("timetable_id"))
                                .ref(rs.getString("timetable_ref"))
                                .dayOfWeek(dayOfWeek)
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
