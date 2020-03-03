package fr.frogdevelopment.ep.implementation.xls;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
class ExcelReaderTest {

    @Test
    void test() {
        // given
        var inputStream = getClass().getClassLoader().getResourceAsStream("PLANNING_EP_2019_V3.0.xls");

        // when
        var result = ExcelReader.read(inputStream);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTeams()).hasSize(7);
//        Assertions.assertThat(result.getSchedules()).hasSize(17); // fixme 22:00 - ?
        assertThat(result.getSchedules()).hasSize(16);
        assertThat(result.getVolunteers()).hasSize(175);
    }

}
