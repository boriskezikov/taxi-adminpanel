package ru.taxi.adminpanel.vaddin.views.map;


import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.google.maps.model.LatLng;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordRepository;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Route(value = "googlemap", layout = MainView.class)
@PageTitle("Map")
public class MapView extends VerticalLayout {

    private final GoogleMap gmaps;
    private final Button loadPointsButton = new Button("Load order points");
    private final Button cleanPointsButton = new Button("Clean map");
    private final DateTimePicker tripDateTimePicker;
    private static final String ICON_URL = "https://www.flowingcode.com/wp-content/uploads/2020/06/FCMarker.png";
    private final TripRecordRepository tripRecordRepository;
    protected String xGoogleApiKey = "AIzaSyB8V8hR1JKIG9L8ojccE5cOfMD4hS3seoA";

    public MapView(TripRecordRepository tripRecordRepository) {
        this.gmaps = new GoogleMap(xGoogleApiKey, null, null);
        this.tripDateTimePicker = new DateTimePicker();
        this.tripRecordRepository = tripRecordRepository;
        configure();
        add(createDateTimePicker());
        add(createButtonLayout());
        add(gmaps);
    }

    private void configure() {
        setSizeFull();
        setPadding(true);
        configureButton();
        configureMap();
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        loadPointsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(loadPointsButton);
        buttonLayout.add(cleanPointsButton);
        return buttonLayout;
    }

    private void configureButton() {
        loadPointsButton.addClickListener(e -> {
            List<TripRecordEntity> all = tripRecordRepository.findAll();
            LocalDateTime filterTime = tripDateTimePicker.getValue();
            all.stream()
                    .filter(t -> Duration.between(filterTime, t.getTripBeginTime()).get(ChronoUnit.SECONDS) > 86400)
                    .forEach(tripRecordEntity -> {
                        AddressEntity from = tripRecordEntity.getFromAddressEntity();
                        LatLon latLon = new LatLon(from.getGeometry().lat, from.getGeometry().lng);
                        gmaps.addMarker(from.getFormattedAddress(), latLon, true, ICON_URL);
                        log.info("Point [lat:{}, lng:{}] added to map", from.getGeometry().lat, from.getGeometry().lng);
                    });
        });
    }

    private void configureMap() {
        gmaps.setMapType(GoogleMap.MapType.ROADMAP);
        gmaps.setSizeFull();
        gmaps.setCenter(findCenter());
        gmaps.setZoom(15);
        gmaps.addRightClickListener(event -> {
            Notification.show("lat:" + event.getLatitude() + ", lng:" + event.getLongitude(),
                    5000, Notification.Position.MIDDLE);
        });
        gmaps.addClickListener(event -> {
            gmaps.addMarker("Point", new LatLon(event.getLatitude(), event.getLongitude()), true, ICON_URL);
        });
        gmaps.onEnabledStateChanged(true);
    }

    private LatLon findCenter() {
        Optional<TripRecordEntity> tripRecordEntityOpt = tripRecordRepository.findAll().stream().findAny();
        if (tripRecordEntityOpt.isPresent()) {
            LatLng geometry = tripRecordEntityOpt.get().getFromAddressEntity().getGeometry();
            return new LatLon(geometry.lat, geometry.lng);
        }
        return new LatLon(0, 0);
    }

    private DateTimePicker createDateTimePicker() {
        tripDateTimePicker.setDatePlaceholder("Date");
        tripDateTimePicker.setTimePlaceholder("Time");
        tripDateTimePicker.setStep(Duration.of(600, ChronoUnit.SECONDS));
        return tripDateTimePicker;
    }
}