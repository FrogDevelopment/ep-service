package fr.frogdevelopment.ep.implementation.timetables;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.valueOf;

import fr.frogdevelopment.ep.model.Timetable;
import java.sql.Date;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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
    public void updateTimetable(Timetable timetable) {
        var sql = "UPDATE timetables SET"
                + " start_time = :startTime,"
                + " end_time = :endTime,"
                + " day_of_week = :dayOfWeek,"
                + " expected_bracelet = :expectedBracelet,"
                + " expected_fouille = :expectedFouille,"
                + " expected_litiges = :expectedLitiges,"
                + " description = :description"
                + " WHERE timetable_ref = :ref";

        var paramSource = new BeanPropertySqlParameterSource(timetable);
        paramSource.registerSqlType("dayOfWeek", Types.VARCHAR);
        jdbcTemplate.update(sql, paramSource);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertTimetable(Timetable timetable) {
        var parameterSource = new MapSqlParameterSource()
                .addValue("timetable_ref", UUID.randomUUID().toString())
                .addValue("day_of_week", timetable.getDayOfWeek())
                .addValue("start_time", timetable.getStartTime())
                .addValue("end_time", timetable.getEndTime())
                .addValue("expected_bracelet", timetable.getExpectedBracelet())
                .addValue("expected_fouille", timetable.getExpectedFouille())
                .addValue("expected_litiges", timetable.getExpectedLitiges())
                .addValue("description", timetable.getDescription());

        new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("timetables")
                .usingGeneratedKeyColumns("timetable_id")
                .execute(parameterSource);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Timetable> getPlanning() {
        var sql = "SELECT e.day_date,"
                + "       t.*,"
                + "       sum (CASE WHEN s.location = 'BRACELET' THEN 1 ELSE 0 END) AS actual_bracelet,"
                + "       sum (CASE WHEN s.location = 'FOUILLES' THEN 1 ELSE 0 END) AS actual_fouille,"
                + "       sum (CASE WHEN s.location = 'LITIGES' THEN 1 ELSE 0 END) AS actual_litiges"
                + " FROM timetables t"
                + "         INNER JOIN edition e ON t.day_of_week = e.day_of_week"
                + "         LEFT JOIN schedules s ON t.timetable_ref = s.timetable_ref"
                + " GROUP BY e.day_date, t.timetable_id, t.day_of_week, t.start_time, t.end_time, t.description"
                + " ORDER BY e.day_date, t.start_time";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            var localDate = rs.getDate("day_date").toLocalDate();
            var dayOfWeek = valueOf(rs.getString("day_of_week"));
            var startTime = rs.getTime("start_time").toLocalTime();
            var endTime = rs.getTime("end_time").toLocalTime();
            var expectedBracelet = rs.getInt("expected_bracelet");
            var expectedFouille = rs.getInt("expected_fouille");
            var expectedLitiges = rs.getInt("expected_litiges");
            var actualBracelet = rs.getInt("actual_bracelet");
            var actualFouille = rs.getInt("actual_fouille");
            var actualLitiges = rs.getInt("actual_litiges");

            // computed for UI
            var startDateTime = localDate.atTime(startTime);
            var endDateTime = localDate.atTime(endTime);
            if (endDateTime.isBefore(startDateTime)) {
                endDateTime = endDateTime.plusDays(1);
            }
            var title = String.format("%s - %s", startTime.format(ISO_TIME), endTime.format(ISO_TIME));
            var duration = Duration.between(startDateTime, endDateTime);
            var hoursPart = duration.toHoursPart();
            var minutesPart = duration.minusHours(hoursPart).toMinutesPart();
            var expectedTotal = expectedBracelet + expectedFouille + expectedLitiges;
            var actualTotal = actualBracelet + actualFouille + actualLitiges;

            return Timetable.builder()
                    .ref(rs.getString("timetable_ref"))
                    .dayOfWeek(dayOfWeek)
                    .startTime(startTime)
                    .endTime(endTime)
                    .expectedBracelet(expectedBracelet)
                    .expectedFouille(expectedFouille)
                    .expectedLitiges(expectedLitiges)
                    .description(rs.getString("description"))
                    .title(title)
                    .duration(String.format("%sh%02dmin", hoursPart, minutesPart))
                    .expectedTotal(expectedTotal)
                    .actualBracelet(actualBracelet)
                    .actualFouille(actualFouille)
                    .actualLitiges(actualLitiges)
                    .actualTotal(actualTotal)
                    .build();
        });
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Timetable timetable) {
        var paramSource = new MapSqlParameterSource("ref", timetable.getRef());
        jdbcTemplate.update("DELETE FROM schedules WHERE timetable_ref = :ref", paramSource);
        jdbcTemplate.update("DELETE FROM timetables WHERE timetable_ref = :ref", paramSource);
    }

}
