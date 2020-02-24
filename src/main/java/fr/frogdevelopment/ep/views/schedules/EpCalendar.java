package fr.frogdevelopment.ep.views.schedules;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;

@Tag("ep-calendar")
@JsModule("./src/ep-calendar.js")
public class EpCalendar extends FullCalendar {

    public EpCalendar() {
        super();

        setHeightAuto();
    }

    public void addEntries(@NotNull List<Entry> entries) {
        removeAllEntries();
        if (CollectionUtils.isNotEmpty(entries)) {
            gotoDate(entries.get(0).getStart().toLocalDate());
            super.addEntries(entries);
        }
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
