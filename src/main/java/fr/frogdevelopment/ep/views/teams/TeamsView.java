package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.schedules.SchedulesRepository;
import fr.frogdevelopment.ep.implementation.teams.GetTeams;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.MainView;
import java.util.Comparator;

@PageTitle("Équipes")
@Route(value = "teams", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class TeamsView extends Div implements AfterNavigationObserver {

    private final transient GetTeams getTeams;
    private final transient SchedulesRepository schedulesRepository;

    private final Accordion accordion = new Accordion();

    public TeamsView(GetTeams getTeams,
                     SchedulesRepository schedulesRepository) {
        this.getTeams = getTeams;
        this.schedulesRepository = schedulesRepository;

        setId("teams-view");

        add(accordion);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        getTeams.getAllWithMembers()
                .sorted(Comparator.comparing(Team::getName))
                .forEach(this::createAccordionContent);
    }

    private void createAccordionContent(Team team) {
        var wrapper = new HorizontalLayout();
        wrapper.setHeight("500px");

        var schedules = schedulesRepository.getGroupedSchedulesByTeam(team.getCode());
        var teamSchedules = new TeamSchedules(schedules);
        teamSchedules.setHeight("500px");
        wrapper.add(teamSchedules);

        var teamMembers = new TeamMembers(team.getVolunteers());
        teamMembers.setHeight("500px");
        wrapper.add(teamMembers);

        accordion.add(team.getFullName(), wrapper);
    }

}
