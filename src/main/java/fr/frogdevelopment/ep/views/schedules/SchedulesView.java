package fr.frogdevelopment.ep.views.schedules;

import com.vaadin.flow.component.dependency.CssImport;
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
@PageTitle("Calendrier")
@Route(value = "calendar", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class SchedulesView extends VerticalLayout implements AfterNavigationObserver {

    private final transient SchedulesClient schedulesClient;

    private final SchedulesCalendar calendar = new SchedulesCalendar();

    public SchedulesView(SchedulesClient schedulesClient) {
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
