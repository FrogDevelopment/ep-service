package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import fr.frogdevelopment.ep.views.teams.TeamNavigationBar.Navigation;

public abstract class AbstractTeamView extends HorizontalLayout implements HasUrlParameter<String>, HasDynamicTitle,
        AfterNavigationObserver {

    protected String teamCode;

    private final TeamNavigationBar teamNavigationBar;

    public AbstractTeamView(Navigation current) {
        this.teamNavigationBar = new TeamNavigationBar(current);

        add(teamNavigationBar);
    }

    @Override
    public String getPageTitle() {
        return teamCode;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        teamCode = parameter;
        teamNavigationBar.setTeam(teamCode);
    }
}
