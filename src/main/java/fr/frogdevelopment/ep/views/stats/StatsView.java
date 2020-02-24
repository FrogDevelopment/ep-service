package fr.frogdevelopment.ep.views.stats;

import static com.vaadin.flow.component.grid.ColumnTextAlign.CENTER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES;
import static fr.frogdevelopment.ep.model.Schedule.Location.AUTRES;
import static fr.frogdevelopment.ep.model.Schedule.Location.values;
import static java.time.Duration.between;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.stats.StatsRepository;
import fr.frogdevelopment.ep.implementation.stats.StatsRepository.TimeSlot;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.MainView;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@PageTitle("Statistiques")
@Route(value = "stats", layout = MainView.class)
@CssImport("./styles/views/stats/stats-view.css")
public class StatsView extends Div implements AfterNavigationObserver {

    private final transient StatsRepository statsRepository;

    private final Grid<Volunteer> grid = new Grid<>();
    private final Map<String, Map<String, String>> mapLocationBySlot = new HashMap<>();
    private final Map<String, Map<Location, Integer>> mapSumLocationsByVolunteers = new HashMap<>();
    private final Map<String, Map<Location, Double>> mapDurationsByVolunteers = new HashMap<>();

    public StatsView(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;

        setId("stats-view");

        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_ROW_STRIPES);
        grid.setHeightFull();

        grid.addColumn(Volunteer::getTeamCode)
                .setHeader("Équipe")
                .setSortable(true)
                .setFrozen(true);

        grid.addColumn(Volunteer::getLastName)
                .setHeader("Nom")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setSortable(true)
                .setFrozen(true);

        grid.addColumn(Volunteer::getFirstName)
                .setHeader("Prénom")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setSortable(true)
                .setFrozen(true);

        var headerRow = grid.prependHeaderRow();
        statsRepository.getTimeSlots()
                .stream()
                .collect(groupingBy(ts -> ts.getStart().getDayOfWeek(), () -> new EnumMap<>(DayOfWeek.class),
                        mapping(ts -> grid.addColumn(getLocationBySlot(ts))
                                .setTextAlign(CENTER)
                                .setHeader(getHeader(ts))
                                .setFlexGrow(0)
                                .setAutoWidth(true), toList())))
                .forEach((dayOfWeek, columns) -> headerRow
                        .join(columns.toArray(new Column[0]))
                        .setComponent(toHeaderTitle(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRANCE)))
                );

        var columnsCount = Arrays.stream(values())
                .filter(l -> !l.equals(AUTRES))
                .map(location -> grid.addColumn(getCountForLocation(location))
                        .setTextAlign(CENTER)
                        .setHeader(location.name())
                        .setFlexGrow(0)
                        .setAutoWidth(true))
                .collect(Collectors.toCollection(ArrayList::new));

        columnsCount.add(grid.addComponentColumn(volunteer -> toTotal(getTotalCountLocation(volunteer)))
                .setTextAlign(CENTER)
                .setHeader(toTotal("TOTAL"))
                .setFlexGrow(0)
                .setAutoWidth(true));

        headerRow.join(columnsCount.toArray(new Column[0]))
                .setComponent(toHeaderTitle("Décompte"));

        var columnsDuration = Arrays.stream(values())
                .filter(l -> !l.equals(AUTRES))
                .map(location -> grid.addColumn(getDurationForLocation(location))
                        .setTextAlign(CENTER)
                        .setHeader(location.name())
                        .setFlexGrow(0)
                        .setAutoWidth(true))
                .collect(Collectors.toCollection(ArrayList::new));

        columnsDuration.add(grid.addComponentColumn(volunteer -> toTotal(getTotalDurationLocation(volunteer)))
                .setTextAlign(CENTER)
                .setHeader(toTotal("TOTAL"))
                .setFlexGrow(0)
                .setAutoWidth(true));

        headerRow.join(columnsDuration.toArray(new Column[0]))
                .setComponent(toHeaderTitle("Nb heures"));

        add(grid);
    }

    private static Component toHeaderTitle(String text) {
        var title = new H3(text);
        title.getStyle().set("text-align", "center");

        return title;
    }

    private static Component toTotal(String text) {
        var title = new Label(text);
        title.getStyle().set("text-align", "center");
        title.getStyle().set("font-weight", "bold");

        return title;
    }

    private static Component getHeader(TimeSlot ts) {
        return new Text(String.format("%02d:%02d - %02d:%02d",
                ts.getStart().getHour(),
                ts.getStart().getMinute(),
                ts.getEnd().getHour(),
                ts.getEnd().getMinute()));
    }

    private ValueProvider<Volunteer, String> getLocationBySlot(TimeSlot ts) {
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

    private ValueProvider<Volunteer, Integer> getCountForLocation(Location location) {
        return volunteer -> mapSumLocationsByVolunteers.computeIfAbsent(volunteer.getRef(),
                key -> volunteer.getSchedules()
                        .stream()
                        .collect(toMap(Schedule::getLocation, schedule -> 1, Integer::sum, HashMap::new)))
                .getOrDefault(location, 0);
    }

    private String getTotalCountLocation(Volunteer volunteer) {
        return String.valueOf(mapSumLocationsByVolunteers
                .getOrDefault(volunteer.getRef(), emptyMap())
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum());
    }

    private ValueProvider<Volunteer, Double> getDurationForLocation(Location location) {
        return volunteer -> mapDurationsByVolunteers.computeIfAbsent(volunteer.getRef(),
                key -> volunteer.getSchedules()
                        .stream()
                        .collect(toMap(Schedule::getLocation,
                                schedule -> (double) between(schedule.getStart(), schedule.getEnd()).toMinutes() / 60,
                                Double::sum, HashMap::new)))
                .getOrDefault(location, 0D);
    }

    private String getTotalDurationLocation(Volunteer volunteer) {
        return String.format("%05.2f", mapDurationsByVolunteers
                .getOrDefault(volunteer.getRef(), emptyMap())
                .values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(statsRepository.getAllWithSchedules());
    }
}
