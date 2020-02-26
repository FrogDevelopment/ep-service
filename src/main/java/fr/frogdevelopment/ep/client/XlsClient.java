package fr.frogdevelopment.ep.client;

import fr.frogdevelopment.ep.api.XlsController;
import java.io.InputStream;
import org.springframework.stereotype.Component;

// fixme tmp component to prepare split back/front => to migrate to Feign
@Component
public class XlsClient {

    private final XlsController xlsController;

    public XlsClient(XlsController xlsController) {
        this.xlsController = xlsController;
    }

    public void readXls(InputStream inputStream) {
        xlsController.readXls(inputStream);
    }
}
