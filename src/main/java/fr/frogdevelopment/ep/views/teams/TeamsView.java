package fr.frogdevelopment.ep.views.teams;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;
import static com.vaadin.flow.component.icon.VaadinIcon.EDIT;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS_CIRCLE;
import static com.vaadin.flow.component.icon.VaadinIcon.TRASH;
import static java.time.Duration.between;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.TeamsClient;
import fr.frogdevelopment.ep.implementation.stats.GetTeamStats;
import fr.frogdevelopment.ep.model.Location;
import fr.frogdevelopment.ep.model.Schedule;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.components.ConfirmDialog;
import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Stream;

@PageTitle("Équipes")
@Route(value = "teams", layout = MainView.class)
@CssImport("./styles/views/teams/teams-view.css")
public class TeamsView extends Div implements AfterNavigationObserver {

    private final transient TeamsClient teamsClient;
    private final transient GetTeamStats getTeamStats;

    private final Grid<Team> grid = new Grid<>();
    private ListDataProvider<Team> dataProvider;

    public TeamsView(TeamsClient teamsClient, GetTeamStats getTeamStats) {
        this.teamsClient = teamsClient;
        this.getTeamStats = getTeamStats;

        setId("teams-view");

        createButtonLayout();
    }

    private void createButtonLayout() {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        var buttonAdd = new Button("Ajouter une équipe", PLUS_CIRCLE.create());
        buttonAdd.addThemeVariants(LUMO_PRIMARY);
        buttonAdd.addClickListener(event -> onAdd());
        buttonLayout.add(buttonAdd);

        add(buttonLayout);
    }

    private void createGrid() {
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeight("95%");

        grid.addItemClickListener(this::onItemClick);

        grid.addColumn(Team::getCode)
                .setHeader("Code")
                .setSortable(true)
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addColumn(Team::getName)
                .setHeader("Nom")
                .setSortable(true)
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addColumn(Team::getCountMembers)
                .setHeader("Membres")
                .setSortable(true)
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addColumn(Team::getReferents)
                .setHeader("Référents")
                .setSortable(false)
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addColumn(getTotal())
                .setHeader("Nb heures")
                .setSortable(false)
                .setFlexGrow(0)
                .setAutoWidth(true);

        // fixme make it dynamic
        var locations = List.of(Location.BRACELET, Location.LITIGES, Location.FOUILLES);
        Stream.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                .forEach(dayOfWeek -> {
                    locations.forEach(location -> grid.addColumn(getCountByDayOfWeekAndLocation(dayOfWeek, location))
                            .setHeader(String.format("%s - %s", dayOfWeek.toString(), location.toString()))
                            .setSortable(false)
                            .setFlexGrow(0)
                            .setAutoWidth(true));

                    grid.addColumn(getTotalByDayOfWeek(dayOfWeek))
                            .setHeader("Total")
                            .setSortable(false)
                            .setFlexGrow(0)
                            .setAutoWidth(true);
                });

        add(grid);

        GridContextMenu<Team> contextMenu = new GridContextMenu<>(grid);
        var edit = new HorizontalLayout(EDIT.create(), new Label("Modifier"));
        contextMenu.addItem(edit, event -> event.getItem().ifPresentOrElse(this::onEdit, this::smallError));
        var delete = new HorizontalLayout(TRASH.create(), new Label("Supprimer"));
        contextMenu.addItem(delete, event -> event.getItem().ifPresentOrElse(this::onDelete, this::smallError));

        grid.setDataProvider(dataProvider);
    }

    private ValueProvider<Team, Double> getTotal() {
        return team -> team.getSchedules()
                .stream()
                .map(s -> Schedule.builder()
                        .dayOfWeek(s.getDayOfWeek())
                        .start(s.getStart())
                        .end(s.getEnd())
                        .build())
                .distinct()
                .map(this::getDurationAsHours)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private ValueProvider<Team, Double> getCountByDayOfWeekAndLocation(DayOfWeek dayOfWeek, Location location) {
        return team -> team.getSchedules()
                .stream()
                .filter(s -> dayOfWeek.equals(s.getDayOfWeek()))
                .filter(s -> location.equals(s.getLocation()))
                .map(this::getDurationAsHours)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private ValueProvider<Team, Double> getTotalByDayOfWeek(DayOfWeek dayOfWeek) {
        return team -> team.getSchedules()
                .stream()
                .filter(s -> dayOfWeek.equals(s.getDayOfWeek()))
                .map(s -> Schedule.builder()
                        .start(s.getStart())
                        .end(s.getEnd())
                        .build())
                .distinct()
                .map(this::getDurationAsHours)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double getDurationAsHours(Schedule s) {
        return (double) between(s.getStart(), s.getEnd()).toMinutes() / 60;
    }

    private void onAdd() {
        new TeamDialog(team -> {
            teamsClient.add(team);
            dataProvider.getItems().add(team);
            dataProvider.refreshAll();
        }).open();
    }

    private void onEdit(Team team) {
        new TeamDialog(team, toUpdate -> {
            teamsClient.update(toUpdate);
            Notification.show("Créneau mis à jour", 5000, Position.TOP_CENTER);
            dataProvider.refreshItem(toUpdate);
            dataProvider.refreshAll();
        }).open();
    }

    private void onDelete(Team team) {
        ConfirmDialog.builder()
                .message("Supprimer l'équipe ?")
                .confirmButton("Supprimer", () -> {
                    teamsClient.delete(team);
                    Notification.show("Equipe supprimé", 5000, Position.TOP_CENTER);
                    dataProvider.getItems().remove(team);
                    dataProvider.refreshAll();
                })
                .open();
    }

    private void smallError() {
        Notification.show("Petit problème ! ", 5000, Position.TOP_CENTER);
    }

    private void onItemClick(ItemClickEvent<Team> event) {
        getUI().ifPresent(ui -> ui.navigate("team/members/" + event.getItem().getCode()));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        dataProvider = DataProvider.ofCollection(getTeamStats.call());
        createGrid();
    }
}
