package fr.frogdevelopment.ep.application.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import fr.frogdevelopment.ep.utils.UnitTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ExportSchedulesTest extends UnitTest {

    @InjectMocks
    private ExportSchedules exportSchedules;

    @Mock
    private ExportRepository exportRepository;

    @Test
    void should_not_fetch_data_when_current_version_lesser() {
        // given
        var version = 3;
        var volunteerRef = "volunteerRef";

        given(exportRepository
                .getCurrentVersion())
                .willReturn(2);

        // when
        var data = exportSchedules.call(version, volunteerRef);

        // then
        assertThat(data).isEmpty();
        then(exportRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void should_fetch_data_when_current_version_greater() {
        // given
        var version = 1;
        var volunteerRef = "volunteerRef";

        given(exportRepository
                .getCurrentVersion())
                .willReturn(2);

        given(exportRepository
                .fetchData(volunteerRef))
                .willReturn(List.of(ExportData.builder().build()));

        // when
        var data = exportSchedules.call(version, volunteerRef);

        // then
        assertThat(data).isNotEmpty();
    }

}
