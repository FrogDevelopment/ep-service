package fr.frogdevelopment.ep.views.upload;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.FinishedEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.UploadData;
import fr.frogdevelopment.ep.views.MainView;

@Route(value = "upload", layout = MainView.class)
@PageTitle("Upload")
//@Secured("ROLE_Admin")
public class UploadView extends VerticalLayout {

    private final transient UploadData uploadData;
    private final MemoryBuffer buffer;

    public UploadView(UploadData uploadData) {
        this.uploadData = uploadData;

        var label = new Label("Charge les données à partir de l'Excel");
        add(label);

        buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xls", ".xlsx", ".xlsm");
        upload.addFinishedListener(readXls());

        add(upload);
    }

    private ComponentEventListener<FinishedEvent> readXls() {
        return e -> this.uploadData.call(buffer.getInputStream());
    }

}
