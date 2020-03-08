package fr.frogdevelopment.ep.implementation.xls;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.inOrder;

import fr.frogdevelopment.ep.implementation.xls.add.AddData;
import fr.frogdevelopment.ep.implementation.xls.clean.CleanUpData;
import fr.frogdevelopment.ep.implementation.xls.parser.ExcelReader;
import java.io.InputStream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
class UploadDataTest {

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
