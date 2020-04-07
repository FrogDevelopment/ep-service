package fr.frogdevelopment.ep.application.export;

import fr.frogdevelopment.ep.utils.UnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class CreateExportDataTest extends UnitTest {

    @InjectMocks
    private CreateExportData createExportData;

    @Mock
    private ExportRepository exportRepository;

    @Test
    void should_GenerateData_then_IncrementVersion() {
        // when
        createExportData.call();

        // then
        var inOrder = Mockito.inOrder(exportRepository, exportRepository);
        inOrder.verify(exportRepository).generateExportData();
        inOrder.verify(exportRepository).incrementVersion();
    }

}
