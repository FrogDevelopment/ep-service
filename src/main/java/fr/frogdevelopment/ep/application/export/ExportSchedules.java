package fr.frogdevelopment.ep.application.export;

import java.util.Collection;
import java.util.Collections;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExportSchedules {

    private final ExportRepository exportRepository;

    public ExportSchedules(ExportRepository exportRepository) {
        this.exportRepository = exportRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Collection<ExportData> call(int version, String volunteerRef) {
        var currentVersion = exportRepository.getCurrentVersion();
        if (currentVersion > version) {
            return exportRepository.fetchData(volunteerRef);
        } else {
            return Collections.emptyList();
        }
    }
}
