package fr.frogdevelopment.ep.implementation.timetables;

import fr.frogdevelopment.ep.model.Timetable;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
public class AddTimetable {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddTimetable(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("timetable_id")
                .withTableName("timetables");
    }

    public void call(Timetable timetable) {
        var paramSource = new MapSqlParameterSource()
                .addValue("location", timetable.getLocation().name())
                .addValue("schedules_ref", timetable.getScheduleRef())
                .addValue("volunteer_ref", timetable.getVolunteerRef());

        var returnedKey = simpleJdbcInsert.executeAndReturnKey(paramSource);

        timetable.setId(returnedKey.intValue());
    }

}
