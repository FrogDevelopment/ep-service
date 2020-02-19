package fr.frogdevelopment.ep.views.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import fr.frogdevelopment.ep.model.Schedule;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;

@Tag("ep-calendar")
@JsModule("./src/ep-calendar.js")
public class EpCalendar extends FullCalendar {

    public EpCalendar() {
        super();

        setHeightAuto();
    }

    public void setSchedules(List<Schedule> schedules) {
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
        entry.setDescription(s.getWhere());
        entry.setStart(s.getFrom());
        entry.setEnd(s.getTo());
        entry.setColor(colorByLocation(s.getWhere()));
        return entry;
    }

    private static String colorByLocation(String location) {
        switch (location) {
            case "F":
                return "#57e114";
            case "B":
                return "#10e2eb";
            case "L":
                return "#eb9e10";
            default:
                return "#eb1010";
        }
    }
}
