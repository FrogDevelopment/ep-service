package fr.frogdevelopment.ep.application.xls;

import fr.frogdevelopment.ep.application.xls.add.AddData;
import fr.frogdevelopment.ep.application.xls.clean.CleanUpData;
import fr.frogdevelopment.ep.application.xls.parser.ExcelReader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public void call(MultipartFile file) {
        var result = excelReader.read(file);

        cleanUpData.call();

        addData.call(result);
    }
}
