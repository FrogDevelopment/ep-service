package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.domain.Member;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddMember {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public AddMember(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .usingGeneratedKeyColumns("member_id")
                .withTableName("members");
    }

    public void call(Member member) {
        var paramSource = new MapSqlParameterSource()
                .addValue("firstName", member.getFirstName())
                .addValue("lastName", member.getLastName())
                .addValue("phoneNumber", member.getPhoneNumber())
                .addValue("email", member.getEmail())
                .addValue("teamId", member.getTeamId());

        log.info("Inserted {}", member);
        var returnedKey = simpleJdbcInsert.executeAndReturnKey(paramSource);

        member.setId(returnedKey.intValue());
    }

}
