package fr.frogdevelopment.ep.application.schedules;

import fr.frogdevelopment.ep.model.Schedule;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
public class AddSchedule {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddSchedule(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("schedule_id")
                .withTableName("schedules");
    }

    public void call(Schedule schedule) {
        var paramSource = new MapSqlParameterSource()
                .addValue("location", schedule.getLocation().name())
                .addValue("timetable_ref", schedule.getTimeTableRef())
                .addValue("volunteer_ref", schedule.getVolunteerRef());

        var returnedKey = simpleJdbcInsert.executeAndReturnKey(paramSource);

        schedule.setId(returnedKey.intValue());
    }

}
