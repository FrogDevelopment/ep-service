package fr.frogdevelopment.ep.views.schedules;

import static fr.frogdevelopment.ep.views.schedules.EpCalendar.colorByLocation;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.GeneratedVaadinContextMenu.OpenedChangeEvent;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.schedules.SchedulesRepository;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import fr.frogdevelopment.ep.views.MainView;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;

@Slf4j
@PageTitle("Planning")
@Route(value = "schedules", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class SchedulesView extends VerticalLayout implements AfterNavigationObserver {

    private final transient SchedulesRepository schedulesRepository;

    private final transient Map<String, Schedule> schedulesByEntry = new HashMap<>();

    private final EpCalendar calendar = new EpCalendar();
    private final ContextMenu contextMenu = new ContextMenu(calendar);

    public SchedulesView(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;

        setId("schedules-view");

        addLegends();

        add(calendar);
        addContextMenuForLocation();
    }

    private void addContextMenuForLocation() {
        contextMenu.setOpenOnClick(true);
        contextMenu.addOpenedChangeListener(this::clearContextMenuOnClose);

        calendar.addEntryClickedListener(this::populateMenuContextOnEventClick);
    }

    private void clearContextMenuOnClose(OpenedChangeEvent<ContextMenu> event) {
        if (!event.isOpened()) {
            contextMenu.removeAll();
        }
    }

    private void populateMenuContextOnEventClick(EntryClickedEvent event) {
        var entryId = event.getEntry().getId();
        Arrays.stream(Location.values())
                .forEach(location -> {
                    MenuItem menuItem = contextMenu.addItem(location.name(), e -> onLocationChange(e, entryId));
                    menuItem.setCheckable(true);
                    menuItem.setChecked(schedulesByEntry.get(entryId).getLocation().equals(location));
                });
    }

    private void onLocationChange(ClickEvent<MenuItem> event, String entryId) {
        var newLocation = Location.valueOf(event.getSource().getText());
        var schedule = schedulesByEntry.get(entryId);
        schedule.setLocation(newLocation);
        schedulesRepository.changeLocation(schedule);
        Notification.show("Change location to " + newLocation);
        fetchEntries();
    }

    private void addLegends() {
        var legends = new HorizontalLayout();

        legends.add(addLegend(Location.BRACELET));
        legends.add(addLegend(Location.FOUILLES));
        legends.add(addLegend(Location.LITIGES));

        add(legends);
    }

    private Component addLegend(Location location) {
        var locationColor = new Div(new Text(location.name()));
        locationColor.getStyle().set("background", colorByLocation(location));

        return locationColor;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        fetchEntries();
    }

    private void fetchEntries() {
        calendar.removeAllEntries();
        List<Schedule> schedules = schedulesRepository.getGroupedSchedulesByTeam();
        if (!schedules.isEmpty()) {
            calendar.gotoDate(schedules.get(0).getStart().toLocalDate());
            calendar.addEntries(toEntries(schedules));
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

        schedulesByEntry.put(entryId, schedule);

        return new Entry(entryId, title, start, end, false, true, color, description);
    }

}
