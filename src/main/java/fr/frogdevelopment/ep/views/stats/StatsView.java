package fr.frogdevelopment.ep.views.stats;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static java.util.stream.Collectors.toMap;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.stats.StatsRepository;
import fr.frogdevelopment.ep.implementation.stats.StatsRepository.TimeSlot;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.MainView;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@PageTitle("Statistiques")
@Route(value = "stats", layout = MainView.class)
@CssImport("./styles/views/volunteers/volunteers-view.css")
public class StatsView extends Div implements AfterNavigationObserver {

    private final transient StatsRepository statsRepository;

    private final Grid<Volunteer> grid = new Grid<>();
    private final HashMap<String, Map<String, String>> mapLocationBySlot = new HashMap<>();

    public StatsView(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;

        setId("stats-view");

        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.addColumn(Volunteer::getLastName)
                .setHeader("Nom")
                .setSortable(true)
                .setFrozen(true);

        grid.addColumn(Volunteer::getFirstName)
                .setHeader("Prénom")
                .setSortable(true)
                .setFrozen(true);

        grid.addColumn(Volunteer::getTeamCode)
                .setHeader("Équipe")
                .setSortable(true)
                .setFrozen(true);

        var mapHeaderDays = new EnumMap<DayOfWeek, List<Column<Volunteer>>>(DayOfWeek.class);
        statsRepository.getTimeSlots()
                .forEach(ts -> {
                    var dayOfWeek = ts.getStart().getDayOfWeek();
                    mapHeaderDays
                            .computeIfAbsent(dayOfWeek, key -> new ArrayList<>())
                            .add(grid.addColumn(getLocationBySlot(ts))
                                    .setFlexGrow(1)
                                    .setHeader(getHeader(ts)));
                });

        var headerRow = grid.prependHeaderRow();
        mapHeaderDays.forEach((dayOfWeek, columns) -> headerRow
                .join(columns.toArray(new Column[0]))
                .setComponent(new Label(dayOfWeek.name()))
        );

        add(grid);
    }

    private static Component getHeader(TimeSlot ts) {
        return new Text(String.format("%02d:%02d - %02d:%02d",
                ts.getStart().getHour(),
                ts.getStart().getMinute(),
                ts.getEnd().getHour(),
                ts.getEnd().getMinute()));
    }

    private ValueProvider<Volunteer, Object> getLocationBySlot(TimeSlot ts) {
        return volunteer -> mapLocationBySlot
                .computeIfAbsent(volunteer.getRef(), computeLocationBySlot(volunteer))
                .getOrDefault(slotsToLabel(ts.getStart(), ts.getEnd()), "-");
    }

    private Function<String, Map<String, String>> computeLocationBySlot(Volunteer volunteer) {
        return key -> volunteer.getSchedules()
                .stream()
                .collect(toMap(schedule -> slotsToLabel(schedule.getStart(), schedule.getEnd()),
                        schedule -> schedule.getLocation().getCode(), (a, b) -> b, HashMap::new));
    }

    private static String slotsToLabel(LocalDateTime start, LocalDateTime end) {
        var dayOfWeek = start.getDayOfWeek();
        var startHour = start.getHour();
        var startMinute = start.getMinute();
        var endHour = end.getHour();
        var endMinute = end.getMinute();
        return String.format("%s%n %s:%s - %s:%s", dayOfWeek, startHour, startMinute, endHour, endMinute);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(statsRepository.getAllWithSchedules());
        grid.recalculateColumnWidths();
        grid.setHeight("800px"); // Fixme
    }
}
