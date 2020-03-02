package fr.frogdevelopment.ep.implementation.xls;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ActiveProfiles({"test"})
@Tag("integrationTest")
@Transactional(propagation = Propagation.REQUIRED)
class ReadXlsTest {

    @Value("classpath:PLANNING_EP_2019_V3.0.xls")
    private Resource resource;

    @Autowired
    private ReadXls readXls;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void test() throws IOException {
        // given
        File file = resource.getFile();
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("PLANNING_EP_2019_V3.0.xls", file.getName(), "text/plain",
                IOUtils.toByteArray(input));

        // when
        readXls.call(multipartFile.getInputStream());

        // then
        var countTeams = JdbcTestUtils.countRowsInTable(jdbcTemplate, "teams");
        assertThat(countTeams).isGreaterThan(0);
        var countVolunteer = JdbcTestUtils.countRowsInTable(jdbcTemplate, "volunteers");
        assertThat(countVolunteer).isGreaterThan(0);
        var countSchedules = JdbcTestUtils.countRowsInTable(jdbcTemplate, "schedules");
        assertThat(countSchedules).isGreaterThan(0);
    }

}
