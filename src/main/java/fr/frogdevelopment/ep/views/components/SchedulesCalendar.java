package fr.frogdevelopment.ep.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
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

    public void setSchedules(@NotNull List<Schedule> schedules) {
        calendar.removeAllEntries();
        if (CollectionUtils.isNotEmpty(schedules)) {
            calendar.gotoDate(schedules.get(0).getStart().toLocalDate());
            calendar.setEntries(toEntries(schedules));
        }
    }

    private List<Entry> toEntries(List<Schedule> schedules) {
        return schedules
                .stream()
                .map(this::toEntry)
                .collect(Collectors.toList());
    }

    private Entry toEntry(Schedule schedule) {
        var entryId = UUID.randomUUID().toString();
        var location = schedule.getLocation();
        var title = String.format("%s%n%s", location, schedule.getTeamCode());
        var start = schedule.getStart();
        var end = schedule.getEnd();
        var color = colorByLocation(location);
        var description = schedule.getTeamCode();

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
