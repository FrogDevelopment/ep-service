package fr.frogdevelopment.ep.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import fr.frogdevelopment.ep.implementation.UploadData;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

//@RestController
//@RequestMapping(path = "xls")
@Component
public class XlsController {

    private final UploadData uploadData;

    public XlsController(UploadData uploadData) {
        this.uploadData = uploadData;
    }

    @PostMapping(consumes = "application/vnd.ms-excel")
    @ResponseStatus(NO_CONTENT)
    public void readXls(InputStream inputStream) {
        uploadData.call(inputStream);
    }
}
