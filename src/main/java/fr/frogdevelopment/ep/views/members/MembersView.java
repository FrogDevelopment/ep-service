package fr.frogdevelopment.ep.views.members;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import fr.frogdevelopment.ep.implementation.Employee;
import fr.frogdevelopment.ep.views.BackendService;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.members.newmember.NewMemberView;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "members", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Members")
@CssImport("./styles/views/members/members-view.css")
public class MembersView extends Div implements AfterNavigationObserver {

    @Autowired
    private BackendService service;

    private final TextField searchField;
    private final Button buttonAdd = new Button("Add");
    private final Grid<Employee> grid;
    private List<Employee> unfilteredData;

    public MembersView() {
        setId("members-view");
        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.addClassName("button-layout");
//        wrapper.setWidthFull();
//        wrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        searchField = new TextField();
        searchField.setPlaceholder("Search member");
        searchField.setAutoselect(true);
        searchField.setAutofocus(true);
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateList());
        wrapper.add(searchField);

        buttonAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonAdd.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                Dialog dialog = new Dialog(new NewMemberView());
                dialog.setCloseOnEsc(false);
                dialog.setCloseOnOutsideClick(false);
                dialog.open();
            }
        });
        wrapper.add(buttonAdd);

        grid = new Grid<>();
        grid.setId("list");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.setHeight("50%");
        grid.addColumn(new ComponentRenderer<>(employee -> {
            var h3 = new H3(employee.getLastname() + ", " + employee.getFirstname());
            var div = new Div(h3);
            div.addClassName("employee-column");
            return div;
        }));
        grid.addColumn(new ComponentRenderer<>(employee -> {
            var anchor = new Anchor("mailto:" + employee.getEmail(), employee.getEmail());
            anchor.getElement().getThemeList().add("font-size-xs");
            var div = new Div(anchor);
            div.addClassName("employee-column");
            return div;
        }));

        add(wrapper, grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        unfilteredData = service.getEmployees();
        grid.setItems(unfilteredData);
    }

    private void updateList() {
        String searchFieldValue = searchField.getValue();
        if (StringUtils.isNotBlank(searchFieldValue)) {
            grid.setItems(unfilteredData
                    .stream()
                    .filter(employee -> startsWithIgnoreCase(employee.getFirstname(), searchFieldValue)
                            || startsWithIgnoreCase(employee.getLastname(), searchFieldValue))
                    .collect(Collectors.toList()));
        } else {
            grid.setItems(unfilteredData);
        }
    }

}
