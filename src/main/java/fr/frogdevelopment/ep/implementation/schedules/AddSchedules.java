package fr.frogdevelopment.ep.implementation.schedules;

import fr.frogdevelopment.ep.model.Schedule;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AddSchedules {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddSchedules(DataSource dataSource) {
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("schedules")
                .usingGeneratedKeyColumns("schedules_id");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void call(Schedule schedule) {
        simpleJdbcInsert.execute(new MapSqlParameterSource()
                .addValue("schedules_ref", schedule.getRef())
                .addValue("day_of_week", schedule.getDayOfWeek())
                .addValue("start_time", schedule.getStart())
                .addValue("end_time", schedule.getEnd())
                .addValue("expected_bracelet", schedule.getExpectedBracelet())
                .addValue("expected_fouille", schedule.getExpectedFouille())
                .addValue("expected_litiges", schedule.getExpectedLitiges())
                .addValue("description", schedule.getDescription()));
    }
}
