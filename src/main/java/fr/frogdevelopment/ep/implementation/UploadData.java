package fr.frogdevelopment.ep.implementation;

import fr.frogdevelopment.ep.implementation.xls.ReadXls;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UploadData {

    private final CleanUpDatabase cleanUpDatabase;
    private final ReadXls readXls;

    public UploadData(CleanUpDatabase cleanUpDatabase, ReadXls readXls) {
        this.cleanUpDatabase = cleanUpDatabase;
        this.readXls = readXls;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void call(InputStream inputStream) {
        cleanUpDatabase.call();
        readXls.call(inputStream);
    }
}
