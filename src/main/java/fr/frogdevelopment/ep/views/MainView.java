package fr.frogdevelopment.ep.views;

import static com.vaadin.flow.component.icon.VaadinIcon.ABACUS;
import static com.vaadin.flow.component.icon.VaadinIcon.CALENDAR;
import static com.vaadin.flow.component.icon.VaadinIcon.CALENDAR_BRIEFCASE;
import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;
import static com.vaadin.flow.component.icon.VaadinIcon.GROUP;
import static com.vaadin.flow.component.icon.VaadinIcon.HEART;
import static com.vaadin.flow.component.icon.VaadinIcon.QUESTION_CIRCLE;
import static com.vaadin.flow.component.icon.VaadinIcon.UPLOAD;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import fr.frogdevelopment.ep.views.about.AboutView;
import fr.frogdevelopment.ep.views.planning.PlanningView;
import fr.frogdevelopment.ep.views.schedules.SchedulesView;
import fr.frogdevelopment.ep.views.stats.StatsView;
import fr.frogdevelopment.ep.views.teams.TeamsView;
import fr.frogdevelopment.ep.views.upload.UploadView;
import fr.frogdevelopment.ep.views.volunteers.VolunteersView;
import java.util.ArrayList;

@JsModule("./styles/shared-styles.js")
@PWA(name = "Solidays - EP", shortName = "Solidays - EP")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends AppLayout {

    private final Tabs menu;

    public MainView() {
        setPrimarySection(AppLayout.Section.DRAWER);

        var navBarWrapper = new HorizontalLayout();
        navBarWrapper.setWidthFull();
        var img = new Image("icons/icon.png", "Solidays Logo");
        img.setHeight("44px");
        var title = new Label(" Solidays - EP");
        title.setWidthFull();
        navBarWrapper.add(new DrawerToggle(), img, title);

        addToNavbar(navBarWrapper);

        menu = createMenuTabs();
        addToDrawer(menu);
    }

    private static Tabs createMenuTabs() {
        final var tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final var tabs = new ArrayList<Tab>();
        tabs.add(createTab(CALENDAR_BRIEFCASE, PlanningView.class));
        tabs.add(createTab(UPLOAD, UploadView.class));
        tabs.add(createTab(HEART, VolunteersView.class));
        tabs.add(createTab(GROUP, TeamsView.class));
        tabs.add(createTab(CALENDAR, SchedulesView.class));
        tabs.add(createTab(ABACUS, StatsView.class));
        tabs.add(createTab(QUESTION_CIRCLE, AboutView.class));
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
