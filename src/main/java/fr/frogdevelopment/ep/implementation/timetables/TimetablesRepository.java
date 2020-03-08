package fr.frogdevelopment.ep.implementation.timetables;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.valueOf;

import fr.frogdevelopment.ep.model.Timetable;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
    public LocalDate getEdition() {
        var sql = "SELECT day_date FROM edition WHERE day_of_week = 'FRIDAY'";

        try {
            return jdbcTemplate.getJdbcTemplate().queryForObject(sql, LocalDate.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setEdition(LocalDate localDate) {
        setEdition(FRIDAY, localDate);
        setEdition(SATURDAY, localDate.plusDays(1));
        setEdition(SUNDAY, localDate.plusDays(2));
    }

    private void setEdition(DayOfWeek dayOfWeek, LocalDate localDate) {
        var sql = "INSERT INTO edition (day_of_week, day_date) VALUES (:dayOfWeek,:dateTime)"
                + " ON CONFLICT ON CONSTRAINT unique_edition_day_of_week"
                + " DO UPDATE SET day_date = :dateTime";

        var paramSource = new MapSqlParameterSource();
        paramSource.addValue("dayOfWeek", dayOfWeek.name());
        paramSource.addValue("dateTime", Date.valueOf(localDate));

        jdbcTemplate.update(sql, paramSource);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Timetable> getPlanning() {
        var sql = "SELECT e.day_date, t.*"
                + " FROM timetables t"
                + " INNER JOIN edition e ON t.day_of_week = e.day_of_week";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            var localDate = rs.getDate("day_date").toLocalDate();
            var dayOfWeek = valueOf(rs.getString("day_of_week"));
            var startTime = rs.getTime("start_time").toLocalTime();
            var endTime = rs.getTime("end_time").toLocalTime();
            var expectedBracelet = rs.getInt("expected_bracelet");
            var expectedFouille = rs.getInt("expected_fouille");
            var expectedLitiges = rs.getInt("expected_litiges");

            // computed for UI
            var startDateTime = localDate.atTime(startTime);
            var endDateTime = localDate.atTime(endTime);
            if (endDateTime.isBefore(startDateTime)) {
                endDateTime = endDateTime.plusDays(1);
            }
            var title = String.format("%s - %s", startTime.format(ISO_TIME), endTime.format(ISO_TIME));
            var duration = (double) Duration.between(startDateTime, endDateTime).toMinutes() / 60;
            var expectedTotal = expectedBracelet + expectedFouille + expectedLitiges;

            return Timetable.builder()
                    .id(rs.getInt("timetable_id"))
                    .ref(rs.getString("timetable_ref"))
                    .dayOfWeek(dayOfWeek)
                    .start(startTime)
                    .end(endTime)
                    .expectedBracelet(expectedBracelet)
                    .expectedFouille(expectedFouille)
                    .expectedLitiges(expectedLitiges)
                    .description(rs.getString("description"))
                    .title(title)
                    .duration(duration)
                    .expectedTotal(expectedTotal)
                    .build();
        });
    }
}
