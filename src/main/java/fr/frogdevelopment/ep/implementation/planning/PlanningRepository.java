package fr.frogdevelopment.ep.implementation.planning;

import fr.frogdevelopment.ep.model.Planning;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PlanningRepository {

    private static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ISO_TIME;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public PlanningRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("planning")
                .usingGeneratedKeyColumns("planning_id");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insert(Planning planning) {
        simpleJdbcInsert.execute(new MapSqlParameterSource()
                .addValue("start_datetime", planning.getStart())
                .addValue("end_datetime", planning.getEnd())
                .addValue("expected_bracelet", planning.getExpectedBracelet())
                .addValue("expected_fouille", planning.getExpectedFouille())
                .addValue("expected_litiges", planning.getExpectedLitiges())
                .addValue("description", planning.getDescription()));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<DayOfWeek, List<Planning>> getPlanning() {
        var sql = "SELECT * FROM planning";

        return jdbcTemplate.query(sql, rs -> {
            var result = new HashMap<DayOfWeek, List<Planning>>();

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
                        .add(Planning.builder()
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
