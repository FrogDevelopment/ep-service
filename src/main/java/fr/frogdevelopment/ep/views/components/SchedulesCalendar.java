package fr.frogdevelopment.ep.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.frogdevelopment.ep.model.Location;
import fr.frogdevelopment.ep.model.Timetable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.vaadin.stefan.fullcalendar.Entry;

public class SchedulesCalendar extends VerticalLayout {

    private final ThreeDaysCalendar calendar = new ThreeDaysCalendar();

    public SchedulesCalendar() {
        super();

        addLegends();

        add(calendar);
    }

    private void addLegends() {
        var legends = new HorizontalLayout();

        Arrays.stream(Location.values())
                .map(this::addLegend)
                .forEach(legends::add);

        add(legends);
    }

    private Component addLegend(Location location) {
        var locationColor = new Div(new Text(location.name()));
        locationColor.getStyle().set("background", colorByLocation(location));

        return locationColor;
    }

    public void setSchedules(@NotNull List<Timetable> timetables) {
        calendar.removeAllEntries();
        if (CollectionUtils.isNotEmpty(timetables)) {
            calendar.gotoDate(timetables.get(0).getStart().toLocalDate());
            calendar.setEntries(toEntries(timetables));
        }
    }

    private List<Entry> toEntries(List<Timetable> timetables) {
        return timetables
                .stream()
                .map(this::toEntry)
                .collect(Collectors.toList());
    }

    private Entry toEntry(Timetable timetable) {
        var entryId = UUID.randomUUID().toString();
        var location = timetable.getLocation();
        var title = String.format("%s%n%s", location, timetable.getTeamCode());
        var start = timetable.getStart();
        var end = timetable.getEnd();
        var color = colorByLocation(location);
        var description = timetable.getTeamCode();

        return new Entry(entryId, title, start, end, false, false, color, description);
    }

    private static String colorByLocation(Location location) {
        switch (location) {
            case FOUILLES:
                return "#57e114";
            case BRACELET:
                return "#10e2eb";
            case LITIGES:
                return "#eb9e10";
            default:
                return "#eb1010";
        }
    }
}
