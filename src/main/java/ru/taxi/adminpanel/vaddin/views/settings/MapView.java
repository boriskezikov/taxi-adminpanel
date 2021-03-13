package ru.taxi.adminpanel.vaddin.views.settings;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordRepository;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "googlemap", layout = MainView.class)
@PageTitle("Map")
public class MapView extends VerticalLayout {

    private LeafletMap map = new LeafletMap();
    private final Button loadPointsButton = new Button("Load Points");

    public MapView(TripRecordRepository tripRecordRepository) {
        setSizeFull();
        setPadding(true);
        map.setSizeFull();
        map.setView(53.238920640925, 34.35287475585938, 20);
        add(map);
        add(createButtonLayout());
        loadPointsButton.addClickListener(e -> {
            List<TripRecordEntity> all = tripRecordRepository.findAll();
            MapDto[] objects = all.stream().map(trip -> {
                AddressEntity fromAddressEntity = trip.getFromAddressEntity();
                double lat = Double.parseDouble(fromAddressEntity.getLat());
                double lng = Double.parseDouble(fromAddressEntity.getLng());
                return new MapDto(lat, lng, fromAddressEntity.getFormattedAddress());
            }).toArray(MapDto[]::new);
            map.setPoints(objects);
        });
    }
    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        loadPointsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(loadPointsButton);
        return buttonLayout;
    }

}