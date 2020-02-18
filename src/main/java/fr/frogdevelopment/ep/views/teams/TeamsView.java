package fr.frogdevelopment.ep.views.teams;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.GetTeams;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.MainView;

@Route(value = "teams", layout = MainView.class)
@PageTitle("Teams")
@CssImport("./styles/views/teams/teams-view.css")
public class TeamsView extends Div implements AfterNavigationObserver {

    private final GetTeams getTeams;
    private final Grid<Team> grid;

    public TeamsView(GetTeams getTeams) {
        this.getTeams = getTeams;

        setId("teams-view");
        grid = new Grid<>();
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeightFull();
        grid.addColumn(Team::getCode)
                .setHeader("Code")
                .setSortable(true);
        grid.addColumn(Team::getName)
                .setHeader("Nom")
                .setSortable(true);

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(getTeams.call());
    }
}
