package fr.frogdevelopment.ep.views.planning;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import fr.frogdevelopment.ep.implementation.planning.PlanningRepository;
import fr.frogdevelopment.ep.model.Planning;
import fr.frogdevelopment.ep.views.MainView;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Planning Générale")
@Route(value = "planning", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@CssImport("./styles/views/planning/planning-view.css")
public class PlanningView extends Div implements AfterNavigationObserver {

    private final transient PlanningRepository planningRepository;

    private final Map<DayOfWeek, Grid<Planning>> grids = new HashMap<>();

    public PlanningView(PlanningRepository planningRepository) {
        this.planningRepository = planningRepository;

        setId("planning-view");
    }

    private void addGridFor(DayOfWeek dayOfWeek, List<Planning> plannings) {
        var grid = new Grid<Planning>();
        grid.setId("grid-" + dayOfWeek.name());
        grid.addThemeVariants(LUMO_NO_BORDER);
        grid.setHeight("33%");

        grid.addColumn(Planning::getTitle)
                .setHeader("Horaire");
        grid.addColumn(Planning::getDuration)
                .setHeader("Nb heures");
        grid.addColumn(Planning::getExpectedBracelet)
                .setHeader("Effectif bracelet");
        grid.addColumn(Planning::getExpectedFouille)
                .setHeader("Effectif fouille");
        grid.addColumn(Planning::getExpectedLitiges)
                .setHeader("Effectif litiges");
        grid.addColumn(Planning::getExpectedTotal)
                .setHeader("Effectif Total");
        grid.addColumn(Planning::getDescription)
                .setHeader("Description");

        grid.setItems(plannings);

        add(grid);
        grids.put(dayOfWeek, grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        planningRepository.getPlanning().forEach(this::addGridFor);
    }

}
