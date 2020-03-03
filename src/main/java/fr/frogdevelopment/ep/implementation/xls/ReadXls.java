package fr.frogdevelopment.ep.implementation.xls;

import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReadXls {

    private final ExcelParameters parameters;

    public ReadXls(ExcelParameters parameters) {
        this.parameters = parameters;
    }

    public Result call(InputStream inputStream) {
        return new ExcelReader(parameters).execute(inputStream);
    }

}
