package fr.frogdevelopment.ep.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import fr.frogdevelopment.ep.implementation.UploadData;
import java.io.InputStream;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "xls")
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