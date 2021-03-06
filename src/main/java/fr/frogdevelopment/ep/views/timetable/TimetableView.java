package fr.frogdevelopment.ep.views.timetable;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.grid.ColumnTextAlign.CENTER;
import static com.vaadin.flow.component.grid.ColumnTextAlign.END;
import static com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.format.TextStyle.FULL;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.FRANCE;
import static java.util.stream.Collectors.toList;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import fr.frogdevelopment.ep.implementation.timetables.TimetablesRepository;
import fr.frogdevelopment.ep.model.Timetable;
import fr.frogdevelopment.ep.views.MainView;
import fr.frogdevelopment.ep.views.components.ConfirmDialog;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@PageTitle("Créneaux horaires")
@Route(value = "timetables", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@CssImport("./styles/views/planning/planning-view.css")
@CssImport(value = "./styles/views/planning/grid-cell.css", themeFor = "vaadin-grid")
public class TimetableView extends VerticalLayout implements AfterNavigationObserver {

    private final transient TimetablesRepository timetablesRepository;

    private static final List<DayOfWeek> DAY_OF_WEEKS = List.of(
            SUNDAY,
            MONDAY,
            TUESDAY,
            WEDNESDAY,
            THURSDAY,
            FRIDAY,
            SATURDAY
    );

    private final VerticalLayout title = new VerticalLayout();
    private final HorizontalLayout actionWrapper = new HorizontalLayout();
    private final DatePicker datePicker = new DatePicker();
    private final Grid<Timetable> grid = new Grid<>();

    private ListDataProvider<Timetable> dataProvider = DataProvider.ofCollection(Collections.emptyList());

    public TimetableView(TimetablesRepository timetablesRepository) {
        this.timetablesRepository = timetablesRepository;

        setId("planning-view");

        add(title);

        add(actionWrapper);

        addDatePicker();
    }

    private void addDatePicker() {
        var june = LocalDate.now().withMonth(6);
        datePicker.setMin(june.withDayOfMonth(1));
        datePicker.setMax(june.withDayOfMonth(30));
        datePicker.setPlaceholder("Sélectionnez le Vendredi");
        datePicker.setLocale(FRANCE);
        datePicker.setI18n(new DatePickerI18n()
                .setCalendar("Calendrier")
                .setClear("Clear")
                .setToday("Aujourd'hui")
                .setWeek("Semaine")
                .setCancel("Annuler")
                .setFirstDayOfWeek(1)
                .setMonthNames(Stream.of(Month.values())
                        .map(m -> m.getDisplayName(FULL, FRANCE))
                        .collect(toList()))
                .setWeekdays(DAY_OF_WEEKS.stream()
                        .map(d -> d.getDisplayName(FULL, FRANCE))
                        .collect(toList()))
                .setWeekdaysShort(DAY_OF_WEEKS.stream()
                        .map(d -> d.getDisplayName(SHORT, FRANCE))
                        .collect(toList()))
        );

        actionWrapper.add(datePicker);
    }

    private void addGrid() {
        grid.setId("grid-timetable");
        grid.setSelectionMode(SelectionMode.NONE);
        grid.addThemeVariants(LUMO_NO_BORDER);
        grid.getStyle().set("margin-left", "0px");
        grid.setHeight("75%");
        grid.setClassNameGenerator(item -> item.getDayOfWeek().name().toLowerCase());

        grid.addColumn(item -> item.getDayOfWeek().getDisplayName(FULL, FRANCE))
                .setAutoWidth(true)
                .setTextAlign(CENTER)
                .setHeader("Jour")
                .setFlexGrow(0);
        grid.addColumn(Timetable::getTitle)
                .setAutoWidth(true)
                .setTextAlign(CENTER)
                .setHeader("Horaire")
                .setFlexGrow(0);
        grid.addColumn(Timetable::getDuration)
                .setAutoWidth(true)
                .setTextAlign(CENTER)
                .setHeader("Durée")
                .setFlexGrow(0)
                .setClassNameGenerator(item -> "right-border");
        var braceletExpected = grid.addColumn(Timetable::getExpectedBracelet)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Voulu")
                .setFlexGrow(0);
        var braceletActual = grid.addColumn(Timetable::getActualBracelet)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Réel")
                .setFlexGrow(0)
                .setClassNameGenerator(
                        item -> getEffectifClassName(item.getActualBracelet(), item.getExpectedBracelet()));
        var fouillesExpected = grid.addColumn(Timetable::getExpectedFouille)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Voulu")
                .setFlexGrow(0);
        var fouillesActual = grid.addColumn(Timetable::getActualFouille)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Réel")
                .setFlexGrow(0)
                .setClassNameGenerator(
                        item -> getEffectifClassName(item.getActualFouille(), item.getExpectedFouille()));
        var litigesExpected = grid.addColumn(Timetable::getExpectedLitiges)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Voulu")
                .setFlexGrow(0);
        var litigesActual = grid.addColumn(Timetable::getActualLitiges)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Réel")
                .setFlexGrow(0)
                .setClassNameGenerator(
                        item -> getEffectifClassName(item.getActualLitiges(), item.getExpectedLitiges()));
        var totalExpected = grid.addColumn(Timetable::getExpectedTotal)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Voulu")
                .setFlexGrow(0);
        var totalActual = grid.addColumn(Timetable::getActualTotal)
                .setAutoWidth(true)
                .setTextAlign(END)
                .setHeader("Réel")
                .setFlexGrow(0)
                .setClassNameGenerator(item -> getEffectifClassName(item.getActualTotal(), item.getExpectedTotal()));
        grid.addColumn(Timetable::getDescription)
                .setHeader("Description")
                .setFlexGrow(1);

        var headerRow = grid.prependHeaderRow();
        headerRow.join(braceletExpected, braceletActual).setComponent(getHeaderTitle("Bracelets"));
        headerRow.join(fouillesExpected, fouillesActual).setComponent(getHeaderTitle("Fouilles"));
        headerRow.join(litigesExpected, litigesActual).setComponent(getHeaderTitle("Litiges"));
        headerRow.join(totalExpected, totalActual).setComponent(getHeaderTitle("Total"));

        dataProvider = DataProvider.ofCollection(timetablesRepository.getPlanning());
        grid.setDataProvider(dataProvider);

        GridContextMenu<Timetable> contextMenu = new GridContextMenu<>(grid);
        var edit = new HorizontalLayout(VaadinIcon.EDIT.create(), new Label("Modifier"));
        contextMenu.addItem(edit, event -> event.getItem().ifPresentOrElse(this::onEdit, this::smallError));
        var delete = new HorizontalLayout(VaadinIcon.TRASH.create(), new Label("Supprimer"));
        contextMenu.addItem(delete, event -> event.getItem().ifPresentOrElse(this::onDelete, this::smallError));

        add(grid);
    }

    private void smallError() {
        Notification.show("Petit problème ! ", 5000, Position.TOP_CENTER);
    }

    private void onEdit(Timetable timetable) {
        new TimetableDialog(timetable, toUpdate -> {
            timetablesRepository.updateTimetable(toUpdate);
            Notification.show("Créneau mis à jour", 5000, Position.TOP_CENTER);
            dataProvider.refreshItem(toUpdate);
            dataProvider.refreshAll();
        }).open();
    }

    private void onDelete(Timetable timetable) {
        ConfirmDialog.builder()
                .message("Supprimer le créneau ?")
                .confirmButton("Supprimer", () -> {
                    timetablesRepository.delete(timetable);
                    Notification.show("Créneau supprimé", 5000, Position.TOP_CENTER);
                    dataProvider.getItems().remove(timetable);
                    dataProvider.refreshAll();
                })
                .open();
    }

    private void onAddTimetable() {
        var dialog = new TimetableDialog(updatedTimetable -> {
            timetablesRepository.insertTimetable(updatedTimetable);
            grid.setItems(timetablesRepository.getPlanning());
        });
        dialog.open();
    }

    private Component getHeaderTitle(String title) {
        Div header = new Div(new Span(title));
        header.getStyle().set("text-align", "center");
        header.setSizeFull();
        return header;
    }

    private String getEffectifClassName(int actual, int expected) {
        if (actual < expected) {
            return "right-border actual-ko";
        } else {
            return "right-border actual-ok";
        }
    }

    private void setTitle(LocalDate edition) {
        var h1 = new H1("Solidays - Edition " + edition.getYear());
        h1.getStyle().set("margin-top", "0px");
        h1.getStyle().set("margin-bottom", "0px");
        title.add(h1);

        var formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM").localizedBy(FRANCE);
        var h2 = new H2(String.join(", ",
                edition.format(formatter),
                edition.plusDays(1).format(formatter),
                edition.plusDays(2).format(formatter)));
        h2.getStyle().set("margin-top", "0px");
        h2.getStyle().set("margin-bottom", "0px");
        title.add(h2);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent e) {
        var edition = timetablesRepository.getEdition();
        datePicker.setValue(edition);
        datePicker.addValueChangeListener(event -> {
            var localDate = event.getValue();
            if (localDate != null) {
                if (localDate.getDayOfWeek().equals(FRIDAY)) {
                    timetablesRepository.setEdition(localDate);
                    getUI().ifPresent(ui -> ui.getPage().reload());
                } else {
                    datePicker.setErrorMessage("Veuillez sélectionner un vendredi");
                    datePicker.setInvalid(true);
                }
            }
        });

        if (edition != null) {
            setTitle(edition);
            addGrid();

            var newButton = new Button("Ajouter un créneau", VaadinIcon.PLUS_CIRCLE.create());
            newButton.addThemeVariants(LUMO_PRIMARY);
            newButton.addClickListener(event -> onAddTimetable());
            actionWrapper.add(newButton);
        } else {
            title.add(new H4("Précisez la date de l'édition"));
        }
    }

}
