package fr.frogdevelopment.ep.views.calendar;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.SchedulesClient;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.components.SchedulesCalendar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PageTitle("Calendrier Global")
@Route(value = "calendar", layout = MainView.class)
public class CalendarView extends VerticalLayout implements AfterNavigationObserver {

    private final transient SchedulesClient schedulesClient;

    private final SchedulesCalendar calendar = new SchedulesCalendar();

    public CalendarView(SchedulesClient schedulesClient) {
        this.schedulesClient = schedulesClient;

        setId("schedules-view");

        add(calendar);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        fetchEntries();
    }

    private void fetchEntries() {
        calendar.setSchedules(schedulesClient.getGroupedSchedulesByTeam());
    }

}
