package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.menubar.MenuBar;

public class TeamNavigationBar extends MenuBar {

    public void setTeam(String teamCode) {
        addItem("<- Back", e -> onItemClick("teams"));
        addItem("Membres", e -> onItemClick("team/members/" + teamCode));
        addItem("Planning", e -> onItemClick("team/schedule/" + teamCode));
    }

    private void onItemClick(String location) {
        getUI().ifPresent(ui -> ui.navigate(location));
    }
}
