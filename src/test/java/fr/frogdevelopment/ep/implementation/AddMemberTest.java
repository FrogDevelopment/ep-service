package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Tag("integrationTest")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class AddMemberTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AddMember addMember;

    @Test
    void shouldInsertNewMemberToTheTable() {
        // given
        var countRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate, "members");
        Assertions.assertThat(countRowsInTable).isEqualTo(0);

        var member = Member.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .build();

        // when
        addMember.call(member);

        // then
        countRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate, "members");
        Assertions.assertThat(countRowsInTable).isEqualTo(1);

    }
}
