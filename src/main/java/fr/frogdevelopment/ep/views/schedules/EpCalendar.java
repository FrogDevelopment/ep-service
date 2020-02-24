package fr.frogdevelopment.ep.views.schedules;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import fr.frogdevelopment.ep.model.Schedule.Location;
import org.vaadin.stefan.fullcalendar.FullCalendar;

@Tag("ep-calendar")
@JsModule("./src/ep-calendar.js")
public class EpCalendar extends FullCalendar {

    EpCalendar() {
        super();

        setHeightAuto();
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
