package fr.frogdevelopment.ep.views.teams;

import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.LIST_WEEK;

import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.views.schedules.EpCalendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.vaadin.stefan.fullcalendar.Entry;

public class TeamSchedules extends EpCalendar {

    public TeamSchedules(List<Schedule> schedules) {
        changeView(LIST_WEEK);
        addEntries(schedules
                .stream()
                .map(this::toEntry)
                .collect(Collectors.toList()));
    }

    private Entry toEntry(Schedule schedule) {
        var entryId = UUID.randomUUID().toString();
        var location = schedule.getLocation();
        var title = String.format("%s", location);
        var start = schedule.getStart();
        var end = schedule.getEnd();

        return new Entry(entryId, title, start, end, false, true, null, null);
    }
}
