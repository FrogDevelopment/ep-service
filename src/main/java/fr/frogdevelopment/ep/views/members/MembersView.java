package fr.frogdevelopment.ep.views.members;

import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_ROW_BORDERS;
import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import fr.frogdevelopment.ep.domain.Member;
import fr.frogdevelopment.ep.implementation.AddMember;
import fr.frogdevelopment.ep.implementation.GetMembers;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.members.newmember.NewMemberView;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Route(value = "members", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Members")
@CssImport("./styles/views/members/members-view.css")
public class MembersView extends Div implements AfterNavigationObserver {

    private final AddMember addMember;
    private final GetMembers getMembers;

    private final Grid<Member> grid;
    private List<Member> unfilteredData;

    public MembersView(AddMember addMember, GetMembers getMembers) {
        this.addMember = addMember;
        this.getMembers = getMembers;

        setId("members-view");

        var buttonAdd = new Button("Nouveau membre");
        buttonAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonAdd.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> newMember());

        grid = new Grid<>();
        grid.setId("list");
        grid.addThemeVariants(LUMO_NO_BORDER, LUMO_NO_ROW_BORDERS);
        grid.setHeight("95%");

        var lastNameFilter = new TextField();
        lastNameFilter.setPlaceholder("Filtrer par nom");
        lastNameFilter.setAutoselect(true);
        lastNameFilter.setAutofocus(true);
        lastNameFilter.setClearButtonVisible(true);
        lastNameFilter.setValueChangeMode(EAGER);
        lastNameFilter.addValueChangeListener(e -> filterBy(lastNameFilter, Member::getLastName));
        grid.addColumn(Member::getLastName)
                .setHeader("Nom")
                .setSortable(true)
                .setFooter(lastNameFilter);

        var firstNameFilter = new TextField();
        firstNameFilter.setPlaceholder("Filtrer par prénom");
        firstNameFilter.setAutoselect(true);
        firstNameFilter.setAutofocus(true);
        firstNameFilter.setClearButtonVisible(true);
        firstNameFilter.setValueChangeMode(EAGER);
        firstNameFilter.addValueChangeListener(e -> filterBy(firstNameFilter, Member::getFirstName));
        grid.addColumn(Member::getFirstName)
                .setHeader("Prénom")
                .setSortable(true)
                .setFooter(firstNameFilter);
        grid.addColumn(Member::getPhoneNumber)
                .setHeader("Téléphone")
                .setSortable(false);

        grid.addColumn(createEmailToAnchor())
                .setHeader("Email");

        grid.addColumn(new NativeButtonRenderer<>(
                item -> "Modifier",
                clickedItem -> {
                    // remove the item
                })
        );
        grid.addColumn(new NativeButtonRenderer<>(
                item -> "Supprimer",
                clickedItem -> {
                    // remove the item
                })
        );

        add(buttonAdd, grid);
    }

    private void newMember() {
        var dialog = new NewMemberView(member -> {
            addMember.call(member);
            Notification.show("Données mise à jour");
            afterNavigation(null);
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
        unfilteredData = getMembers.call();
        grid.setItems(unfilteredData);
    }

    private void filterBy(TextField filter, Function<Member, String> provider) {
        var filterValue = filter.getValue();
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
