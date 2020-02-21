package fr.frogdevelopment.ep.views.about;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.views.MainView;

@PageTitle("À propos")
@Route(value = "about", layout = MainView.class)
public class AboutView extends VerticalLayout {

}
