package fr.frogdevelopment.ep.application.xls.parser;

import fr.frogdevelopment.ep.application.xls.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExcelReader {

    public Result read(MultipartFile file) {
        return new ExcelParser().read(file);
    }
}
