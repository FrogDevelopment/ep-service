package fr.frogdevelopment.ep.views.upload;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.FinishedEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.xls.ReadXls;
import fr.frogdevelopment.ep.views.MainView;

@Route(value = "upload", layout = MainView.class)
@PageTitle("Upload")
//@Secured("ROLE_Admin")
public class UploadView extends VerticalLayout {

    private final transient ReadXls readXls;
    private final MemoryBuffer buffer;

    public UploadView(ReadXls readXls) {
        this.readXls = readXls;

        var label = new Label("Charge les données à partir de l'Xls");
        add(label);

        buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xls", ".xlsx", ".xlsm");
        upload.addFinishedListener(readXls());

        add(upload);
    }

    private ComponentEventListener<FinishedEvent> readXls() {
        return e -> this.readXls.call(buffer.getInputStream());
    }

}
