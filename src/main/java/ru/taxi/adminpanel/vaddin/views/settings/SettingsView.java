package ru.taxi.adminpanel.vaddin.views.settings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.taxi.adminpanel.backend.generator.FakeDataGenerator;
import ru.taxi.adminpanel.backend.generator.GeneratorAccessorService;
import ru.taxi.adminpanel.backend.generator.GeneratorParametersEntity;
import ru.taxi.adminpanel.backend.generator.GeneratorParams;
import ru.taxi.adminpanel.vaddin.views.main.MainView;


@Route(value = "settings", layout = MainView.class)
@PageTitle("Settings")
public class SettingsView extends Div {

    private TextField city = new TextField("City");
    private IntegerField rad = new IntegerField("Area radius");
    private IntegerField ordersNumber = new IntegerField("Orders number");
    private ComboBox<String> language = new ComboBox<>("Lang");

    private Button cancelButton = new Button("Cancel");
    private Button regenerateDataButton = new Button("Regenerate data");

    private Binder<GeneratorParams> binder = new Binder<>(GeneratorParams.class);

    public SettingsView(GeneratorAccessorService generatorAccessorService, FakeDataGenerator fakeDataGenerator) {
        GeneratorParametersEntity generatorParametersEntity = generatorAccessorService.loadParameters();
        setId("settings-view");
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        fillActualParams(generatorParametersEntity);
        cancelButton.addClickListener(e -> fillActualParams(generatorParametersEntity));
        regenerateDataButton.addClickListener(e -> {
            try {
                GeneratorParams generatorParams = GeneratorParams.builder()
                        .city(city.getValue())
                        .ordersNumber(ordersNumber.getValue())
                        .language(language.getValue())
                        .rad(rad.getValue()).build();
                GeneratorParametersEntity updated = generatorAccessorService.updateGeneratorParams(generatorParams);
                fakeDataGenerator.generate(updated);
                Notification.show("Generator settings updated");
            }
            catch (Exception ex) {
                Notification.show(ex.toString());
            }
        });
    }

    private void fillActualParams(GeneratorParametersEntity generatorParameters) {
        if (generatorParameters != null) {
            city.setValue(generatorParameters.getCity());
            rad.setValue(generatorParameters.getRad());
            ordersNumber.setValue(generatorParameters.getOrdersNumber());
            language.setValue(generatorParameters.getLanguage());
        }
    }

    private Component createFormLayout() {
        language.setItems("ru", "en");
        FormLayout formLayout = new FormLayout();
        formLayout.add(city, 2);
        formLayout.add(city, rad, ordersNumber, language);
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
}
