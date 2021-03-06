package fr.frogdevelopment.ep.implementation.xls;

import fr.frogdevelopment.ep.implementation.xls.add.AddData;
import fr.frogdevelopment.ep.implementation.xls.clean.CleanUpData;
import fr.frogdevelopment.ep.implementation.xls.parser.ExcelReader;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UploadData {

    private final ExcelReader excelReader;
    private final CleanUpData cleanUpData;
    private final AddData addData;

    public UploadData(ExcelReader excelReader, CleanUpData cleanUpData,
                      AddData addData) {
        this.excelReader = excelReader;
        this.cleanUpData = cleanUpData;
        this.addData = addData;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void call(InputStream inputStream) {
        var result = excelReader.read(inputStream);

        cleanUpData.call();

        addData.call(result);
    }
}
