package fr.frogdevelopment.ep.views.teams;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.TeamsClient;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.MainView;

@PageTitle("Équipes")
@Route(value = "teams", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class TeamsView extends Div implements AfterNavigationObserver {

    private final transient TeamsClient teamsClient;

    private final Grid<Team> grid = new Grid<>();

    public TeamsView(TeamsClient teamsClient) {
        this.teamsClient = teamsClient;

        setId("teams-view");
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeightFull();
        grid.addColumn(Team::getFullName)
                .setHeader("Nom")
                .setSortable(true);
        grid.addColumn(t -> String.format("%s membres", t.getVolunteers().size()));
        grid.addItemClickListener(this::onItemClick);

        add(grid);
    }

    private void onItemClick(ItemClickEvent<Team> event) {
        getUI().ifPresent(ui -> ui.navigate("team/members/" + event.getItem().getCode()));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(teamsClient.getAll());
    }
}
