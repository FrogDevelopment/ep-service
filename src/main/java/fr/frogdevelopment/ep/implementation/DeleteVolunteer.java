package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.model.Volunteer;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteVolunteer {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DeleteVolunteer(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Volunteer volunteer) {
        var sql = "DELETE FROM volunteers WHERE volunteer_id = :id";

        MapSqlParameterSource paramSource = new MapSqlParameterSource("id", volunteer.getId());

        namedParameterJdbcTemplate.update(sql, paramSource);
    }

}
