package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.SchedulesClient;
import fr.frogdevelopment.ep.views.components.SchedulesCalendar;
import fr.frogdevelopment.ep.views.teams.TeamNavigationBar.Navigation;

@Route(value = "team/planning", layout = TeamParentView.class)
public class TeamPlanningView extends AbstractTeamView {

    private final transient SchedulesClient schedulesClient;

    private final SchedulesCalendar calendar = new SchedulesCalendar();

    public TeamPlanningView(SchedulesClient schedulesClient) {
        super(Navigation.PLANNING);

        this.schedulesClient = schedulesClient;

        add(calendar);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        calendar.setSchedules(schedulesClient.getGroupedSchedulesByTeam(teamCode));
    }

}
