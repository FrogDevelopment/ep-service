package fr.frogdevelopment.ep.views.schedules;

import static fr.frogdevelopment.ep.views.schedules.EpCalendar.colorByLocation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.schedules.GetSchedules;
import fr.frogdevelopment.ep.model.Schedule.Location;
import fr.frogdevelopment.ep.views.MainView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PageTitle("Planning")
@Route(value = "schedules", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class SchedulesView extends VerticalLayout implements AfterNavigationObserver {

    private final transient GetSchedules getSchedules;
    private final EpCalendar calendar = new EpCalendar();

    public SchedulesView(GetSchedules getSchedules) {
        this.getSchedules = getSchedules;

        setId("schedules-view");

        addLegends();

        add(calendar);
    }

    private void addLegends() {
        var legends = new HorizontalLayout();

        legends.add(addLegend(Location.BRACELET));
        legends.add(addLegend(Location.FOUILLES));
        legends.add(addLegend(Location.LITIGES));

        add(legends);
    }

    private Component addLegend(Location location) {
        var locationColor = new Div(new Text(location.name()));
        locationColor.getStyle().set("background", colorByLocation(location));

        return locationColor;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        calendar.setSchedules(getSchedules.call());
    }

}
