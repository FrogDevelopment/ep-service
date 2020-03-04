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
import fr.frogdevelopment.ep.implementation.timetables.TimetablesRepository;
import fr.frogdevelopment.ep.model.Timetable;
import fr.frogdevelopment.ep.views.MainView;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Timetable Générale")
@Route(value = "planning", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@CssImport("./styles/views/planning/planning-view.css")
public class PlanningView extends Div implements AfterNavigationObserver {

    private final transient TimetablesRepository timetablesRepository;

    private final Map<DayOfWeek, Grid<Timetable>> grids = new HashMap<>();

    public PlanningView(TimetablesRepository timetablesRepository) {
        this.timetablesRepository = timetablesRepository;

        setId("planning-view");
    }

    private void addGridFor(DayOfWeek dayOfWeek, List<Timetable> timetables) {
        var grid = new Grid<Timetable>();
        grid.setId("grid-" + dayOfWeek.name());
        grid.addThemeVariants(LUMO_NO_BORDER);
        grid.setHeight("33%");

        grid.addColumn(Timetable::getTitle)
                .setHeader("Horaire");
        grid.addColumn(Timetable::getDuration)
                .setHeader("Nb heures");
        grid.addColumn(Timetable::getExpectedBracelet)
                .setHeader("Effectif bracelet");
        grid.addColumn(Timetable::getExpectedFouille)
                .setHeader("Effectif fouille");
        grid.addColumn(Timetable::getExpectedLitiges)
                .setHeader("Effectif litiges");
        grid.addColumn(Timetable::getExpectedTotal)
                .setHeader("Effectif Total");
        grid.addColumn(Timetable::getDescription)
                .setHeader("Description");

        grid.setItems(timetables);

        add(grid);
        grids.put(dayOfWeek, grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        timetablesRepository.getPlanning().forEach(this::addGridFor);
    }

}
