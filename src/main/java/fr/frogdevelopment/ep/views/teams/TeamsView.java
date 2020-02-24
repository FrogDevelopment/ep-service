package fr.frogdevelopment.ep.views.teams;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.implementation.schedules.SchedulesRepository;
import fr.frogdevelopment.ep.implementation.teams.GetTeams;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.MainView;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.vaadin.stefan.fullcalendar.Entry;

@PageTitle("Équipes")
@Route(value = "teams", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class TeamsView extends Div implements AfterNavigationObserver {

    private final transient GetTeams getTeams;
    private final transient SchedulesRepository schedulesRepository;

    private final Grid<Team> grid = new Grid<>();

    public TeamsView(GetTeams getTeams,
                     SchedulesRepository schedulesRepository) {
        this.getTeams = getTeams;
        this.schedulesRepository = schedulesRepository;

        setId("teams-view");
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeightFull();
        grid.addComponentColumn(this::getIndicator);
        grid.addColumn(t -> String.format("%s membres", t.getVolunteers().size()));
        grid.setItemDetailsRenderer(new ComponentRenderer<>(team -> {
            var schedules = schedulesRepository.getGroupedSchedulesByTeam(team.getCode());
            return new TeamMembers(team.getVolunteers(), toEntries(schedules));
        }));

        add(grid);
    }

    private HorizontalLayout getIndicator(Team t) {
        var wrapper = new HorizontalLayout();

        if (grid.isDetailsVisible(t)) {
            wrapper.add(VaadinIcon.ANGLE_DOWN.create());
        } else {
            wrapper.add(VaadinIcon.ANGLE_RIGHT.create());
        }
        wrapper.add(String.format("%s (%s)", t.getName(), t.getCode()));

        return wrapper;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(getTeams.getAllWithMembers().sorted(Comparator.comparing(Team::getName)));
    }

    private List<Entry> toEntries(List<Schedule> schedules) {
        return schedules
                .stream()
                .map(this::toEntry)
                .collect(Collectors.toList());
    }

    private Entry toEntry(Schedule schedule) {
        var entryId = UUID.randomUUID().toString();
        var location = schedule.getLocation();
        var title = String.format("%s", location);
        var start = schedule.getStart();
        var end = schedule.getEnd();

        return new Entry(entryId, title, start, end, false, true, null, null);
    }
}
