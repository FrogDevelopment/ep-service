package fr.frogdevelopment.ep.implementation.volunteers;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.upperCase;

import fr.frogdevelopment.ep.model.Volunteer;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AddVolunteer {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddVolunteer(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("volunteer_id")
                .withTableName("volunteers");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Volunteer volunteer) {
        var paramSource = new MapSqlParameterSource()
                .addValue("volunteer_ref", volunteer.getRef())
                .addValue("firstName", capitalize(lowerCase(volunteer.getFirstName())))
                .addValue("lastName", upperCase(volunteer.getLastName()))
                .addValue("phoneNumber", volunteer.getPhoneNumber())
                .addValue("email", lowerCase(volunteer.getEmail()))
                .addValue("team_code", volunteer.getTeamCode())
                .addValue("friends_group", volunteer.getFriendsGroup())
                .addValue("referent", volunteer.isReferent());

        var returnedKey = simpleJdbcInsert.executeAndReturnKey(paramSource);

        volunteer.setId(returnedKey.intValue());
    }

}
