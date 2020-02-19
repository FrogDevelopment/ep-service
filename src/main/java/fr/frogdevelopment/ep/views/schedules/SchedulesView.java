package fr.frogdevelopment.ep.views.schedules;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.schedules.GetSchedules;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.views.MainView;
import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

@Route(value = "schedules", layout = MainView.class)
@PageTitle("Schedules")
@CssImport("./styles/views/teams/teams-view.css")
public class SchedulesView extends Div implements AfterNavigationObserver {

    private final transient GetSchedules getSchedules;
    private final FullCalendar calendar = FullCalendarBuilder.create().build();

    public SchedulesView(GetSchedules getSchedules) {
        this.getSchedules = getSchedules;

        setId("schedules-view");

        calendar.changeView(CalendarViewImpl.TIME_GRID_WEEK);
        calendar.setFirstDay(DayOfWeek.MONDAY);
        calendar.setNowIndicatorShown(false);
        calendar.setHeightAuto();
        calendar.setSnapDuration("00:15");
        calendar.setOption("weekends", "false");
        calendar.setOption("dayCount", "3");

        add(calendar);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        var schedules = getSchedules.call();
        if (!schedules.isEmpty()) {
            calendar.gotoDate(schedules.get(0).getFrom().toLocalDate());
            var entries = toEntries(schedules);
            calendar.addEntries(entries);
        }
    }

    private List<Entry> toEntries(List<Schedule> schedules) {
        return schedules
                .stream()
                .map(s -> {
                    var entry = new Entry();
                    entry.setTitle(s.getTeamCode());
                    entry.setDescription(s.getWhere());
                    entry.setStart(s.getFrom());
                    entry.setEnd(s.getTo());
                    entry.setColor(colorByLocation(s.getWhere()));
                    return entry;
                })
                .collect(Collectors.toList());
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
