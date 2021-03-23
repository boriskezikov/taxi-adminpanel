package ru.taxi.adminpanel.vaddin.views.map;


import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Route(value = "googlemap", layout = MainView.class)
@PageTitle("Map")
@PreserveOnRefresh
public class MapView extends VerticalLayout {

    private final GoogleMap gmaps;
    private final Button loadPointsButton = new Button("Load order points");
    private final Button cleanPointsButton = new Button("Clean map");
    private final DatePicker datePicker = new DatePicker("Day");
    private final TimePicker timePickerFrom = new TimePicker("From time");
    private final TimePicker timePickerTo = new TimePicker("To time");
    private static final String ICON_URL = "https://www.flowingcode.com/wp-content/uploads/2020/06/FCMarker.png";
    private final TripRecordService tripRecordService;
    protected String xGoogleApiKey = "AIzaSyB8V8hR1JKIG9L8ojccE5cOfMD4hS3seoA";
    private final IntegerField tripsLimit = new IntegerField("Points number limit");

    private static final int DEFAULT_TRIPS_LIMIT = 100;

    public MapView(TripRecordService tripRecordService) {
        this.gmaps = new GoogleMap(xGoogleApiKey, null, null);
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
            if (datePicker.getValue() == null) {
                Notification.show(datePicker.getErrorMessage(), 2000, Notification.Position.MIDDLE);
                return;
            }
            gmaps.getChildren().forEach(ch -> gmaps.removeMarker((GoogleMapMarker) ch));
            Optional<LocalTime> timePickerFromValue = Optional.ofNullable(timePickerFrom.getValue());
            Optional<LocalTime> timePickerToValue = Optional.ofNullable(timePickerTo.getValue());
            int tripsLimitCount = tripsLimit.getValue() == null ? DEFAULT_TRIPS_LIMIT : tripsLimit.getValue();
            Pageable pageable = PageRequest.of(0, tripsLimitCount);

            LocalTime timeFrom = timePickerFromValue.orElse(LocalTime.MIN);
            LocalTime timeTo = timePickerToValue.orElse(LocalTime.MAX);

            LocalDateTime searchDateTimeFrom = LocalDateTime.of(datePicker.getValue(), timeFrom);
            LocalDateTime searchDateTimeTo = LocalDateTime.of(datePicker.getValue(), timeTo);
            Page<TripRecordEntity> rangedTrips = tripRecordService.findInRange(searchDateTimeFrom, searchDateTimeTo, pageable);
            rangedTrips.stream().parallel()
                    .forEach(tripRecordEntity -> {
                        AddressEntity from = tripRecordEntity.getFromAddressEntity();
                        gmaps.addMarker(from.getFormattedAddress(), from.getGeometry(), true, ICON_URL);
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
        var tripRecordEntityOpt = tripRecordService.findById(BigInteger.valueOf(1000L));
        if (tripRecordEntityOpt.isPresent()) {
            return tripRecordEntityOpt.get().getFromAddressEntity().getGeometry();
        }
        return new LatLon(0, 0);
    }

    private Component createFilterLayout() {
        HorizontalLayout hl = new HorizontalLayout();
        datePicker.setRequired(true);
        datePicker.setErrorMessage("Date field is required for filling");

        tripsLimit.setValue(DEFAULT_TRIPS_LIMIT);

        hl.add(datePicker, timePickerFrom, timePickerTo);
        hl.add(tripsLimit);
        return hl;
    }
}