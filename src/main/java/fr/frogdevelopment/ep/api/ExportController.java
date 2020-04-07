package fr.frogdevelopment.ep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import fr.frogdevelopment.ep.application.export.CreateExportData;
import fr.frogdevelopment.ep.application.export.ExportData;
import fr.frogdevelopment.ep.application.export.ExportSchedules;
import java.util.Collection;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "export", produces = APPLICATION_JSON_VALUE)
public class ExportController {

    private final CreateExportData createExportData;
    private final ExportSchedules exportSchedules;

    public ExportController(CreateExportData createExportData,
                            ExportSchedules exportSchedules) {
        this.createExportData = createExportData;
        this.exportSchedules = exportSchedules;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createExportData() {
        createExportData.call();
    }

    @GetMapping("/{volunteer_ref}")
    public Collection<ExportData> fetchData(@PathVariable("volunteer_ref") String volunteerRef,
                                            @RequestParam("version") int version) {
        return exportSchedules.call(version, volunteerRef);
    }
}
