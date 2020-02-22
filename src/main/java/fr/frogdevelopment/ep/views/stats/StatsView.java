package fr.frogdevelopment.ep.views.stats;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.volunteers.GetVolunteers;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.MainView;

@PageTitle("Statistiques")
@Route(value = "stats", layout = MainView.class)
public class StatsView extends Div implements AfterNavigationObserver {

    private final transient GetVolunteers getVolunteers;

    private final Grid<Volunteer> grid = new Grid<>();

    public StatsView(GetVolunteers getVolunteers) {
        this.getVolunteers = getVolunteers;
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.addColumn(Volunteer::getLastName)
                .setHeader("Nom");

        grid.addColumn(Volunteer::getFirstName)
                .setHeader("Prénom");

        grid.addColumn(Volunteer::getTeamCode)
                .setHeader("Équipe");

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(getVolunteers.getAll());
    }
}
