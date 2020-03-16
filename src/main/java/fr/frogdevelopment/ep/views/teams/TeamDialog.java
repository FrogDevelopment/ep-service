package fr.frogdevelopment.ep.views.teams;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import fr.frogdevelopment.ep.model.Team;

@CssImport("./styles/views/volunteers/volunteer-dialog.css")
public class TeamDialog extends Dialog {

    @FunctionalInterface
    public interface OnValidListener {

        void onValid(Team team);
    }

    private final transient OnValidListener onValidListener;

    // The object that will be edited
    private final Team teamBeingEdited;

    private final Binder<Team> binder = new Binder<>(Team.class);

    private final TextField codeField = new TextField();
    private final TextField nameField = new TextField();

    private final Button cancel = new Button("Annuler");
    private final Button save = new Button("Sauvegarder");

    public TeamDialog(OnValidListener onValidListener) {
        this(null, onValidListener);
    }

    public TeamDialog(Team team, OnValidListener onValidListener) {
        super();
        this.onValidListener = onValidListener;
        teamBeingEdited = team == null ? new Team() : team;

        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);

        setId("new-team-view");
        var wrapper = createWrapper();

        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        this.setWidth("400px");
    }

    private VerticalLayout createWrapper() {
        var wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);

        add(wrapper);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        var formLayout = new FormLayout();

        codeField.setLabel("Code");
        codeField.setValueChangeMode(EAGER);
        formLayout.add(codeField);

        nameField.setLabel("Nom");
        nameField.setValueChangeMode(EAGER);
        formLayout.add(nameField);

        wrapper.add(formLayout);

        createFormValidation();
    }

    private void createFormValidation() {
        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        // required fields
        codeField.setRequiredIndicatorVisible(true);
        nameField.setRequiredIndicatorVisible(true);

        //
        binder.forField(codeField)
                .withValidator(new StringLengthValidator("Champ obligatoire", 1, null))
                .bind(Team::getCode, Team::setCode);
        binder.forField(nameField)
                .withValidator(new StringLengthValidator("Champ obligatoire", 1, null))
                .bind(Team::getName, Team::setName);

        binder.readBean(teamBeingEdited);
    }

    private void createButtonLayout(VerticalLayout wrapper) {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        if (teamBeingEdited.getCode() != null) {
            save.setText("Mettre à jour");
        }
        buttonLayout.add(cancel);
        buttonLayout.add(save);

        wrapper.add(buttonLayout);

        cancel.addClickListener(e -> onCancel());
        save.addClickListener(e -> onValidate());
    }

    private void onValidate() {
        if (binder.writeBeanIfValid(teamBeingEdited)) {
            onValidListener.onValid(teamBeingEdited);
            this.close();
        }
    }

    private void onCancel() {
        // clear fields by setting null
        binder.readBean(null);
        this.close();
    }
}
