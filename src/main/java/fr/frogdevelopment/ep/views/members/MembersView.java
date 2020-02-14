package fr.frogdevelopment.ep.views.members;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import fr.frogdevelopment.ep.implementation.BackendService;
import fr.frogdevelopment.ep.implementation.Employee;
import fr.frogdevelopment.ep.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "members", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Members")
@CssImport("styles/views/members/members-view.css")
public class MembersView extends Div implements AfterNavigationObserver {

    @Autowired
    private BackendService service;
    private final Grid<Employee> grid;

    public MembersView() {
        setId("members-view");
        grid = new Grid<>();
        grid.setId("list");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS);
        grid.setHeightFull();
        grid.addColumn(new ComponentRenderer<>(employee -> {
            var h3 = new H3(employee.getLastname() + ", " + employee.getFirstname());
            var anchor = new Anchor("mailto:" + employee.getEmail(), employee.getEmail());
            anchor.getElement().getThemeList().add("font-size-xs");
            var div = new Div(h3, anchor);
            div.addClassName("employee-column");
            return div;
        }));

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        grid.setItems(service.getEmployees());
    }
}
