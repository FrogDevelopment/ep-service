package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.StatsClient;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.components.StatisticsGrid;
import fr.frogdevelopment.ep.views.teams.TeamNavigationBar.Navigation;

@Route(value = "team/stats", layout = MainView.class)
@CssImport("./styles/views/stats/stats-view.css")
public class TeamStatsView extends HorizontalLayout implements HasUrlParameter<String>, HasDynamicTitle,AfterNavigationObserver {

    private final transient StatsClient statsClient;

    private final TeamNavigationBar teamNavigationBar = new TeamNavigationBar(Navigation.MEMBERS);
    private final StatisticsGrid grid;

    private String teamCode;

    public TeamStatsView(StatsClient statsClient) {
        this.statsClient = statsClient;

        add(teamNavigationBar);

        setId("stats-view");

        grid = new StatisticsGrid(true, statsClient.getTimeSlots());
        grid.setId("list");
        grid.setHeightFull();

        add(grid);
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
        grid.setItems(statsClient.getWithSchedules(teamCode));
    }
}
