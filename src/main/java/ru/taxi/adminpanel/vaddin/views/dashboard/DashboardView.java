package ru.taxi.adminpanel.vaddin.views.dashboard;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.apache.commons.lang3.StringUtils;
import ru.taxi.adminpanel.backend.domain.TripRecord;
import ru.taxi.adminpanel.backend.service.RecordService;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

@Route(value = "admin-panel", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport(value = "./styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@RouteAlias(value = "", layout = MainView.class)
public class DashboardView extends Div {

    private GridPro<TripRecord> grid;
    private ListDataProvider<TripRecord> dataProvider;
    @Inject
    private RecordService recordService;

    private Grid.Column<TripRecord> idColumn;
    private Grid.Column<TripRecord> uuidColumn;
    private Grid.Column<TripRecord> fromColumn;
    private Grid.Column<TripRecord> toColumn;
    private Grid.Column<TripRecord> priceColumn;
    private Grid.Column<TripRecord> statusColumn;
    private Grid.Column<TripRecord> beginColumn;
    private Grid.Column<TripRecord> endColumn;

    public DashboardView() {
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

        dataProvider = new ListDataProvider<TripRecord>(recordService.findAll());
        grid.setDataProvider(dataProvider);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createClientColumn();
        createPriceColumn();
        createStatusColumn();
        createDateColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(TripRecord::getId, "id").setHeader("ID").setWidth("120px").setFlexGrow(0);
    }

//    private void createClientColumn() {
//        toColumn = grid.addColumn(new ComponentRenderer<>(client -> {
//            HorizontalLayout hl = new HorizontalLayout();
//            hl.setAlignItems(Alignment.CENTER);
//            Image img = new Image(client.getImg(), "");
//            Span span = new Span();
//            span.setClassName("name");
//            span.setText(client.getClient());
//            hl.add(img, span);
//            return hl;
//        })).setComparator(client -> client.getClient()).setHeader("Client");
//    }

    private void createPriceColumn() {
        priceColumn = grid
                .addEditColumn(TripRecord::getPrice,
                        new NumberRenderer<>(TripRecord::getPrice, NumberFormat.getCurrencyInstance(Locale.US)))
                .text((item, newValue) -> item.setPrice(Double.parseDouble(newValue)))
                .setComparator(TripRecord::getPrice).setHeader("Trip price");
    }

//    private void createStatusColumn() {
//        statusColumn = grid.addEditColumn(Client::getClient, new ComponentRenderer<>(client -> {
//            Span span = new Span();
//            span.setText(client.getStatus());
//            span.getElement().setAttribute("theme", "badge " + client.getStatus().toLowerCase());
//            return span;
//        })).select(Client::setStatus, Arrays.asList("Pending", "Success", "Error"))
//                .setComparator(Client::getStatus).setHeader("Status");
//    }

    private void createDateColumn() {
        endColumn = grid
                .addColumn(new LocalDateRenderer<>(client -> LocalDate.parse(client.getDate()),
                        DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator(Client::getDate).setHeader("Date").setWidth("180px").setFlexGrow(0);
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

//        TextField clientFilter = new TextField();
//        clientFilter.setPlaceholder("Filter");
//        clientFilter.setClearButtonVisible(true);
//        clientFilter.setWidth("100%");
//        clientFilter.setValueChangeMode(ValueChangeMode.EAGER);
//        clientFilter.addValueChangeListener(event -> dataProvider
//                .addFilter(trip -> StringUtils.containsIgnoreCase(trip.getClient(), clientFilter.getValue())));
//        filterRow.getCell(toColumn).setComponent(clientFilter);

        TextField priceFilter = new TextField();
        priceFilter.setPlaceholder("Filter");
        priceFilter.setClearButtonVisible(true);
        priceFilter.setWidth("100%");
        priceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        priceFilter.addValueChangeListener(event -> dataProvider.addFilter(trip -> StringUtils
                .containsIgnoreCase(Double.toString(trip.getPrice()), priceFilter.getValue())));
        filterRow.getCell(priceColumn).setComponent(priceFilter);

//        ComboBox<String> statusFilter = new ComboBox<>();
//        statusFilter.setItems(Arrays.asList("Pending", "Success", "Error"));
//        statusFilter.setPlaceholder("Filter");
//        statusFilter.setClearButtonVisible(true);
//        statusFilter.setWidth("100%");
//        statusFilter.addValueChangeListener(
//                event -> dataProvider.addFilter(client -> areStatusesEqual(client, statusFilter)));
//        filterRow.getCell(statusColumn).setComponent(statusFilter);

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPlaceholder("Filter");
        dateFilter.setClearButtonVisible(true);
        dateFilter.setWidth("100%");
        dateFilter.addValueChangeListener(event -> dataProvider.addFilter(client -> areDatesEqual(client, dateFilter)));
        filterRow.getCell(endColumn).setComponent(dateFilter);
    }

//    private boolean areStatusesEqual(Client client, ComboBox<String> statusFilter) {
//        String statusFilterValue = statusFilter.getValue();
//        if (statusFilterValue != null) {
//            return StringUtils.equals(client.getStatus(), statusFilterValue);
//        }
//        return true;
//    }

    private boolean areDatesEqual(Client client, DatePicker dateFilter) {
        LocalDate dateFilterValue = dateFilter.getValue();
        if (dateFilterValue != null) {
            LocalDate clientDate = LocalDate.parse(client.getDate());
            return dateFilterValue.equals(clientDate);
        }
        return true;
    }
};
