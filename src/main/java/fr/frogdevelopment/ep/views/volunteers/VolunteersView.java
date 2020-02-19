package fr.frogdevelopment.ep.views.volunteers;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;
import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import fr.frogdevelopment.ep.implementation.teams.GetTeams;
import fr.frogdevelopment.ep.implementation.volunteers.AddVolunteer;
import fr.frogdevelopment.ep.implementation.volunteers.DeleteVolunteer;
import fr.frogdevelopment.ep.implementation.volunteers.GetVolunteers;
import fr.frogdevelopment.ep.implementation.volunteers.UpdateVolunteer;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.components.ConfirmDialog;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Route(value = "volunteers", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Bénévoles")
@CssImport("./styles/views/volunteers/volunteers-view.css")
public class VolunteersView extends Div implements AfterNavigationObserver {

    private final transient AddVolunteer addVolunteer;
    private final transient GetVolunteers getVolunteers;
    private final transient GetTeams getTeams;
    private final transient UpdateVolunteer updateVolunteer;
    private final transient DeleteVolunteer deleteVolunteer;

    private final Grid<Volunteer> grid;
    private List<Volunteer> unfilteredData;
    private final ComboBox<Team> teamFilter;
    private List<Team> teams;

    public VolunteersView(AddVolunteer addVolunteer,
                          GetVolunteers getVolunteers,
                          GetTeams getTeams,
                          UpdateVolunteer updateVolunteer,
                          DeleteVolunteer deleteVolunteer) {
        this.addVolunteer = addVolunteer;
        this.getVolunteers = getVolunteers;
        this.getTeams = getTeams;
        this.updateVolunteer = updateVolunteer;
        this.deleteVolunteer = deleteVolunteer;

        setId("volunteers-view");

        var buttonAdd = new Button("Nouveau bénévole");
        buttonAdd.addThemeVariants(LUMO_PRIMARY);
        buttonAdd.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> newVolunteer());

        grid = new Grid<>();
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeight("95%");

        var lastNameFilter = new TextField();
        lastNameFilter.setPlaceholder("Filtrer par nom");
        lastNameFilter.setClearButtonVisible(true);
        lastNameFilter.setValueChangeMode(EAGER);
        lastNameFilter.addValueChangeListener(e -> filterBy(lastNameFilter.getValue(), Volunteer::getLastName));
        grid.addColumn(Volunteer::getLastName)
                .setHeader("Nom")
                .setSortable(true)
                .setFooter(lastNameFilter);

        var firstNameFilter = new TextField();
        firstNameFilter.setPlaceholder("Filtrer par prénom");
        firstNameFilter.setClearButtonVisible(true);
        firstNameFilter.setValueChangeMode(EAGER);
        firstNameFilter.addValueChangeListener(e -> filterBy(firstNameFilter.getValue(), Volunteer::getFirstName));
        grid.addColumn(Volunteer::getFirstName)
                .setHeader("Prénom")
                .setSortable(true)
                .setFooter(firstNameFilter);

        teamFilter = new ComboBox<>();
        teamFilter.setPlaceholder("Filtrer par équipe");
        teamFilter.setClearButtonVisible(true);
        teamFilter.setItemLabelGenerator(Team::getFullName);
        teamFilter.addValueChangeListener(e -> {
            var team = teamFilter.getValue();
            filterBy(team != null ? team.getCode() : null, Volunteer::getTeamCode);
        });
        grid.addColumn(Volunteer::getTeamCode)
                .setHeader("Équipe")
                .setSortable(true)
                .setFooter(teamFilter);

        grid.addColumn(Volunteer::getPhoneNumber)
                .setHeader("Téléphone")
                .setSortable(false);

        grid.addColumn(createEmailToAnchor())
                .setHeader("Email");

        grid.addComponentColumn(volunteer -> {
            var wrapper = new HorizontalLayout();
            var edit = new Button(VaadinIcon.EDIT.create());
            edit.getStyle().set("cursor", "pointer");
            edit.addClickListener(event -> onEditVolunteer(volunteer));
            wrapper.add(edit);

            var trash = new Button(VaadinIcon.TRASH.create());
            trash.getStyle().set("cursor", "pointer");
            trash.addClickListener(event -> onDeleteVolunteer(volunteer));
            wrapper.add(trash);

            return wrapper;
        });

        add(buttonAdd, grid);
    }

    private void onEditVolunteer(Volunteer volunteer) {
        var dialog = new VolunteerDialog(volunteer, teams, updatedVolunteer -> {
            updateVolunteer.call(updatedVolunteer);
            Notification.show("Bénévole mis à jour", 5000, Position.TOP_CENTER);
            fetchVolunteers();
        });
        dialog.open();
    }

    private void onDeleteVolunteer(Volunteer volunteer) {
        ConfirmDialog.builder()
                .message("Supprimer le bénévole ?")
                .confirmButton("Supprimer", () -> {
                    deleteVolunteer.call(volunteer);
                    Notification.show("Bénévole supprimé", 5000, Position.TOP_CENTER);
                    fetchVolunteers();
                })
                .open();
    }

    private void newVolunteer() {
        var dialog = new VolunteerDialog(teams, volunteer -> {
            addVolunteer.call(volunteer);
            Notification.show("Bénévole ajouté", 5000, Position.TOP_CENTER);
            fetchVolunteers();
        });
        dialog.open();
    }

    private static ComponentRenderer<Anchor, Volunteer> createEmailToAnchor() {
        return new ComponentRenderer<>(volunteer -> new Anchor("mailto:" + volunteer.getEmail(), volunteer.getEmail()));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        fetchTeams();
        fetchVolunteers();
    }

    private void fetchTeams() {
        teams = getTeams.call();
        teamFilter.setItems(teams);
    }

    private void fetchVolunteers() {
        unfilteredData = getVolunteers.call();
        grid.setItems(unfilteredData);
    }

    private void filterBy(String filterValue, Function<Volunteer, String> provider) {
        if (isNotBlank(filterValue)) {
            grid.setItems(unfilteredData
                    .stream()
                    .filter(volunteer -> startsWithIgnoreCase(provider.apply(volunteer), filterValue))
                    .collect(Collectors.toList()));
        } else {
            grid.setItems(unfilteredData);
        }
    }

}
