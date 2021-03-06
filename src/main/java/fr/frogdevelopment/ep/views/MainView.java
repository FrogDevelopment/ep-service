package fr.frogdevelopment.ep.views;

import static com.vaadin.flow.component.applayout.AppLayout.Section.DRAWER;
import static com.vaadin.flow.component.icon.VaadinIcon.ABACUS;
import static com.vaadin.flow.component.icon.VaadinIcon.CALENDAR;
import static com.vaadin.flow.component.icon.VaadinIcon.CALENDAR_BRIEFCASE;
import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;
import static com.vaadin.flow.component.icon.VaadinIcon.GROUP;
import static com.vaadin.flow.component.icon.VaadinIcon.HEART;
import static com.vaadin.flow.component.icon.VaadinIcon.UPLOAD;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import fr.frogdevelopment.ep.views.timetable.TimetableView;
import fr.frogdevelopment.ep.views.calendar.CalendarView;
import fr.frogdevelopment.ep.views.stats.StatsView;
import fr.frogdevelopment.ep.views.teams.TeamsView;
import fr.frogdevelopment.ep.views.upload.UploadView;
import fr.frogdevelopment.ep.views.volunteers.VolunteersView;
import java.util.ArrayList;

@JsModule("./styles/shared-styles.js")
@PWA(name = "Solidays - EP", shortName = "Solidays - EP", enableInstallPrompt = false)
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends AppLayout {

    private final Tabs menu;

    public MainView() {
        setPrimarySection(DRAWER);

        var logo = new Image("icons/icon.png", "Solidays Logo");
        logo.setHeight("100px");
        logo.getStyle().set("display", "block");
        logo.getStyle().set("margin-top", "5px");
        logo.getStyle().set("margin", "0 auto");

        var title = new H4(" Entrées Public");
        title.getStyle().set("margin-top", "5px");
        title.getStyle().set("text-align", "center");
        title.setWidthFull();
        menu = createMenuTabs();
        addToDrawer(logo, title, menu);
    }

    private static Tabs createMenuTabs() {
        final var tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final var tabs = new ArrayList<Tab>();
        tabs.add(createTab(CALENDAR_BRIEFCASE, TimetableView.class));
        tabs.add(createTab(HEART, VolunteersView.class));
        tabs.add(createTab(GROUP, TeamsView.class));
        tabs.add(createTab(CALENDAR, CalendarView.class));
        tabs.add(createTab(ABACUS, StatsView.class));
        tabs.add(createTab(UPLOAD, UploadView.class));
        tabs.add(createTab(EXIT, new Anchor("logout", "Logout")));
        return tabs.toArray(new Tab[0]);
    }

    private static Tab createTab(VaadinIcon vaadinIcon, Class<? extends Component> viewClass) {
        PageTitle pageTitle = viewClass.getAnnotation(PageTitle.class);
        return createTab(vaadinIcon, populateLink(new RouterLink(null, viewClass), pageTitle.value()));
    }

    private static Tab createTab(VaadinIcon vaadinIcon, Component content) {
        final var tab = new Tab();
        tab.add(vaadinIcon.create());
        tab.add(content);
        return tab;
    }

    private static <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        var target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        menu.getChildren()
                .filter(tab -> tab.getChildren()
                        .filter(child -> child instanceof RouterLink)
                        .anyMatch(child -> ((RouterLink) child).getHref().equals(target)))
                .findFirst()
                .ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }
}
