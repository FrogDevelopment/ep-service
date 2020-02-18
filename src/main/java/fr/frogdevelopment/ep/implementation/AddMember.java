package fr.frogdevelopment.ep.implementation;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.upperCase;

import fr.frogdevelopment.ep.model.Member;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddMember {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddMember(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("member_id")
                .withTableName("members");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Member member) {
        var paramSource = new MapSqlParameterSource()
                .addValue("firstName", capitalize(lowerCase(member.getFirstName())))
                .addValue("lastName", upperCase(member.getLastName()))
                .addValue("phoneNumber", member.getPhoneNumber())
                .addValue("email", lowerCase(member.getEmail()))
                .addValue("team_code", member.getTeamCode())
                .addValue("referent", member.isReferent());

        var returnedKey = simpleJdbcInsert.executeAndReturnKey(paramSource);

        member.setId(returnedKey.intValue());
    }

}
