package fr.frogdevelopment.ep.implementation.timetables;

import fr.frogdevelopment.ep.model.Timetable;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AddTimetable {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddTimetable(DataSource dataSource) {
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("timetables")
                .usingGeneratedKeyColumns("timetable_id");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void call(Timetable timetable) {
        simpleJdbcInsert.execute(new MapSqlParameterSource()
                .addValue("timetable_ref", timetable.getRef())
                .addValue("day_of_week", timetable.getDayOfWeek())
                .addValue("start_time", timetable.getStart())
                .addValue("end_time", timetable.getEnd())
                .addValue("expected_bracelet", timetable.getExpectedBracelet())
                .addValue("expected_fouille", timetable.getExpectedFouille())
                .addValue("expected_litiges", timetable.getExpectedLitiges())
                .addValue("description", timetable.getDescription()));
    }
}
