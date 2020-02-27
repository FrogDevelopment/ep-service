package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.client.VolunteersClient;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.teams.TeamNavigationBar.Navigation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Route(value = "team/members", layout = TeamParentView.class)
public class TeamMembersView extends HorizontalLayout implements HasUrlParameter<String>, HasDynamicTitle,
        AfterNavigationObserver {

    private final transient VolunteersClient volunteersClient;

    private final TeamNavigationBar teamNavigationBar = new TeamNavigationBar(Navigation.MEMBERS);
    private final ListBox<Volunteer> referentsListBox = new ListBox<>();
    private final ListBox<Volunteer> membersListBox = new ListBox<>();

    private String teamCode;

    public TeamMembersView(VolunteersClient volunteersClient) {
        this.volunteersClient = volunteersClient;

        add(teamNavigationBar);

        var teamLayout = new VerticalLayout();
        var volunteerRenderer = new TextRenderer<>((ItemLabelGenerator<Volunteer>) Volunteer::getFullName);

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
    public void setParameter(BeforeEvent event, String parameter) {
        teamCode = parameter;
        teamNavigationBar.setTeam(teamCode);
    }

    @Override
    public String getPageTitle() {
        return teamCode;
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
