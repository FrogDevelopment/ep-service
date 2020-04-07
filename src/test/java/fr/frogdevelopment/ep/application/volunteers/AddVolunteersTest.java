package fr.frogdevelopment.ep.application.volunteers;

import static org.assertj.core.api.Assertions.assertThat;

import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.utils.IntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

class AddVolunteersTest extends IntegrationTest {

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
