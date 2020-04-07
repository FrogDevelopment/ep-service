package fr.frogdevelopment.ep.application.export;

import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ExportRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ExportRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void generateExportData() {
        var sql = "WITH datetime_schedules AS (SELECT t.timetable_ref,\n"
                + "                                   e.day_date + t.start_time AS start_schedule,\n"
                + "                                   CASE\n"
                + "                                       WHEN t.start_time > t.end_time THEN e.day_date + t.end_time + INTERVAL '1 day'\n"
                + "                                       WHEN t.end_time = TIME '00:00' THEN e.day_date + TIME '24:00'\n"
                + "                                       ELSE e.day_date + t.end_time\n"
                + "                                       END                   AS end_schedule\n"
                + "                            FROM timetables t\n"
                + "                                     INNER JOIN edition e ON t.day_of_week = e.day_of_week)\n"
                + "INSERT\n"
                + "INTO export_schedules\n"
                + "SELECT DISTINCT ds.start_schedule,\n"
                + "                ds.end_schedule,\n"
                + "                s.location,\n"
                + "                s.volunteer_ref\n"
                + "FROM schedules s\n"
                + "         INNER JOIN datetime_schedules ds ON s.timetable_ref = ds.timetable_ref;";

        namedParameterJdbcTemplate.getJdbcTemplate().update(sql);
    }

    public void incrementVersion() {
        var sql = "UPDATE export_version SET version = version + 1, date_export = now()";

        namedParameterJdbcTemplate.getJdbcTemplate().update(sql);
    }

    public int getCurrentVersion() {
        var sql = "SELECT version FROM export_version;";

        return Objects.requireNonNull(namedParameterJdbcTemplate.getJdbcTemplate().queryForObject(sql, Integer.class));
    }

    public List<ExportData> fetchData(String volunteerRef) {
        var sql = "SELECT start_schedule,\n"
                + "       end_schedule,\n"
                + "       location\n"
                + "FROM export_schedules\n"
                + "WHERE volunteer_ref = :volunteerRef";
        var parameterSource = new MapSqlParameterSource("volunteerRef", volunteerRef);

        return namedParameterJdbcTemplate.query(sql, parameterSource, (rs, rowNum) -> ExportData.builder()
                .startSchedule(rs.getTimestamp("start_schedule").toLocalDateTime())
                .endSchedule(rs.getTimestamp("end_schedule").toLocalDateTime())
                .location(rs.getString("location"))
                .build());
    }
}
