package fr.frogdevelopment.ep.views.volunteers;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class FilterDialog extends Dialog {

    private final TextField lastName = new TextField("Nom");
    private final TextField firstName = new TextField("Prénom");
    private final ComboBox<Team> teams = new ComboBox<>("Équipe");
    private final Checkbox referent = new Checkbox("Référent");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final List<Team> teamValues;
    private final transient OnFilterListener onFilterListener;

    public FilterDialog(List<Team> teamValues, OnFilterListener onFilterListener) {
        super();
        this.teamValues = teamValues;
        this.onFilterListener = onFilterListener;

        var wrapper = createWrapper();

        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        add(wrapper);
    }

    private VerticalLayout createWrapper() {
        var wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        var formLayout = new FormLayout();

        lastName.setClearButtonVisible(true);
        lastName.setValueChangeMode(EAGER);
        formLayout.add(lastName);

        firstName.setClearButtonVisible(true);
        firstName.setValueChangeMode(EAGER);
        formLayout.add(firstName);

        teams.setItems(teamValues);
        teams.setClearButtonVisible(true);
        teams.setItemLabelGenerator(Team::getName);
        formLayout.add(teams);

        formLayout.add(referent);

        wrapper.add(formLayout);
    }

    private void createButtonLayout(VerticalLayout wrapper) {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel);
        buttonLayout.add(save);

        wrapper.add(buttonLayout);

        cancel.addClickListener(e -> onCancel());
        save.addClickListener(e -> onValidate());
    }

    private void onValidate() {
        onFilterListener.onFilter(Filter.builder()
                .referent(referent.getValue())
                .lastName(lastName.getValue())
                .firstName(firstName.getValue())
                .teamCode(teams.getValue() != null ? teams.getValue().getCode() : null)
                .build());
        this.close();
    }

    private void onCancel() {
        this.close();
    }

    @FunctionalInterface
    public interface OnFilterListener {

        void onFilter(Filter filter);
    }

    @Getter
    @Builder
    static class Filter {

        private final boolean referent;
        private final String lastName;
        private final String firstName;
        private final String teamCode;
    }
}
