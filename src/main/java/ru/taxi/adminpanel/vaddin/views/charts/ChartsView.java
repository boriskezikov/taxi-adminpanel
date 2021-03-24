package ru.taxi.adminpanel.vaddin.views.charts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.router.RouterLayout;
import lombok.extern.slf4j.Slf4j;
import ru.taxi.adminpanel.backend.background.statistics.GeneralStatisticsEntity;
import ru.taxi.adminpanel.backend.background.statistics.StatisticsAccessor;
import ru.taxi.adminpanel.backend.background.statistics.TripsPerRangeEntity;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@CssImport("./styles/views/charts/charts-view.css")
@Route(value = "charts", layout = MainView.class)
@PageTitle("Chartboard")
//@PreserveOnRefresh
public class ChartsView extends Div implements RouterLayout {
    private final Grid<String> grid = new Grid<>();

    private final H2 addressesCount = new H2();
    private final H2 tripsCount = new H2();
    private final H2 mostPopularArea = new H2();
    private final Chart tripsPerRangeChart = new Chart(ChartType.SPLINE);

    private final StatisticsAccessor statisticsAccessor;


    public ChartsView(StatisticsAccessor statisticsAccessor) {
        this.statisticsAccessor = statisticsAccessor;

        addClassName("charts-view");
        grid.addColumn(String::valueOf).setHeader("City");
        grid.addColumn(new ComponentRenderer<>(item -> {
            Span span = new Span("Connected");
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Status").setFlexGrow(0).setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        WrapperCard gridWrapper = new WrapperCard("wrapper", new Component[]{new H3("Cities around main"), grid}, "card");
        WrapperCard responseTimesWrapper = new WrapperCard("wrapper", new Component[]{tripsPerRangeChart}, "card");

        Board board = initBoard();
        board.addRow(gridWrapper);
        board.addRow(responseTimesWrapper);

        add(board);

        populateCharts();
    }

    private Board initBoard() {
        Board board = new Board();
        board.addRow(createBadge("Trips", tripsCount, "primary-text", "Trips in the current area", "badge"),
                createBadge("Area", mostPopularArea, "success-text", "Most popular area", "badge success"),
                createBadge("Addresses", addressesCount, "success-text", "Addresses found for this region", "badge success")
        );
        return board;
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
        GeneralStatisticsEntity generalStatisticsEntity = statisticsAccessor.loadStatistics();
        populateCitiesConnectedGrid(generalStatisticsEntity.getCitiesInArea());
        populateMostPopularRegionTab(generalStatisticsEntity.getMostPopularZip());
        populateTripsPerRangeChart(generalStatisticsEntity.getTripsPerRangeEntities());
        populateAddressesGeneratedTab(generalStatisticsEntity.getAddressesCount());
        populateTripsTotal(generalStatisticsEntity.getTripsTotal());
    }

    private void populateCitiesConnectedGrid(String citiesString) {
        if (citiesString == null || citiesString.isBlank()) {
            return;
        }
        List<String> cities = Arrays.asList(citiesString.split(";"));
        grid.setItems(cities);
    }

    private void populateTripsTotal(Long trips) {
        tripsCount.setText(String.valueOf(trips == null ? "CALCULATING" : trips));
    }

    private void populateAddressesGeneratedTab(Long addressesGenerated) {
        addressesCount.setText(String.valueOf(addressesGenerated == null ? "CALCULATING" : addressesGenerated));
    }

    private void populateMostPopularRegionTab(String mostPopularZip) {
        mostPopularArea.setText(mostPopularZip == null ? "CALCULATING" : mostPopularZip);
    }

    private void populateTripsPerRangeChart(List<TripsPerRangeEntity> tripsPerRange) {
        if (tripsPerRange == null || tripsPerRange.isEmpty()) {
            return;
        }

        tripsPerRangeChart.getConfiguration().setTitle("Trips in range times");
        tripsPerRangeChart.setTimeline(true);
        Configuration configuration = tripsPerRangeChart.getConfiguration();
        configuration.getTooltip().setEnabled(true);

        DataSeries dataSeries = new DataSeries();
        tripsPerRange.forEach((entry) -> {
            var item = new DataSeriesItem();
            item.setX(entry.getTimestamp());
            item.setY(entry.getTripsCount());
            dataSeries.add(item);
        });
        configuration.addSeries(dataSeries);
        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setSelected(1);
        configuration.setRangeSelector(rangeSelector);
    }
}
