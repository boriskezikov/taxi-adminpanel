package ru.taxi.adminpanel.vaddin.views.map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordRepository;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@CssImport("./styles/views/charts/charts-view.css")
@Route(value = "charts", layout = MainView.class)
@PageTitle("Chartboard")
public class ChartsView extends Div {
    private Grid<String> grid = new Grid<>();

    private Chart responseTimes = new Chart();
    private final H2 tripsCount = new H2();
    private final H2 mostPopularArea = new H2();
    private final TripRecordRepository tripRecordRepository;


    public ChartsView(TripRecordRepository tripRecordRepository) {
        this.tripRecordRepository = tripRecordRepository;
        addClassName("chart-view");
        Board board = new Board();
        board.addRow(createBadge("Trips", tripsCount, "primary-text", "Trips in the current area", "badge"),
                createBadge("Area", mostPopularArea, "success-text", "Most popular area", "badge success"));
        grid.addColumn(String::valueOf).setHeader("City");
        grid.addColumn(new ComponentRenderer<>(item -> {
            Span span = new Span("Connected");
            span.getElement().getThemeList().add("Excellent");
            return span;
        })).setHeader("Status").setFlexGrow(0).setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        WrapperCard gridWrapper = new WrapperCard("wrapper", new Component[]{new H3("Service health"), grid}, "card");
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

        //populate cities list
        var trips = tripRecordRepository.findAll();
        Set<String> addresses = trips.stream().map(TripRecordEntity::getFromAddressEntity)
                .map(AddressEntity::getCity)
                .collect(Collectors.toSet());
        tripsCount.setText(String.valueOf(trips.size()));
        grid.setItems(addresses);

        var areas = trips.stream().map(t -> t.getFromAddressEntity().getZipCode()).collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        String mostPopularZip = Collections.max(areas.entrySet(), Map.Entry.comparingByValue()).getKey();
        mostPopularArea.setText(String.valueOf(mostPopularZip));

//        Map<LocalDateTime, List<String>> zipToTime = new HashMap<>();
//        for (TripRecordEntity trip : trips) {
//            if (zipToTime.put(trip.getTripBeginTime(), trip.getFromAddressEntity().getZipCode()) != null) {
//                throw new IllegalStateException("Duplicate key");
//            }
//        }
//        // Second chart
//        Configuration configuration = responseTimes.getConfiguration();
//        configuration
//                .addSeries(new ListSeries("Time", 7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6));
//
//        XAxis x = new XAxis();
//        x.setCrosshair(new Crosshair());
//        x.setCategories("00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00");
//        configuration.addxAxis(x);
//
//        YAxis y = new YAxis();
//        y.setMin(0);
//        configuration.addyAxis(y);
    }
}
