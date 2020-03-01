package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.StatsClient;
import fr.frogdevelopment.ep.views.components.StatisticsGrid;
import fr.frogdevelopment.ep.views.teams.TeamNavigationBar.Navigation;

@Route(value = "team/stats", layout = TeamParentView.class)
@CssImport("./styles/views/stats/stats-view.css")
public class TeamStatsView extends AbstractTeamView {

    private final transient StatsClient statsClient;

    private final StatisticsGrid grid;

    public TeamStatsView(StatsClient statsClient) {
        super(Navigation.STATS);
        this.statsClient = statsClient;

        setId("stats-view");

        grid = new StatisticsGrid(true, statsClient.getTimeSlots());
        grid.setId("list");
        grid.setHeightFull();

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(statsClient.getWithSchedules(teamCode));
    }
}
