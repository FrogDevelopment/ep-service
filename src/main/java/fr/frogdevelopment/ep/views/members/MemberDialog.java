package fr.frogdevelopment.ep.views.members;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.ValueProvider;
import fr.frogdevelopment.ep.model.Member;
import fr.frogdevelopment.ep.model.Team;
import java.util.List;

@CssImport("./styles/views/members/member-dialog.css")
public class MemberDialog extends Dialog {

    private final List<Team> teamValues;

    @FunctionalInterface
    public interface OnMemberValidListener {

        void onMemberValid(Member member);
    }

    private final transient OnMemberValidListener onMemberValidListener;

    // The object that will be edited
    private final Member memberBeingEdited;

    private final Binder<Member> binder = new Binder<>(Member.class);

    private final TextField lastName = new TextField();
    private final TextField firstName = new TextField();
    private final TextField email = new TextField();
    private final TextField phoneNumber = new TextField();
    private final ComboBox<Team> teams = new ComboBox<>();
    private final Checkbox referent = new Checkbox();

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    public MemberDialog(List<Team> teamValues, OnMemberValidListener onMemberValidListener) {
        this(null, teamValues, onMemberValidListener);
    }

    public MemberDialog(Member member, List<Team> teamValues, OnMemberValidListener onMemberValidListener) {
        super();
        this.teamValues = teamValues;
        this.onMemberValidListener = onMemberValidListener;
        memberBeingEdited = member == null ? new Member() : member;

        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);

        setId("new-member-view");
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

        lastName.setLabel("Nom");
        lastName.setValueChangeMode(EAGER);
        formLayout.add(lastName);

        firstName.setLabel("Prénom");
        firstName.setValueChangeMode(EAGER);
        formLayout.add(firstName);

        email.setLabel("Email");
        email.setValueChangeMode(EAGER);
        formLayout.add(email);

        phoneNumber.setLabel("Téléphone");
        phoneNumber.setValueChangeMode(EAGER);
        formLayout.add(phoneNumber);

        teams.setLabel("Équipe");
        teams.setItems(teamValues);
        teams.setClearButtonVisible(true);
        teams.setItemLabelGenerator(Team::getFullName);
        formLayout.add(teams);

        referent.setLabel("Référent d'équipe");
        formLayout.add(referent);

        wrapper.add(formLayout);

        createFormValidation();
    }

    private void createFormValidation() {
        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        binder.bind(teams,
                (ValueProvider<Member, Team>) member -> teamValues
                        .stream()
                        .filter(t -> t.getCode().equals(member.getTeamCode()))
                        .findFirst()
                        .orElse(null),
                (Setter<Member, Team>) (member, team) -> member.setTeamCode(team.getCode()));
        binder.readBean(memberBeingEdited);

        // First name and last name are required fields
        lastName.setRequiredIndicatorVisible(true);
        firstName.setRequiredIndicatorVisible(true);

        binder.forField(lastName)
                .withValidator(new StringLengthValidator("Champ obligatoire", 1, null))
                .bind(Member::getLastName, Member::setLastName);
        binder.forField(firstName)
                .withValidator(new StringLengthValidator("Champ obligatoire", 1, null))
                .bind(Member::getFirstName, Member::setFirstName);

        // E-mail and phone have specific validators
        var emailBinding = binder.forField(email)
                .withValidator(new EmailValidator("Adresse email incorrecte."))
                .bind(Member::getEmail, Member::setEmail);
        var phoneBinding = binder.forField(phoneNumber)
                .withValidator(new RegexpValidator("Chiffres et espaces uniquement", "^(\\d+|(\\d{2}\\s){4}\\d{2})$"))
                .bind(Member::getPhoneNumber, Member::setPhoneNumber);

        // Trigger cross-field validation when the other field is changed
        email.addValueChangeListener(event -> phoneBinding.validate());
        phoneNumber.addValueChangeListener(event -> emailBinding.validate());
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
        if (binder.writeBeanIfValid(memberBeingEdited)) {
            onMemberValidListener.onMemberValid(memberBeingEdited);
            this.close();
        }
    }

    private void onCancel() {
        // clear fields by setting null
        binder.readBean(null);
        this.close();
    }
}
