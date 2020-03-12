package fr.frogdevelopment.ep.views.timetable;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.format.TextStyle.FULL;
import static java.util.Locale.FRANCE;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import fr.frogdevelopment.ep.model.Timetable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;

@CssImport("./styles/views/volunteers/volunteer-dialog.css")
public class TimetableDialog extends Dialog {

    private final List<DayOfWeek> dayOfWeeks = List.of(FRIDAY, SATURDAY, SUNDAY);

    @FunctionalInterface
    public interface OnValidListener {

        void onValid(Timetable timetable);
    }

    @FunctionalInterface
    public interface OnDeleteListener {

        void onDelete(Timetable timetable);
    }

    private final transient OnValidListener onValidListener;
    private final transient OnDeleteListener onDeleteListener;

    // The object that will be edited
    private final Timetable timetableBeingEdited;

    private final Binder<Timetable> binder = new Binder<>(Timetable.class);

    private final ComboBox<DayOfWeek> dayOfWeekComboBox = new ComboBox<>();
    private final TimePicker startTimePicker = new TimePicker();
    private final TimePicker endTimePicker = new TimePicker();
    private final IntegerField expectedBracelets = new IntegerField();
    private final IntegerField expectedFouilles = new IntegerField();
    private final IntegerField expectedLitiges = new IntegerField();
    private final TextArea description = new TextArea();

    private final Button delete = new Button("Supprimer");
    private final Button cancel = new Button("Annuler");
    private final Button save = new Button("Sauvegarder");

    public TimetableDialog(OnValidListener onValidListener) {
        this(null, onValidListener, null);
    }

    public TimetableDialog(Timetable timetable, OnValidListener onValidListener, OnDeleteListener onDeleteListener) {
        super();
        this.onValidListener = onValidListener;
        this.onDeleteListener = onDeleteListener;
        timetableBeingEdited = timetable == null ? new Timetable() : timetable;

        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);

        setId("new-planning-view");
        var wrapper = createWrapper();

        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        add(wrapper);

        this.setWidth("400px");
    }

    private VerticalLayout createWrapper() {
        var wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        var formLayout = new FormLayout();

        dayOfWeekComboBox.setItems(dayOfWeeks);
        dayOfWeekComboBox.setItemLabelGenerator(item -> item.getDisplayName(FULL, FRANCE));
        formLayout.addFormItem(dayOfWeekComboBox, "Jour");

        startTimePicker.setLocale(FRANCE);
        startTimePicker.setStep(Duration.ofMinutes(15));
        formLayout.addFormItem(startTimePicker, "Début");

        endTimePicker.setLocale(FRANCE);
        endTimePicker.setStep(Duration.ofMinutes(15));
        formLayout.addFormItem(endTimePicker, "Fin");

        expectedBracelets.setMin(0);
        expectedBracelets.setHasControls(true);
        expectedBracelets.setValueChangeMode(EAGER);
        formLayout.addFormItem(expectedBracelets, "Bracelets");

        expectedFouilles.setMin(0);
        expectedFouilles.setHasControls(true);
        expectedFouilles.setValueChangeMode(EAGER);
        formLayout.addFormItem(expectedFouilles, "Fouilles");

        expectedLitiges.setMin(0);
        expectedLitiges.setHasControls(true);
        expectedLitiges.setValueChangeMode(EAGER);
        formLayout.addFormItem(expectedLitiges, "Litiges");

        formLayout.addFormItem(description, "Description");

        wrapper.add(formLayout);

        createFormValidation();
    }

    private void createFormValidation() {
        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        // required fields
        dayOfWeekComboBox.setRequiredIndicatorVisible(true);
        startTimePicker.setRequiredIndicatorVisible(true);
        endTimePicker.setRequiredIndicatorVisible(true);
        expectedBracelets.setRequiredIndicatorVisible(true);
        expectedFouilles.setRequiredIndicatorVisible(true);
        expectedLitiges.setRequiredIndicatorVisible(true);

        //
        binder.forField(dayOfWeekComboBox)
                .bind(
                        timetable -> dayOfWeeks
                                .stream()
                                .filter(d -> d.equals(timetable.getDayOfWeek()))
                                .findFirst()
                                .orElse(null),
                        Timetable::setDayOfWeek);

        binder.forField(startTimePicker)
                .bind(Timetable::getStartTime, Timetable::setStartTime);
        binder.forField(endTimePicker)
                .bind(Timetable::getEndTime, Timetable::setEndTime);
        binder.forField(expectedBracelets)
                .bind(Timetable::getExpectedBracelet, Timetable::setExpectedBracelet);
        binder.forField(expectedFouilles)
                .bind(Timetable::getExpectedFouille, Timetable::setExpectedFouille);
        binder.forField(expectedLitiges)
                .bind(Timetable::getExpectedLitiges, Timetable::setExpectedLitiges);

        binder.readBean(timetableBeingEdited);
    }

    private void createButtonLayout(VerticalLayout wrapper) {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        if (timetableBeingEdited != null) {
            buttonLayout.add(delete);
            save.setText("Mettre à jour");
        }
        buttonLayout.add(cancel);
        buttonLayout.add(save);

        wrapper.add(buttonLayout);

        delete.addClickListener(e -> onDelete());
        cancel.addClickListener(e -> onCancel());
        save.addClickListener(e -> onValidate());
    }

    private void onValidate() {
        if (binder.writeBeanIfValid(timetableBeingEdited)) {
            onValidListener.onValid(timetableBeingEdited);
            this.close();
        }
    }

    private void onCancel() {
        // clear fields by setting null
        binder.readBean(null);
        this.close();
    }

    private void onDelete() {
        onDeleteListener.onDelete(timetableBeingEdited);
        binder.readBean(null);
        this.close();
    }
}
