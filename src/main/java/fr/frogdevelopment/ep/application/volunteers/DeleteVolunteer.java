package fr.frogdevelopment.ep.application.volunteers;

import fr.frogdevelopment.ep.model.Volunteer;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DeleteVolunteer {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DeleteVolunteer(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Volunteer volunteer) {
        var paramSource = new MapSqlParameterSource("ref", volunteer.getRef());
        namedParameterJdbcTemplate.update("DELETE FROM schedules WHERE volunteer_ref = :ref", paramSource);
        namedParameterJdbcTemplate.update("DELETE FROM volunteers WHERE volunteer_ref = :ref", paramSource);
    }

}
