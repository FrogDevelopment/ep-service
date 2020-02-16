package fr.frogdevelopment.ep.views.upload;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

@Route("/upload")
@Secured("ROLE_Admin")
public class UploadView extends VerticalLayout {

    public UploadView() {
        Label label = new Label("Looks like you are admin!");
        add(label);
    }

}
