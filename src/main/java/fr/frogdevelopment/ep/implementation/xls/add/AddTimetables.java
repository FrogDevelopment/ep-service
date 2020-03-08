package fr.frogdevelopment.ep.implementation.xls.add;

import fr.frogdevelopment.ep.implementation.xls.model.XlsTimetable;
import java.util.Collection;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class AddTimetables {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddTimetables(DataSource dataSource) {
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("timetables")
                .usingGeneratedKeyColumns("timetable_id");
    }

    public void call(Collection<XlsTimetable> timetables) {
        timetables.forEach(this::add);
    }

    private void add(XlsTimetable timetable) {
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
