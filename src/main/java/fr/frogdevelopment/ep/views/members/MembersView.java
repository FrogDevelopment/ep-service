package fr.frogdevelopment.ep.views.members;

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
import fr.frogdevelopment.ep.implementation.AddMember;
import fr.frogdevelopment.ep.implementation.DeleteMember;
import fr.frogdevelopment.ep.implementation.GetMembers;
import fr.frogdevelopment.ep.implementation.GetTeams;
import fr.frogdevelopment.ep.implementation.UpdateMember;
import fr.frogdevelopment.ep.model.Member;
import fr.frogdevelopment.ep.model.Team;
import fr.frogdevelopment.ep.views.ConfirmDialog;
import fr.frogdevelopment.ep.views.MainView;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Route(value = "members", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Members")
@CssImport("./styles/views/members/members-view.css")
public class MembersView extends Div implements AfterNavigationObserver {

    private final transient AddMember addMember;
    private final transient GetMembers getMembers;
    private final transient GetTeams getTeams;
    private final transient UpdateMember updateMember;
    private final transient DeleteMember deleteMember;

    private final Grid<Member> grid;
    private List<Member> unfilteredData;
    private final ComboBox<Team> teamFilter;
    private List<Team> teams;

    public MembersView(AddMember addMember,
                       GetMembers getMembers,
                       GetTeams getTeams,
                       UpdateMember updateMember,
                       DeleteMember deleteMember) {
        this.addMember = addMember;
        this.getMembers = getMembers;
        this.getTeams = getTeams;
        this.updateMember = updateMember;
        this.deleteMember = deleteMember;

        setId("members-view");

        var buttonAdd = new Button("Nouveau bénévole");
        buttonAdd.addThemeVariants(LUMO_PRIMARY);
        buttonAdd.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> newMember());

        grid = new Grid<>();
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeight("95%");

        var lastNameFilter = new TextField();
        lastNameFilter.setPlaceholder("Filtrer par nom");
        lastNameFilter.setClearButtonVisible(true);
        lastNameFilter.setValueChangeMode(EAGER);
        lastNameFilter.addValueChangeListener(e -> filterBy(lastNameFilter.getValue(), Member::getLastName));
        grid.addColumn(Member::getLastName)
                .setHeader("Nom")
                .setSortable(true)
                .setFooter(lastNameFilter);

        var firstNameFilter = new TextField();
        firstNameFilter.setPlaceholder("Filtrer par prénom");
        firstNameFilter.setClearButtonVisible(true);
        firstNameFilter.setValueChangeMode(EAGER);
        firstNameFilter.addValueChangeListener(e -> filterBy(firstNameFilter.getValue(), Member::getFirstName));
        grid.addColumn(Member::getFirstName)
                .setHeader("Prénom")
                .setSortable(true)
                .setFooter(firstNameFilter);

        teamFilter = new ComboBox<>();
        teamFilter.setPlaceholder("Filtrer par équipe");
        teamFilter.setClearButtonVisible(true);
        teamFilter.setItemLabelGenerator(Team::getFullName);
        teamFilter.addValueChangeListener(e -> {
            var team = teamFilter.getValue();
            filterBy(team != null ? team.getCode() : null, Member::getTeamCode);
        });
        grid.addColumn(Member::getTeamCode)
                .setHeader("Équipe")
                .setSortable(true)
                .setFooter(teamFilter);

        grid.addColumn(Member::getPhoneNumber)
                .setHeader("Téléphone")
                .setSortable(false);

        grid.addColumn(createEmailToAnchor())
                .setHeader("Email");

        grid.addComponentColumn(member -> {
            var wrapper = new HorizontalLayout();
            var edit = VaadinIcon.EDIT.create();
            edit.getStyle().set("cursor", "pointer");
            edit.addClickListener(event -> onEditMember(member));
            wrapper.add(edit);

            var trash = VaadinIcon.TRASH.create();
            trash.getStyle().set("cursor", "pointer");
            trash.addClickListener(event -> onDeleteMember(member));
            wrapper.add(trash);

            return wrapper;
        });

        add(buttonAdd, grid);
    }

    private void onEditMember(Member member) {
        var dialog = new MemberDialog(member, teams, updatedMember -> {
            updateMember.call(updatedMember);
            Notification.show("Bénévole mis à jour", 5000, Position.TOP_CENTER);
            fetchMembers();
        });
        dialog.open();
    }

    private void onDeleteMember(Member member) {
        ConfirmDialog.builder()
                .message("Supprimer le bénévole ?")
                .confirmButton("Supprimer", () -> {
                    deleteMember.call(member);
                    Notification.show("Bénévole supprimé", 5000, Position.TOP_CENTER);
                    fetchMembers();
                })
                .open();
    }

    private void newMember() {
        var dialog = new MemberDialog(teams, member -> {
            addMember.call(member);
            Notification.show("Bénévole ajouté", 5000, Position.TOP_CENTER);
            fetchMembers();
        });
        dialog.open();
    }

    private static ComponentRenderer<Div, Member> createEmailToAnchor() {
        return new ComponentRenderer<>(member -> {
            var anchor = new Anchor("mailto:" + member.getEmail(), member.getEmail());
            anchor.getElement().getThemeList().add("font-size-xs");
            var div = new Div(anchor);
            div.addClassName("employee-column");
            return div;
        });
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        fetchTeams();
        fetchMembers();
    }

    private void fetchTeams() {
        teams = getTeams.call();
        teamFilter.setItems(teams);
    }

    private void fetchMembers() {
        unfilteredData = getMembers.call();
        grid.setItems(unfilteredData);
    }

    private void filterBy(String filterValue, Function<Member, String> provider) {
        if (isNotBlank(filterValue)) {
            grid.setItems(unfilteredData
                    .stream()
                    .filter(member -> startsWithIgnoreCase(provider.apply(member), filterValue))
                    .collect(Collectors.toList()));
        } else {
            grid.setItems(unfilteredData);
        }
    }

}
