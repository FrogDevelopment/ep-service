package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.SchedulesClient;
import fr.frogdevelopment.ep.views.components.SchedulesCalendar;
import fr.frogdevelopment.ep.views.teams.TeamNavigationBar.Navigation;

@Route(value = "team/planning", layout = TeamParentView.class)
public class TeamPlanningView extends HorizontalLayout implements HasUrlParameter<String>, HasDynamicTitle,
        AfterNavigationObserver {

    private final transient SchedulesClient schedulesClient;

    private final TeamNavigationBar teamNavigationBar = new TeamNavigationBar(Navigation.PLANNING);
    private final SchedulesCalendar calendar = new SchedulesCalendar();

    private String teamCode;

    public TeamPlanningView(SchedulesClient schedulesClient) {
        this.schedulesClient = schedulesClient;

        add(teamNavigationBar);
        add(calendar);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        teamCode = parameter;
        teamNavigationBar.setTeam(teamCode);
    }

    @Override
    public String getPageTitle() {
        return teamCode;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        calendar.setSchedules(schedulesClient.getGroupedSchedulesByTeam(teamCode));
    }

}
