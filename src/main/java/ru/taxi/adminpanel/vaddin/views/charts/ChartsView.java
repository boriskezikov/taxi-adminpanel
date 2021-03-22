package ru.taxi.adminpanel.vaddin.views.charts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.address.AddressRepository;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordService;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@CssImport("./styles/views/charts/charts-view.css")
@Route(value = "charts", layout = MainView.class)
@PageTitle("Chartboard")
@PreserveOnRefresh
public class ChartsView extends Div {
    private final Grid<String> grid = new Grid<>();

    private final Chart responseTimes = new Chart();
    private final H2 addressesCount = new H2();
    private final H2 tripsCount = new H2();
    private final H2 mostPopularArea = new H2();
    private final TripRecordService tripRecordService;
    private final AddressRepository addressRepository;


    public ChartsView(TripRecordService tripRecordService, AddressRepository addressRepository) {
        this.tripRecordService = tripRecordService;
        this.addressRepository = addressRepository;
        addClassName("charts-view");
        Board board = new Board();
        board.addRow(createBadge("Trips", tripsCount, "primary-text", "Trips in the current area", "badge"),
                createBadge("Area", mostPopularArea, "success-text", "Most popular area", "badge success"),
                createBadge("Addresses", addressesCount, "success-text", "Addresses found for this region", "badge success")
        );
        grid.addColumn(String::valueOf).setHeader("City");
        grid.addColumn(new ComponentRenderer<>(item -> {
            Span span = new Span("Connected");
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Status").setFlexGrow(0).setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        WrapperCard gridWrapper = new WrapperCard("wrapper", new Component[]{new H3("Cities around main"), grid}, "card");
        responseTimes.getConfiguration().setTitle("Response times");
        WrapperCard responseTimesWrapper = new WrapperCard("wrapper", new Component[]{responseTimes}, "card");
        board.addRow(gridWrapper, responseTimesWrapper);

        add(board);

        populateCharts();
    }

    private WrapperCard createBadge(String title, H2 h2, String h2ClassName, String description, String badgeTheme) {
        Span titleSpan = new Span(title);
        titleSpan.getElement().setAttribute("theme", badgeTheme);

        h2.addClassName(h2ClassName);

        Span descriptionSpan = new Span(description);
        descriptionSpan.addClassName("secondary-text");

        return new WrapperCard("wrapper", new Component[]{titleSpan, h2, descriptionSpan}, "card", "space-m");
    }

    private void populateCharts() {
        CompletableFuture<List<TripRecordEntity>> tripsFuture = tripRecordService.findAll();
        tripsFuture.whenComplete((trips, throwable) -> {
            if (throwable != null) {
                throw new RuntimeException(throwable);
            }
            Set<String> addresses = trips.stream().map(TripRecordEntity::getFromAddressEntity)
                    .map(AddressEntity::getCity)
                    .collect(Collectors.toSet());
            tripsCount.setText(String.valueOf(trips.size()));
            grid.setItems(addresses);

            var areas = trips.parallelStream().map(t -> t.getFromAddressEntity().getZipCode()).collect(Collectors.groupingBy(s -> Objects.requireNonNullElse(s, "NO ZIPCODE"), Collectors.counting()));
            String mostPopularZip = Collections.max(areas.entrySet(), Map.Entry.comparingByValue()).getKey();
            mostPopularArea.setText(String.valueOf(mostPopularZip));

            long addressesGenerated = addressRepository.count();
            addressesCount.setText(String.valueOf(addressesGenerated));
        });

        // Second chart
        Configuration configuration = responseTimes.getConfiguration();
        configuration
                .addSeries(new ListSeries("Time", 7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6));

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00");
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        configuration.addyAxis(y);
    }
}
