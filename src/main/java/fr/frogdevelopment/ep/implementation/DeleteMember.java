package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.model.Member;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteMember {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DeleteMember(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void call(Member member) {
        var sql = "DELETE FROM members WHERE member_id = :id";

        MapSqlParameterSource paramSource = new MapSqlParameterSource("id", member.getId());

        namedParameterJdbcTemplate.update(sql, paramSource);
    }

}
