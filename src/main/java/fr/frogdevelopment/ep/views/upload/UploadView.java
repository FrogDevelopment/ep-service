package fr.frogdevelopment.ep.views.upload;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.xls.ReadXls;
import fr.frogdevelopment.ep.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "upload", layout = MainView.class)
@PageTitle("Upload")
//@Secured("ROLE_Admin")
public class UploadView extends VerticalLayout {

    @Autowired
    private ReadXls readXls;

    public UploadView() {
        var label = new Label("Charge les données à partir de l'Xls");
        add(label);

//        var buffer = new FileBuffer();
        var buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xls", ".xlsx", ".xlsm");
        upload.addFinishedListener(e -> readXls.call(buffer.getInputStream()));

        add(upload);
    }

}
