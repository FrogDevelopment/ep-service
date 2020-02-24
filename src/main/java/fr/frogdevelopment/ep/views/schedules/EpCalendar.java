package fr.frogdevelopment.ep.views.schedules;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.GeneratedVaadinContextMenu.OpenedChangeEvent;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.notification.Notification;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;
import org.vaadin.stefan.fullcalendar.FullCalendar;

@Tag("ep-calendar")
@JsModule("./src/ep-calendar.js")
public class EpCalendar extends FullCalendar {

    private final ContextMenu contextMenu = new ContextMenu(this);

    EpCalendar() {
        super();

        setHeightAuto();

        addContextMenuForLocation();
    }

    private void addContextMenuForLocation() {
        contextMenu.setOpenOnClick(true);
        contextMenu.addOpenedChangeListener(this::clearContextMenuOnClose);

        addEntryClickedListener(this::populateMenuContextOnEventClick);
    }

    private void clearContextMenuOnClose(OpenedChangeEvent<ContextMenu> event) {
        if (!event.isOpened()) {
            contextMenu.removeAll();
        }
    }

    private void populateMenuContextOnEventClick(EntryClickedEvent event) {
        var entry = event.getEntry();
        Arrays.stream(Location.values())
                .forEach(location -> {
                    MenuItem menuItem = contextMenu.addItem(location.name(), e -> onLocationChange(e, entry));
                    menuItem.setCheckable(true);
                    menuItem.setChecked(locationsMap.get(entry.getId()).equals(location));
                });
    }

    private void onLocationChange(ClickEvent<MenuItem> event, Entry entry) {
        var newLocation = Location.valueOf(event.getSource().getText());
        entry.setTitle(String.format("%s%n%s", newLocation, entry.getDescription()));
        entry.setColor(colorByLocation(newLocation));
        locationsMap.put(entry.getId(), newLocation);
        // todo save for all schedules
        Notification.show("Change location to " + newLocation);
        render();
    }

    void setSchedules(List<Schedule> schedules) {
        if (!schedules.isEmpty()) {
            gotoDate(schedules.get(0).getStart().toLocalDate());
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

    private final transient Map<String, Entry> entriesMap = new HashMap<>();
    private final transient Map<String, Location> locationsMap = new HashMap<>();

    private Entry toEntry(Schedule schedule) {
        var entryId = UUID.randomUUID().toString();
        var location = schedule.getLocation();
        var title = String.format("%s%n%s", location, schedule.getTeamCode());
        var start = schedule.getStart();
        var end = schedule.getEnd();
        var color = colorByLocation(location);
        var description = schedule.getTeamCode();

        var entry = new Entry(entryId, title, start, end, false, true, color, description);
        entriesMap.put(entryId, entry);
        locationsMap.put(entryId, location);
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
