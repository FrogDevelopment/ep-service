package fr.frogdevelopment.ep.application.xls.parser;

import static org.assertj.core.api.Assertions.assertThat;

import fr.frogdevelopment.ep.utils.UnitTest;
import java.io.IOException;
import org.apache.poi.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ExcelParserTest extends UnitTest {

    @Test
    void test() throws IOException {
        // given
        var inputStream = getClass().getClassLoader().getResourceAsStream("PLANNING_EP_2019_V3.0.xls");
        var multipartFile = new MockMultipartFile("file",
                "PLANNING_EP_2019_V3.0.xls", "text/plain", IOUtils.toByteArray(inputStream));

        // when
        var result = new ExcelParser().read(multipartFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTeams()).hasSize(7);
//        Assertions.assertThat(result.getSchedules()).hasSize(17); // fixme 22:00 - ?
        assertThat(result.getTimetables()).hasSize(16);
        assertThat(result.getVolunteers()).hasSize(175);
    }

}
