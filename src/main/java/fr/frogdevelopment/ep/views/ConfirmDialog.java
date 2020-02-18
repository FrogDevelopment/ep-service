package fr.frogdevelopment.ep.views;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NoArgsConstructor;

public class ConfirmDialog extends Dialog {

    private final VerticalLayout wrapper;
    private final Button cancel = new Button("Annuler");
    private final Button confirm = new Button("Continuer");
    private final transient OnConfirmClickedListener onConfirmClickedListener;

    private ConfirmDialog(String title,
                          String message,
                          String okButton,
                          OnConfirmClickedListener onConfirmClickedListener) {
        super();

        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);

        this.onConfirmClickedListener = onConfirmClickedListener;

        wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);

        if (isNotBlank(title)) {
            createTitle(title);
        }
        if (isNotBlank(message)) {
            createMessage(message);
        }
        createButtonLayout(okButton);

        add(wrapper);
    }

    private void createTitle(String title) {
        wrapper.add(new H4(title));
    }

    private void createMessage(String message) {
        wrapper.add(new Label(message));
    }

    private void createButtonLayout(String okButton) {
        var buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(END);

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        if (isNotBlank(okButton)) {
            confirm.setText(okButton);
        }

        buttonLayout.add(cancel);
        buttonLayout.add(confirm);

        wrapper.add(buttonLayout);

        cancel.addClickListener(e -> onCancel());
        confirm.addClickListener(e -> onConfirm());
    }

    private void onCancel() {
        close();
    }

    private void onConfirm() {
        onConfirmClickedListener.onConfirmClicked();
        close();
    }

    public static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor
    public static class Builder {

        private String title = null;
        private String message = null;
        private String confirmButton = null;
        private OnConfirmClickedListener onConfirmClickedListener;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder confirmButton(String confirmButton, OnConfirmClickedListener onConfirmClickedListener) {
            this.confirmButton = confirmButton;
            this.onConfirmClickedListener = onConfirmClickedListener;
            return this;
        }

        public ConfirmDialog build() {
            return new ConfirmDialog(title, message, confirmButton, onConfirmClickedListener);
        }

        public void open() {
            build().open();
        }
    }

    @FunctionalInterface
    public interface OnConfirmClickedListener {

        void onConfirmClicked();
    }
}
