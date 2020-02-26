package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.SchedulesClient;

@Route(value = "team/schedule", layout = TeamParentView.class)
public class TeamScheduleView extends VerticalLayout implements HasUrlParameter<String>, HasDynamicTitle,
        AfterNavigationObserver {

    private final transient SchedulesClient schedulesClient;

    private final TeamNavigationBar teamNavigationBar = new TeamNavigationBar();

    private String teamCode;

    public TeamScheduleView(SchedulesClient schedulesClient) {
        this.schedulesClient = schedulesClient;

        add(teamNavigationBar);
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
    }

}
