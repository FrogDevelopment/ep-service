package fr.frogdevelopment.ep.views.upload;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.XlsClient;
import fr.frogdevelopment.ep.views.MainView;

@Route(value = "upload", layout = MainView.class)
@PageTitle("Import Excel")
//@Secured("ROLE_Admin")
public class UploadView extends VerticalLayout {

    private final transient XlsClient xlsClient;
    private final MemoryBuffer buffer;

    public UploadView(XlsClient xlsClient) {
        this.xlsClient = xlsClient;

        var label = new Label("Charge les données à partir de l'Excel");
        add(label);

        buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xls", ".xlsx", ".xlsm");
        upload.addFinishedListener(e -> readXls());

        add(upload);
    }

    private void readXls() {
        this.xlsClient.readXls(buffer.getInputStream());
    }

}
