package ru.taxi.adminpanel.vaddin.views.charts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.RangeSelector;
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

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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

    private final H2 addressesCount = new H2();
    private final H2 tripsCount = new H2();
    private final H2 mostPopularArea = new H2();
    private final TripRecordService tripRecordService;
    private final AddressRepository addressRepository;
    private final Chart tripsPerRangeChart = new Chart(ChartType.SPLINE);


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
        tripsPerRangeChart.getConfiguration().setTitle("Trips in range times");
        WrapperCard responseTimesWrapper = new WrapperCard("wrapper", new Component[]{tripsPerRangeChart}, "card");
        board.addRow(gridWrapper);
        board.addRow(responseTimesWrapper);

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

            // Second chart
            tripsPerRangeChart.setTimeline(true);
            Configuration configuration = tripsPerRangeChart.getConfiguration();
            configuration.getTooltip().setEnabled(true);

            DataSeries dataSeries = new DataSeries();
            trips.stream().map(TripRecordEntity::getTripBeginTime).collect(Collectors.groupingBy(time -> {
                int minutes = time.getMinute();
                int minutesOver = minutes % 15;
                return time.truncatedTo(ChronoUnit.MINUTES).withMinute(minutes - minutesOver).toInstant(ZoneOffset.UTC).toEpochMilli();
            }, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((entry) -> {
                DataSeriesItem item = new DataSeriesItem();
                item.setX(entry.getKey());
                item.setY(entry.getValue());
                dataSeries.add(item);
            });

            configuration.addSeries(dataSeries);
            RangeSelector rangeSelector = new RangeSelector();
            rangeSelector.setSelected(1);
            configuration.setRangeSelector(rangeSelector);
        });
    }
}
