package fr.frogdevelopment.ep.views.teams;

import static com.vaadin.flow.component.icon.VaadinIcon.ABACUS;
import static com.vaadin.flow.component.icon.VaadinIcon.CALENDAR_USER;
import static com.vaadin.flow.component.icon.VaadinIcon.USER;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import java.util.HashMap;
import java.util.Map;

class TeamNavigationBar extends Tabs {

    private final Map<Tab, String> navigationByTag = new HashMap<>();
    private String teamCode;

    TeamNavigationBar(Navigation current) {
        setOrientation(Tabs.Orientation.VERTICAL);

        Tab selectedTab = null;
        for (Navigation navigation : Navigation.values()) {
            var title = new HorizontalLayout();
            title.add(navigation.icon.create());
            title.add(new Text(navigation.label));
            var tab = new Tab(title);
            if (navigation.equals(current)) {
                selectedTab = tab;
            }
            navigationByTag.put(tab, navigation.location);
            add(tab);
        }

        setSelectedTab(selectedTab);

        addSelectedChangeListener(event -> navigate(navigationByTag.get(getSelectedTab()) + teamCode));
    }

    void setTeam(String teamCode) {
        this.teamCode = teamCode;
    }

    private void navigate(String location) {
        getUI().ifPresent(ui -> ui.navigate(location));
    }

    enum Navigation {
        //        DASHBOARD("Dashboard", "team/dashboard/"),
        MEMBERS(USER, "Membres", "team/members/"),
        PLANNING(CALENDAR_USER, "Timetable", "team/planning/"),
        STATS(ABACUS, "Stats", "team/stats/");

        private final VaadinIcon icon;
        private final String label;
        private final String location;

        Navigation(VaadinIcon icon, String label, String location) {
            this.icon = icon;
            this.label = label;
            this.location = location;
        }
    }
}
