package fr.frogdevelopment.ep.views.teams;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.teams.GetTeams;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.MainView;
import java.util.Comparator;

@PageTitle("Équipes")
@Route(value = "teams", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class TeamsView extends Div implements AfterNavigationObserver {

    private final transient GetTeams getTeams;

    private final Grid<Team> grid = new Grid<>();

    public TeamsView(GetTeams getTeams) {
        this.getTeams = getTeams;

        setId("teams-view");
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeightFull();
        grid.addComponentColumn(t -> {
            var wrapper = new HorizontalLayout();

            if (grid.isDetailsVisible(t)) {
                wrapper.add(VaadinIcon.ANGLE_DOWN.create());
            } else {
                wrapper.add(VaadinIcon.ANGLE_RIGHT.create());
            }
            wrapper.add(String.format("%s (%s)", t.getName(), t.getCode()));

            return wrapper;
        })
                .setHeader("Nom")
                .setSortable(true);
        grid.addColumn(t -> String.format("%s membres", t.getVolunteers().size()));
        grid.setItemDetailsRenderer(new ComponentRenderer<>(team -> new TeamMembers(team.getVolunteers())));

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(getTeams.getAllWithMembers().sorted(Comparator.comparing(Team::getName)));
    }
}
