package fr.frogdevelopment.ep.application.xls;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.inOrder;

import fr.frogdevelopment.ep.application.xls.add.AddData;
import fr.frogdevelopment.ep.application.xls.clean.CleanUpData;
import fr.frogdevelopment.ep.application.xls.parser.ExcelReader;
import fr.frogdevelopment.ep.utils.UnitTest;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class UploadDataTest extends UnitTest {

    @InjectMocks
    private UploadData uploadData;

    @Mock
    private ExcelReader excelReader;
    @Mock
    private CleanUpData cleanUpData;
    @Mock
    private AddData addData;

    @Mock
    private InputStream inputStream;

    private final Result result = Result.builder().build();

    @Test
    void shouldImport_clean_then_add() {
        // given
        given(excelReader
                .read(inputStream))
                .willReturn(result);

        // when
        uploadData.call(inputStream);

        // then
        var inOrder = inOrder(cleanUpData, addData);
        inOrder.verify(cleanUpData).call();
        inOrder.verify(addData).call(result);
    }
}
