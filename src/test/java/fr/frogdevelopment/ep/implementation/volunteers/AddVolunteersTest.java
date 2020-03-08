package fr.frogdevelopment.ep.implementation.volunteers;

import static org.assertj.core.api.Assertions.assertThat;

import fr.frogdevelopment.ep.model.Volunteer;
import java.util.UUID;
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
class AddVolunteersTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AddVolunteer addVolunteer;

    @Test
    void shouldInsertNewVolunteerToTheTable() {
        // given
        var countRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate, "volunteers");
        assertThat(countRowsInTable).isEqualTo(0);

        var volunteer = Volunteer.builder()
                .ref(UUID.randomUUID().toString())
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("123456789")
                .email("john.doe@test.com")
                .build();

        // when
        addVolunteer.call(volunteer);

        // then
        assertThat(volunteer.getId()).isNotNull();
        countRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate, "volunteers");
        assertThat(countRowsInTable).isEqualTo(1);

    }
}
