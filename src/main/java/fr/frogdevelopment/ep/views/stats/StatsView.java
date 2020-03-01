package fr.frogdevelopment.ep.views.stats;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.StatsClient;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.components.StatisticsGrid;

@PageTitle("Statistiques Globales")
@Route(value = "stats", layout = MainView.class)
@CssImport("./styles/views/stats/stats-view.css")
public class StatsView extends Div implements AfterNavigationObserver {

    private final transient StatsClient statsClient;

    private final StatisticsGrid grid;

    public StatsView(StatsClient statsClient) {
        this.statsClient = statsClient;

        setId("stats-view");

        grid = new StatisticsGrid(true, statsClient.getTimeSlots());
        grid.setId("list");
        grid.setHeightFull();

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(statsClient.getAllWithSchedules());
    }
}
