package fr.frogdevelopment.ep.views.teams.team;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import fr.frogdevelopment.ep.views.MainView;

@ParentLayout(MainView.class)
@Route(value = "team")
public class TeamParentView extends Div implements RouterLayout {

}
