package ru.taxi.adminpanel.vaddin.views.dashboard;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.StringUtils;
import ru.taxi.adminpanel.backend.taxitrip.TripRecordEntity;
import ru.taxi.adminpanel.backend.taxitrip.TripRecordService;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.text.NumberFormat;
import java.time.LocalDate;
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

    private GridPro<TripRecordEntity> grid;
    private ListDataProvider<TripRecordEntity> dataProvider;
    private final TripRecordService tripRecordService;

    private Grid.Column<TripRecordEntity> idColumn;
    private Grid.Column<TripRecordEntity> uuidColumn;
    private Grid.Column<TripRecordEntity> fromColumn;
    private Grid.Column<TripRecordEntity> toColumn;
    private Grid.Column<TripRecordEntity> priceColumn;
    private Grid.Column<TripRecordEntity> beginColumn;
    private Grid.Column<TripRecordEntity> endColumn;

    public DashboardView(TripRecordService tripRecordService) {
        this.tripRecordService = tripRecordService;
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
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");
        dataProvider = new ListDataProvider<>(tripRecordService.findAll());
        grid.setDataProvider(dataProvider);
        grid.addSelectionListener((SelectionListener<Grid<TripRecordEntity>, TripRecordEntity>) selectionEvent -> Notification.show("Selected row: " + grid.getSelectedItems()));
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createPriceColumn();
        createFromColumn();
        createToColumn();
        createBeginColumn();
        createEndColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(TripRecordEntity::getId, "id").setHeader("ID");
    }

    private void createFromColumn() {
        fromColumn = grid.addEditColumn(TripRecordEntity::getFromAddress)
                .text((item, newValue) -> {
                    item.setFromAddress(newValue);
                    tripRecordService.updateRecord(item);
                })
                .setComparator(TripRecordEntity::getFromAddress).setHeader("From address");
    }

    private void createToColumn() {
        toColumn = grid.addEditColumn(TripRecordEntity::getToAddress).text((item, newValue) -> {
            item.setToAddress(newValue);
            tripRecordService.updateRecord(item);
        })
                .setComparator(TripRecordEntity::getToAddress).setHeader("To address");
    }

    private void createPriceColumn() {
        priceColumn = grid
                .addEditColumn(TripRecordEntity::getPrice,
                        new NumberRenderer<>(TripRecordEntity::getPrice, NumberFormat.getCurrencyInstance(Locale.US), "-"))
                .text((item, newValue) -> {
                    item.setPrice(Double.parseDouble(newValue));
                    tripRecordService.updateRecord(item);
                })
                .setComparator(TripRecordEntity::getPrice).setHeader("Trip price");

    }

    private void createBeginColumn() {
        beginColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(TripRecordEntity::getTripBeginTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setComparator(TripRecordEntity::getTripBeginTime).setHeader("Begin time");
    }

    private void createEndColumn() {
        endColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(TripRecordEntity::getTripEndTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setComparator(TripRecordEntity::getTripEndTime).setHeader("End time");
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
        dateBeginFilter.setWidth("100%");
        dateBeginFilter.addValueChangeListener(event -> dataProvider.addFilter(trip -> areDatesEqual(trip.getTripBeginTime(), dateBeginFilter)));
        filterRow.getCell(beginColumn).setComponent(dateBeginFilter);

        DateTimePicker dateEndFilter = new DateTimePicker();
        dateEndFilter.setDatePlaceholder("Date Filter");
        dateEndFilter.setTimePlaceholder("Time Filter");
        dateEndFilter.setWidth("100%");
        dateEndFilter.addValueChangeListener(event -> dataProvider.addFilter(trip -> areDatesEqual(trip.getTripEndTime(), dateEndFilter)));
        filterRow.getCell(endColumn).setComponent(dateEndFilter);
    }


    private boolean areDatesEqual(LocalDateTime dateTime, DateTimePicker dateFilter) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            return dateFilterValue.equals(LocalDateTime.parse(dateTimeFormatter.format(dateTime)));
        }
        return true;
    }
};
