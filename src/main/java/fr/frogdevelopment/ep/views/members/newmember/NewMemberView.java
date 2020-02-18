package fr.frogdevelopment.ep.views.members.newmember;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import fr.frogdevelopment.ep.domain.Member;

@CssImport("./styles/views/members/newmember/new-member-view.css")
public class NewMemberView extends Dialog {

    @FunctionalInterface
    public interface OnMemberValidListener {

        void onMemberValid(Member member);
    }

    private final OnMemberValidListener onMemberValidListener;

    // The object that will be edited
    private final Member memberBeingEdited = new Member();

    private final Binder<Member> binder = new Binder<>(Member.class);

    private final TextField lastName = new TextField();
    private final TextField firstName = new TextField();
    private final TextField email = new TextField();
    private final TextField phoneNumber = new TextField();
    private final Checkbox referent = new Checkbox("Référent d'équipe");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    public NewMemberView(OnMemberValidListener onMemberValidListener) {
        this.onMemberValidListener = onMemberValidListener;

        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);

        setId("new-member-view");
        var wrapper = createWrapper();

        createTitle(wrapper);
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

    private void createTitle(VerticalLayout wrapper) {
        var h1 = new H1("Ajouter un bénévole");
        wrapper.add(h1);
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

        formLayout.add(referent);

        wrapper.add(formLayout);

        createFormValidation();
    }

    private void createFormValidation() {
        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

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
