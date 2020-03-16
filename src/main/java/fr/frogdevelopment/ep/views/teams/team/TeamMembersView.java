package fr.frogdevelopment.ep.views.teams.team;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.VolunteersClient;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.teams.team.TeamNavigationBar.Navigation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Route(value = "team/members", layout = TeamParentView.class)
public class TeamMembersView extends AbstractTeamView {

    private final transient VolunteersClient volunteersClient;

    private final ListBox<Volunteer> referentsListBox = new ListBox<>();
    private final ListBox<Volunteer> membersListBox = new ListBox<>();

    public TeamMembersView(VolunteersClient volunteersClient) {
        super(Navigation.MEMBERS);

        this.volunteersClient = volunteersClient;

        var teamLayout = new VerticalLayout();
        var volunteerRenderer = new TextRenderer<>(Volunteer::getFullName);

        referentsListBox.setHeightFull();
        referentsListBox.setReadOnly(true);
        referentsListBox.setRenderer(volunteerRenderer);
        teamLayout.add(titleBox(VaadinIcon.USER_STAR, "Référents"), referentsListBox);

        membersListBox.setHeightFull();
        membersListBox.setReadOnly(true);
        membersListBox.setRenderer(volunteerRenderer);
        teamLayout.add(titleBox(VaadinIcon.USER, String.format("Membres (%s)", membersListBox.getChildren().count())),
                membersListBox);

        add(teamLayout);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setVolunteers(volunteersClient.getAll(teamCode));
    }

    private void setVolunteers(List<Volunteer> volunteers) {
        referentsListBox.removeAll();
        membersListBox.removeAll();

        var referents = new ArrayList<Volunteer>();
        var members = new ArrayList<Volunteer>();

        volunteers
                .stream()
                .sorted(Comparator.comparing(Volunteer::getFullName))
                .forEach(volunteer -> {
                    if (volunteer.isReferent()) {
                        referents.add(volunteer);
                    } else {
                        members.add(volunteer);
                    }
                });

        referentsListBox.setItems(referents);
        membersListBox.setItems(members);
    }

    private Component titleBox(VaadinIcon vaadinIcon, String title) {
        var icon = vaadinIcon.create();
        icon.setSize("22px");
        var text = new H4(title);
        text.getStyle().set("margin-top", "0");
        return new HorizontalLayout(icon, text);
    }
}
