package fr.frogdevelopment.ep.views.schedules;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.schedules.GetSchedules;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.teams.EpCalendar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PageTitle("Schedules")
@Route(value = "schedules", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class SchedulesView extends Div implements AfterNavigationObserver {

    private final transient GetSchedules getSchedules;
    private final EpCalendar calendar = new EpCalendar();

    public SchedulesView(GetSchedules getSchedules) {
        this.getSchedules = getSchedules;

        setId("schedules-view");

        add(calendar);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        calendar.setSchedules(getSchedules.call());
    }

}
