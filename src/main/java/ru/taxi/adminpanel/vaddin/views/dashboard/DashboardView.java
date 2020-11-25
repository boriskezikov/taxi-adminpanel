package ru.taxi.adminpanel.vaddin.views.dashboard;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.StringUtils;
import ru.taxi.adminpanel.backend.domain.TripRecord;
import ru.taxi.adminpanel.backend.service.RecordService;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Route(value = "admin-panel", layout = MainView.class)
@SpringComponent
@UIScope
@PageTitle("Dashboard")
@CssImport(value = "./styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@RouteAlias(value = "", layout = MainView.class)
public class DashboardView extends Div {

    private GridPro<TripRecord> grid;
    private ListDataProvider<TripRecord> dataProvider;
    private final RecordService recordService;

    private Grid.Column<TripRecord> idColumn;
    private Grid.Column<TripRecord> uuidColumn;
    private Grid.Column<TripRecord> fromColumn;
    private Grid.Column<TripRecord> toColumn;
    private Grid.Column<TripRecord> priceColumn;
    private Grid.Column<TripRecord> beginColumn;
    private Grid.Column<TripRecord> endColumn;

    public DashboardView(RecordService recordService) {
        this.recordService = recordService;
        setId("dashboard-view");
        setSizeFull();
        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        dataProvider = new ListDataProvider<>(recordService.findAll());
        grid.setDataProvider(dataProvider);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createUuidColumn();

        createPriceColumn();

        createFromColumn();
        createToColumn();

        createBeginColumn();
        createEndColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(TripRecord::getId, "id").setHeader("ID");
    }
    private void createUuidColumn(){
        uuidColumn = grid.addColumn(TripRecord::getUuid, "uuid").setHeader("uuid");
    }

    private void createFromColumn(){
        fromColumn = grid.addColumn(TripRecord::getFromAddress)
        .setComparator(TripRecord::getFromAddress).setHeader("From address");
    }

    private void createToColumn(){
        toColumn = grid.addColumn(TripRecord::getToAddress)
                .setComparator(TripRecord::getToAddress).setHeader("To address");
    }


    private void createPriceColumn() {
        priceColumn = grid
                .addEditColumn(TripRecord::getPrice,
                        new NumberRenderer<>(TripRecord::getPrice, NumberFormat.getCurrencyInstance(Locale.US)))
                .text((item, newValue) -> item.setPrice(Double.parseDouble(newValue)))
                .setComparator(TripRecord::getPrice).setHeader("Trip price");
    }

    private void createBeginColumn() {
        beginColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(TripRecord::getTripBeginTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setComparator(TripRecord::getTripBeginTime).setHeader("Begin time");
    }

    private void createEndColumn() {
        endColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(TripRecord::getTripEndTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setComparator(TripRecord::getTripEndTime).setHeader("End time");
    }


    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(event -> dataProvider.addFilter(
                trip -> StringUtils.containsIgnoreCase(trip.getId().toString(), idFilter.getValue())));
        filterRow.getCell(idColumn).setComponent(idFilter);


        TextField priceFilter = new TextField();
        priceFilter.setPlaceholder("Filter");
        priceFilter.setClearButtonVisible(true);
        priceFilter.setWidth("100%");
        priceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        priceFilter.addValueChangeListener(event -> dataProvider.addFilter(trip -> StringUtils
                .containsIgnoreCase(Double.toString(trip.getPrice()), priceFilter.getValue())));
        filterRow.getCell(priceColumn).setComponent(priceFilter);


        DateTimePicker dateBeginFilter = new DateTimePicker();
        dateBeginFilter.setDatePlaceholder("Date Filter");
        dateBeginFilter.setTimePlaceholder("Time Filter");
//        dateBeginFilter.setClearButtonVisible(true);
        dateBeginFilter.setWidth("100%");
        dateBeginFilter.addValueChangeListener(event -> dataProvider.addFilter(trip -> areDatesEqual(trip.getTripBeginTime(), dateBeginFilter)));
        filterRow.getCell(endColumn).setComponent(dateBeginFilter);

        DateTimePicker dateEndFilter = new DateTimePicker();
        dateEndFilter.setDatePlaceholder("Date Filter");
        dateEndFilter.setTimePlaceholder("Time Filter");
        dateEndFilter.setWidth("100%");
        dateEndFilter.addValueChangeListener(event -> dataProvider.addFilter(trip -> areDatesEqual(trip.getTripEndTime(), dateEndFilter)));
        filterRow.getCell(beginColumn).setComponent(dateEndFilter);
    }


    private boolean areDatesEqual(LocalDateTime dateTime, DateTimePicker dateFilter) {
        LocalDateTime dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            return dateFilterValue.equals(dateTime);
        }
        return true;
    }
};
