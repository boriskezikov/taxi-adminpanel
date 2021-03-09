package ru.taxi.adminpanel.vaddin.views.settings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.taxi.adminpanel.backend.generator.ArtificialDataGenerator;
import ru.taxi.adminpanel.backend.generator.GeneratorAccessorService;
import ru.taxi.adminpanel.backend.generator.GeneratorParametersEntity;
import ru.taxi.adminpanel.backend.generator.GeneratorParams;
import ru.taxi.adminpanel.vaddin.views.main.MainView;


@CssImport("./styles/views/settings/settings-view.css")
@Route(value = "settings", layout = MainView.class)
@PageTitle("Settings")
public class SettingsView extends Div {

    private final TextField city = new TextField("City");
    private final IntegerField rad = new IntegerField("Area radius");
    private final IntegerField ordersNumber = new IntegerField("Orders number");
    private final ComboBox<String> nnServiceUrl = new ComboBox<>("NN url");
    private final ComboBox<String> language = new ComboBox<>("Lang");

    private final Button cancelButton = new Button("Cancel");
    private final Button regenerateDataButton = new Button("Regenerate data");
    private final Checkbox removeData = new Checkbox("Remove previously generated");

    public SettingsView(GeneratorAccessorService generatorAccessorService, ArtificialDataGenerator artificialDataGenerator) {
        GeneratorParametersEntity generatorParametersEntity = generatorAccessorService.loadParameters();
        addClassName("settings-view");
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
        configureComponents();
        fillActualParams(generatorParametersEntity);
        cancelButton.addClickListener(e -> fillActualParams(generatorParametersEntity));
        regenerateDataButton.addClickListener(e -> {
            try {
                GeneratorParams generatorParams = GeneratorParams.builder()
                        .city(city.getValue())
                        .ordersNumber(ordersNumber.getValue())
                        .language(language.getValue())
                        .predictorUrl(nnServiceUrl.getValue())
                        .removePreviouslyGenerated(removeData.getValue())
                        .rad(rad.getValue()).build();
                GeneratorParametersEntity updated = generatorAccessorService.updateGeneratorParams(generatorParams);
                artificialDataGenerator.generateAsync(updated);
                Notification.show("Generator settings updated", 1000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show(ex.toString(), 1000, Notification.Position.MIDDLE);
            }
        });
    }

    private void fillActualParams(GeneratorParametersEntity generatorParameters) {
        if (generatorParameters != null) {
            city.setValue(generatorParameters.getCity());
            rad.setValue(generatorParameters.getRad());
            ordersNumber.setValue(generatorParameters.getOrdersNumber());
            language.setValue(generatorParameters.getLanguage());
            nnServiceUrl.setValue(generatorParameters.getPredictorUrl());
            removeData.setValue(generatorParameters.isClean());
        }
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(nnServiceUrl, 2);
        formLayout.add(city, rad, ordersNumber, language, removeData);
        return formLayout;
    }


    private Component createTitle() {
        return new H3("Settings");
    }


    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        regenerateDataButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(regenerateDataButton);
        buttonLayout.add(cancelButton);
        return buttonLayout;
    }

    private void configureComponents() {
        language.setItems("ru", "en");
        language.setAllowCustomValue(false);
        nnServiceUrl.setItems("https://nnpredictor-v1.herokuapp.com", "https://nnpredictor-v2.herokuapp.com");
        nnServiceUrl.setAllowCustomValue(false);
        nnServiceUrl.setRequired(true);
        language.setRequired(true);
        language.setPreventInvalidInput(true);
        city.setRequired(true);
        rad.setRequiredIndicatorVisible(true);
        ordersNumber.setRequiredIndicatorVisible(true);
    }
}
