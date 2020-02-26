package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.SchedulesClient;
import fr.frogdevelopment.ep.client.TeamsClient;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.MainView;
import java.util.Comparator;

@PageTitle("Équipes")
@Route(value = "teams", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class TeamsView extends Div implements AfterNavigationObserver {

    private final transient TeamsClient teamsClient;
    private final transient SchedulesClient schedulesClient;

    private final Accordion accordion = new Accordion();

    public TeamsView(TeamsClient teamsClient,
                     SchedulesClient schedulesClient) {
        this.teamsClient = teamsClient;
        this.schedulesClient = schedulesClient;

        setId("teams-view");

        add(accordion);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        teamsClient.getAllWithMembers()
                .sorted(Comparator.comparing(Team::getName))
                .forEach(this::createAccordionContent);
    }

    private void createAccordionContent(Team team) {
        var wrapper = new HorizontalLayout();
        wrapper.setHeight("500px");

        var schedules = schedulesClient.getGroupedSchedulesByTeam(team.getCode());
        var teamSchedules = new TeamSchedules(schedules);
        teamSchedules.setHeight("500px");
        wrapper.add(teamSchedules);

        var teamMembers = new TeamMembers(team.getVolunteers());
        teamMembers.setHeight("500px");
        wrapper.add(teamMembers);

        accordion.add(team.getFullName(), wrapper);
    }

}
