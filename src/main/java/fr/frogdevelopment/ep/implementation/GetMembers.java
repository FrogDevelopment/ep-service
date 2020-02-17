package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.domain.Member;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetMembers {

    private final JdbcTemplate jdbcTemplate;

    public GetMembers(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Member> call() {
        var sql = "SELECT * FROM members";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Member.builder()
                .id(rs.getInt("member_id"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .email(rs.getString("email"))
                .phoneNumber(rs.getString("phone_number"))
                .teamId(rs.getInt("team_id"))
                .referent(rs.getBoolean("referent"))
                .build());
    }
}
