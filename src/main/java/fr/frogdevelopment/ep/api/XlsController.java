package fr.frogdevelopment.ep.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import fr.frogdevelopment.ep.application.xls.UploadData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "xls")
public class XlsController {

    private final UploadData uploadData;

    public XlsController(UploadData uploadData) {
        this.uploadData = uploadData;
    }

    @PostMapping
    @ResponseStatus(NO_CONTENT)
    public void readXls(@RequestParam("file") MultipartFile file) {
        uploadData.call(file);
    }
}
