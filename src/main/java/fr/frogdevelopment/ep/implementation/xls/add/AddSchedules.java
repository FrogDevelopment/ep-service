package fr.frogdevelopment.ep.implementation.xls.add;

import static fr.frogdevelopment.ep.model.Location.AUTRES;
import static fr.frogdevelopment.ep.model.Location.BRACELET;
import static fr.frogdevelopment.ep.model.Location.FOUILLES;
import static fr.frogdevelopment.ep.model.Location.LITIGES;

import fr.frogdevelopment.ep.implementation.xls.model.XlsSchedule;
import fr.frogdevelopment.ep.model.Location;
import java.util.Collection;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.MANDATORY)
public class AddSchedules {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddSchedules(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("schedule_id")
                .withTableName("schedules");
    }

    public void call(Collection<XlsSchedule> schedules) {
        schedules.forEach(this::add);
    }

    private void add(XlsSchedule schedule) {
        var paramSource = new MapSqlParameterSource()
                .addValue("location", fromCode(schedule.getLocation()).name())
                .addValue("timetable_ref", schedule.getTimetableRef())
                .addValue("volunteer_ref", schedule.getVolunteerRef());

        simpleJdbcInsert.execute(paramSource);
    }

    private static Location fromCode(String code) {
        switch (code) {
            case "F":
                return FOUILLES;
            case "B":
                return BRACELET;
            case "L":
                return LITIGES;
            default:
                return AUTRES;
        }
    }
}
