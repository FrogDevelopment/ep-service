package fr.frogdevelopment.ep.views.volunteers;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.grid.ColumnTextAlign.CENTER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES;
import static com.vaadin.flow.component.icon.VaadinIcon.EDIT;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS_CIRCLE;
import static com.vaadin.flow.component.icon.VaadinIcon.TRASH;
import static com.vaadin.flow.component.icon.VaadinIcon.USER;
import static com.vaadin.flow.component.icon.VaadinIcon.USER_STAR;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.TeamsClient;
import fr.frogdevelopment.ep.client.VolunteersClient;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.components.ConfirmDialog;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@PageTitle("Bénévoles")
@Route(value = "volunteers", layout = MainView.class)
@CssImport("./styles/views/volunteers/volunteers-view.css")
public class VolunteersView extends Div implements AfterNavigationObserver {

    private final transient VolunteersClient volunteersClient;
    private final transient TeamsClient teamsClient;

    private final Grid<Volunteer> grid = new Grid<>();

    private ListDataProvider<Volunteer> dataProvider;
    private List<Team> teams;
    private ComboBox<String> friendsFilter;
    private ComboBox<Team> teamsFilter;

    public VolunteersView(VolunteersClient volunteersClient,
                          TeamsClient teamsClient) {
        this.volunteersClient = volunteersClient;
        this.teamsClient = teamsClient;

        setId("volunteers-view");

        createButtonLayout();

        createGrid();
    }

    private void createButtonLayout() {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        var buttonAdd = new Button("Ajouter un bénévole", PLUS_CIRCLE.create());
        buttonAdd.addThemeVariants(LUMO_PRIMARY);
        buttonAdd.addClickListener(event -> onAdd());
        buttonLayout.add(buttonAdd);

        add(buttonLayout);
    }

    private void createGrid() {
        grid.setId("list");
        grid.setSelectionMode(SelectionMode.NONE);
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_ROW_STRIPES);
        grid.setHeight("95%");

        var referentColumn = grid.addComponentColumn(this::getReferentRenderer)
                .setFlexGrow(0)
                .setAutoWidth(true);

        var lastNameColumn = grid.addColumn(Volunteer::getLastName)
                .setHeader("Nom")
                .setSortable(true);

        var firstNameColumn = grid.addColumn(Volunteer::getFirstName)
                .setHeader("Prénom")
                .setSortable(true);

        var friendsColumn = grid.addColumn(Volunteer::getFriendsGroup)
                .setHeader("Groupe\nd'amis")
                .setSortable(true)
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setTextAlign(CENTER);

        var teamColumn = grid.addColumn(this::getVolunteerTeam)
                .setHeader("Équipe")
                .setSortable(true);

        grid.addColumn(Volunteer::getPhoneNumber)
                .setHeader("Téléphone")
                .setSortable(false)
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addComponentColumn(this::getEmailToAnchor)
                .setHeader("Email");

        HeaderRow filterRow = grid.appendHeaderRow();
        // Referent filter
        ComboBox<String> referentFilter = new ComboBox<>();
        referentFilter.setItems("Oui", "Non");
        referentFilter.setClearButtonVisible(true);
        referentFilter.addValueChangeListener(event -> dataProvider.addFilter(person -> {
            var value = referentFilter.getValue();
            if (value == null) {
                return true;
            }
            switch (value) {
                case "Oui":
                    return person.isReferent();
                case "Non":
                default:
                    return !person.isReferent();
            }
        }));
        filterRow.getCell(referentColumn).setComponent(referentFilter);
        referentFilter.setWidth("70px");
        referentFilter.setPlaceholder("Filtrer");

        // lastName filter
        TextField lastNameFilter = new TextField();
        lastNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        lastNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                person -> containsIgnoreCase(person.getLastName(), lastNameFilter.getValue())));
        filterRow.getCell(lastNameColumn).setComponent(lastNameFilter);
        lastNameFilter.setSizeFull();
        lastNameFilter.setPlaceholder("Filtrer");

        // firstName filter
        TextField firstNameFilter = new TextField();
        firstNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        firstNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                person -> containsIgnoreCase(person.getFirstName(), firstNameFilter.getValue())));
        filterRow.getCell(firstNameColumn).setComponent(firstNameFilter);
        firstNameFilter.setSizeFull();
        firstNameFilter.setPlaceholder("Filter");

        // Friends filter
        friendsFilter = new ComboBox<>();
        friendsFilter.setClearButtonVisible(true);
        friendsFilter.addValueChangeListener(event -> dataProvider
                .addFilter(person -> {
                    var value = friendsFilter.getValue();
                    if (value == null) {
                        return true;
                    }
                    return value.equals(person.getFriendsGroup());
                }));
        filterRow.getCell(friendsColumn).setComponent(friendsFilter);
        friendsFilter.setPlaceholder("Filtrer");

        // Team filter
        teamsFilter = new ComboBox<>();
        teamsFilter.setClearButtonVisible(true);
        teamsFilter.setItemLabelGenerator(Team::getName);
        teamsFilter.addValueChangeListener(event -> dataProvider
                .addFilter(person -> {
                    var value = teamsFilter.getValue();
                    if (value == null) {
                        return true;
                    }
                    return value.getCode().equals(person.getTeamCode());
                }));
        filterRow.getCell(teamColumn).setComponent(teamsFilter);
        teamsFilter.setPlaceholder("Filtrer");

        add(grid);

        GridContextMenu<Volunteer> contextMenu = new GridContextMenu<>(grid);
        var edit = new HorizontalLayout(EDIT.create(), new Label("Modifier"));
        contextMenu.addItem(edit, event -> event.getItem().ifPresentOrElse(this::onEdit, this::smallError));
        var delete = new HorizontalLayout(TRASH.create(), new Label("Supprimer"));
        contextMenu.addItem(delete, event -> event.getItem().ifPresentOrElse(this::onDelete, this::smallError));
    }

    private void smallError() {
        Notification.show("Petit problème ! ", 5000, Position.TOP_CENTER);
    }

    private Icon getReferentRenderer(Volunteer volunteer) {
        if (volunteer.isReferent()) {
            var icon = USER_STAR.create();
            icon.setColor("gold");
            return icon;
        }
        return USER.create();
    }

    private String getVolunteerTeam(Volunteer volunteer) {
        return teams.stream()
                .filter(t -> t.getCode().equals(volunteer.getTeamCode()))
                .findFirst()
                .map(Team::getName)
                .orElse("-");
    }

    private Anchor getEmailToAnchor(Volunteer volunteer) {
        return new Anchor("mailto:" + volunteer.getEmail(), volunteer.getEmail());
    }

    private void onAdd() {
        new VolunteerDialog(teams, volunteer -> {
            volunteer = volunteersClient.create(volunteer);
            Notification.show("Bénévole ajouté", 5000, Position.TOP_CENTER);
            dataProvider.getItems().add(volunteer);
            dataProvider.refreshAll();
        }).open();
    }

    private void onEdit(Volunteer volunteer) {
        var dialog = new VolunteerDialog(volunteer, teams, updatedVolunteer -> {
            volunteersClient.update(updatedVolunteer);
            Notification.show("Bénévole mis à jour", 5000, Position.TOP_CENTER);
            dataProvider.refreshItem(volunteer);
            dataProvider.refreshAll();
        });
        dialog.open();
    }

    private void onDelete(Volunteer volunteer) {
        ConfirmDialog.builder()
                .message("Supprimer le bénévole ?")
                .confirmButton("Supprimer", () -> {
                    volunteersClient.delete(volunteer);
                    Notification.show("Bénévole supprimé", 5000, Position.TOP_CENTER);
                    dataProvider.getItems().remove(volunteer);
                    dataProvider.refreshAll();
                })
                .open();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        teams = teamsClient.getAll();
        teamsFilter.setItems(teams);

        var volunteers = volunteersClient.getAll();

        friendsFilter.setItems(volunteers
                .stream()
                .map(Volunteer::getFriendsGroup)
                .distinct()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList()));

        dataProvider = DataProvider.ofCollection(volunteers);
        grid.setDataProvider(dataProvider);
    }

}
