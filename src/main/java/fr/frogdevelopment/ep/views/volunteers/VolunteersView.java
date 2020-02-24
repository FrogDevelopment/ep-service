package fr.frogdevelopment.ep.views.volunteers;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
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
import fr.frogdevelopment.ep.views.volunteers.FilterDialog.Filter;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Bénévoles")
@Route(value = "volunteers", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@CssImport("./styles/views/volunteers/volunteers-view.css")
public class VolunteersView extends Div implements AfterNavigationObserver {

    private final transient AddVolunteer addVolunteer;
    private final transient GetVolunteers getVolunteers;
    private final transient GetTeams getTeams;
    private final transient UpdateVolunteer updateVolunteer;
    private final transient DeleteVolunteer deleteVolunteer;

    private final Grid<Volunteer> grid = new Grid<>();
    private final Button clearFilter = new Button("Enlever filtre", VaadinIcon.CLOSE_SMALL.create());
    private final Button buttonFilter = new Button("Filtrer", VaadinIcon.FILTER.create());

    private List<Volunteer> unfilteredData;
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

        createButtonLayout();

        createGrid();
    }

    private void createButtonLayout() {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        clearFilter.addThemeVariants(LUMO_TERTIARY);
        clearFilter.addClickListener(event -> clearFilter());
        clearFilter.setVisible(false);
        buttonLayout.add(clearFilter);
        add(buttonLayout);

        buttonFilter.addThemeVariants(LUMO_TERTIARY);
        buttonFilter.addClickListener(event -> openFilterDialog());
        buttonLayout.add(buttonFilter);

        var buttonAdd = new Button("Ajouter un bénévole", VaadinIcon.PLUS_CIRCLE.create());
        buttonAdd.addThemeVariants(LUMO_PRIMARY);
        buttonAdd.addClickListener(event -> newVolunteer());
        buttonLayout.add(buttonAdd);
    }

    private void createGrid() {
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_ROW_STRIPES);
        grid.setHeight("95%");

        grid.addComponentColumn(getReferentRenderer())
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addColumn(Volunteer::getLastName)
                .setHeader("Nom")
                .setSortable(true);

        grid.addColumn(Volunteer::getFirstName)
                .setHeader("Prénom")
                .setSortable(true);

        grid.addColumn(v -> teams.stream()
                .filter(t -> t.getCode().endsWith(v.getTeamCode()))
                .findFirst()
                .map(Team::getName)
                .orElse("-"))
                .setHeader("Équipe")
                .setSortable(true);

        grid.addColumn(Volunteer::getPhoneNumber)
                .setHeader("Téléphone")
                .setSortable(false)
                .setFlexGrow(0)
                .setAutoWidth(true);

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
        })
                .setFlexGrow(0)
                .setAutoWidth(true);

        add(grid);
    }

    private ValueProvider<Volunteer, Icon> getReferentRenderer() {
        return v -> {
            if (v.isReferent()) {
                var icon = VaadinIcon.USER_STAR.create();
                icon.setColor("gold");
                return icon;
            }
            return VaadinIcon.USER.create();
        };
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

    private void openFilterDialog() {
        new FilterDialog(teams, this::filterBy).open();
    }

    private void filterBy(Filter filter) {
        var stream = unfilteredData.stream();

        if (isNotBlank(filter.getLastName())) {
            stream = stream.filter(v -> startsWithIgnoreCase(v.getLastName(), filter.getLastName()));
        }

        if (isNotBlank(filter.getFirstName())) {
            stream = stream.filter(v -> startsWithIgnoreCase(v.getFirstName(), filter.getFirstName()));
        }

        if (isNotBlank(filter.getTeamCode())) {
            stream = stream.filter(v -> startsWithIgnoreCase(v.getTeamCode(), filter.getTeamCode()));
        }

        if (filter.isReferent()) {
            stream = stream.filter(Volunteer::isReferent);
        }

        grid.setItems(stream.collect(Collectors.toList()));
        clearFilter.setVisible(true);
        buttonFilter.setVisible(false);
    }

    private void clearFilter() {
        grid.setItems(unfilteredData);
        clearFilter.setVisible(false);
        buttonFilter.setVisible(true);
    }

    private static ComponentRenderer<Anchor, Volunteer> createEmailToAnchor() {
        return new ComponentRenderer<>(volunteer -> new Anchor("mailto:" + volunteer.getEmail(), volunteer.getEmail()));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        teams = getTeams.getAll();
        fetchVolunteers();
    }

    private void fetchVolunteers() {
        unfilteredData = getVolunteers.getAll();
        grid.setItems(unfilteredData);
    }

}
