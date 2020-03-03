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
import fr.frogdevelopment.ep.implementation.schedules.SchedulesRepository;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.views.MainView;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Schedule Générale")
@Route(value = "planning", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@CssImport("./styles/views/planning/planning-view.css")
public class PlanningView extends Div implements AfterNavigationObserver {

    private final transient SchedulesRepository schedulesRepository;

    private final Map<DayOfWeek, Grid<Schedule>> grids = new HashMap<>();

    public PlanningView(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;

        setId("planning-view");
    }

    private void addGridFor(DayOfWeek dayOfWeek, List<Schedule> schedules) {
        var grid = new Grid<Schedule>();
        grid.setId("grid-" + dayOfWeek.name());
        grid.addThemeVariants(LUMO_NO_BORDER);
        grid.setHeight("33%");

        grid.addColumn(Schedule::getTitle)
                .setHeader("Horaire");
        grid.addColumn(Schedule::getDuration)
                .setHeader("Nb heures");
        grid.addColumn(Schedule::getExpectedBracelet)
                .setHeader("Effectif bracelet");
        grid.addColumn(Schedule::getExpectedFouille)
                .setHeader("Effectif fouille");
        grid.addColumn(Schedule::getExpectedLitiges)
                .setHeader("Effectif litiges");
        grid.addColumn(Schedule::getExpectedTotal)
                .setHeader("Effectif Total");
        grid.addColumn(Schedule::getDescription)
                .setHeader("Description");

        grid.setItems(schedules);

        add(grid);
        grids.put(dayOfWeek, grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        schedulesRepository.getPlanning().forEach(this::addGridFor);
    }

}
