package fr.frogdevelopment.ep.application.export;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateExportData {

    private final ExportRepository exportRepository;

    public CreateExportData(ExportRepository exportRepository) {
        this.exportRepository = exportRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void call() {
        exportRepository.generateExportData();
        exportRepository.incrementVersion();
    }
}
