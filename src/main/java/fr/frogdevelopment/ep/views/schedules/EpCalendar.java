package fr.frogdevelopment.ep.views.schedules;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;

@Tag("ep-calendar")
@JsModule("./src/ep-calendar.js")
public class EpCalendar extends FullCalendar {

    EpCalendar() {
        super();

        setHeightAuto();
    }

    void setSchedules(List<Schedule> schedules) {
        if (!schedules.isEmpty()) {
            gotoDate(schedules.get(0).getFrom().toLocalDate());
            var entries = toEntries(schedules);
            addEntries(entries);
        }
    }

    private List<Entry> toEntries(List<Schedule> schedules) {
        return schedules
                .stream()
                .map(this::toEntry)
                .collect(Collectors.toList());
    }

    private Entry toEntry(Schedule s) {
        var entry = new Entry();
        entry.setTitle(s.getTeamCode());
        entry.setStart(s.getFrom());
        entry.setEnd(s.getTo());
        entry.setColor(colorByLocation(s.getWhere()));
        return entry;
    }

    static String colorByLocation(Location location) {
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
