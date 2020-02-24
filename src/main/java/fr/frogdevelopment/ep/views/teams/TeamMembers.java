package fr.frogdevelopment.ep.views.teams;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import fr.frogdevelopment.ep.model.Volunteer;
import fr.frogdevelopment.ep.views.schedules.EpCalendar;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;

public class TeamMembers extends HorizontalLayout {

    private final ListBox<Volunteer> referentsListBox = new ListBox<>();
    private final ListBox<Volunteer> membersListBox = new ListBox<>();

    TeamMembers(List<Volunteer> volunteers, List<Entry> entries) {
        addTeamLayout(volunteers);
        addTeamCalendar(entries);
    }

    private void addTeamLayout(List<Volunteer> volunteers) {
        var teamLayout = new VerticalLayout();
        var volunteerRenderer = new TextRenderer<>((ItemLabelGenerator<Volunteer>) Volunteer::getFullName);

        referentsListBox.setHeightFull();
        referentsListBox.setReadOnly(true);
        referentsListBox.setRenderer(volunteerRenderer);
        teamLayout.add(titleBox(VaadinIcon.USER_STAR, "Référents"), referentsListBox);

        membersListBox.setHeightFull();
        membersListBox.setReadOnly(true);
        membersListBox.setRenderer(volunteerRenderer);
        teamLayout.add(titleBox(VaadinIcon.USER, "Membres"), membersListBox);

        setVolunteers(volunteers);

        add(teamLayout);
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

    private void addTeamCalendar(List<Entry> entries) {
        var calendar = new EpCalendar();
        calendar.changeView(CalendarViewImpl.LIST_WEEK);
        calendar.setHeight("500px");
        calendar.addEntries(entries);

        add(calendar);
    }

    private Component titleBox(VaadinIcon vaadinIcon, String title) {
        var icon = vaadinIcon.create();
        icon.setSize("22px");
        var text = new H4(title);
        text.getStyle().set("margin-top", "0");
        return new HorizontalLayout(icon, text);
    }
}
