package fr.frogdevelopment.ep.views.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;

@Tag("three-days-calendar")
@JsModule("./src/three-days-calendar.js")
public class ThreeDaysCalendar extends FullCalendar {

    ThreeDaysCalendar() {
        super();

        setHeightAuto();
    }

    void setEntries(@NotNull List<Entry> entries) {
        removeAllEntries();
        if (CollectionUtils.isNotEmpty(entries)) {
            gotoDate(entries.get(0).getStart().toLocalDate());
            super.addEntries(entries);
        }
    }
}
