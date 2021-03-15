package ru.taxi.adminpanel.vaddin.views.map;


import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.google.maps.model.LatLng;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordService;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Route(value = "googlemap", layout = MainView.class)
@PageTitle("Map")
@PreserveOnRefresh
public class MapView extends VerticalLayout {

    private final GoogleMap gmaps;
    private final Button loadPointsButton = new Button("Load order points");
    private final Button cleanPointsButton = new Button("Clean map");
    private final DateTimePicker tripDateTimePicker;
    private static final String ICON_URL = "https://www.flowingcode.com/wp-content/uploads/2020/06/FCMarker.png";
    private final TripRecordService tripRecordService;
    protected String xGoogleApiKey = "AIzaSyB8V8hR1JKIG9L8ojccE5cOfMD4hS3seoA";
    private final IntegerField tripsLimit = new IntegerField("Trips limit");

    private static final int DEFAULT_TRIPS_LIMIT = 100;

    public MapView(TripRecordService tripRecordService) {
        this.gmaps = new GoogleMap(xGoogleApiKey, null, null);
        this.tripDateTimePicker = new DateTimePicker();
        this.tripRecordService = tripRecordService;
        configure();
        add(createFilterLayout());
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
            gmaps.getChildren().forEach(ch -> gmaps.removeMarker((GoogleMapMarker) ch));
            Optional<LocalDateTime> value = Optional.ofNullable(tripDateTimePicker.getValue());
            Page<TripRecordEntity> trips;
            int tripsLimitCount = tripsLimit.getValue() == null ? DEFAULT_TRIPS_LIMIT : tripsLimit.getValue();
            Pageable pageable = PageRequest.of(0, tripsLimitCount);

            if (value.isPresent()) {
                trips = tripRecordService.findInRange(value.get(), value.get().plusSeconds(86400), pageable);
            } else {
                trips = tripRecordService.findAll(pageable);
            }
            trips.stream().parallel()
                    .forEach(tripRecordEntity -> {
                        AddressEntity from = tripRecordEntity.getFromAddressEntity();
                        LatLon latLon = new LatLon(from.getGeometry().lat, from.getGeometry().lng);
                        gmaps.addMarker(from.getFormattedAddress(), latLon, true, ICON_URL);
                        log.info("Point [lat:{}, lng:{}] added to map", from.getGeometry().lat, from.getGeometry().lng);
                    });
        });

        cleanPointsButton.addClickListener(e -> gmaps.getChildren()
                .forEach(ch -> gmaps.removeMarker((GoogleMapMarker) ch)));


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
        var tripRecordEntityOpt = tripRecordService.findById(BigInteger.ONE);
        if (tripRecordEntityOpt.isPresent()) {
            LatLng geometry = tripRecordEntityOpt.get().getFromAddressEntity().getGeometry();
            return new LatLon(geometry.lat, geometry.lng);
        }
        return new LatLon(0, 0);
    }

    private Component createFilterLayout() {
        HorizontalLayout hl = new HorizontalLayout();
        tripDateTimePicker.setDatePlaceholder("Date");
        tripDateTimePicker.setTimePlaceholder("Time");
        tripDateTimePicker.setStep(Duration.of(600, ChronoUnit.SECONDS));

        tripsLimit.setValue(DEFAULT_TRIPS_LIMIT);

        hl.add(tripDateTimePicker);
        hl.add(tripsLimit);
        return hl;
    }
}