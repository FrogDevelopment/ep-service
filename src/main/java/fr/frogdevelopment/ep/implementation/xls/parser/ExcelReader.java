package fr.frogdevelopment.ep.implementation.xls.parser;

import fr.frogdevelopment.ep.implementation.xls.Result;
import java.io.InputStream;
import org.springframework.stereotype.Component;

@Component
public class ExcelReader {

    public Result read(InputStream inputStream) {
        return new ExcelParser().read(inputStream);
    }
}
