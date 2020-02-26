package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import fr.frogdevelopment.ep.views.MainView;

@ParentLayout(MainView.class)
@Route(value = "team")
public class ParentTeamView extends VerticalLayout implements RouterLayout {

    public ParentTeamView() {
        var menuBar = new MenuBar();
        menuBar.addItem("Membres", event -> onItemClick("team/members/MST"));
        menuBar.addItem("Planning", event -> onItemClick("team/planning/MST"));

        add(menuBar);
    }

    private void onItemClick(String location) {
        getUI().ifPresent(ui -> ui.navigate(location));
    }
}
