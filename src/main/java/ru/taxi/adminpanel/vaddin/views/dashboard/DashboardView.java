package ru.taxi.adminpanel.vaddin.views.dashboard;

import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordService;
import ru.taxi.adminpanel.vaddin.views.main.MainView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Route(value = "admin-panel", layout = MainView.class)
@SpringComponent
@UIScope
@PageTitle("Dashboard")
@CssImport(value = "./styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@RouteAlias(value = "", layout = MainView.class)
//@Push(PushMode.MANUAL)
public class DashboardView extends Div implements RouterLayout {

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
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.MATERIAL_COLUMN_DIVIDERS);
        grid.setHeight("100%");
        UI current = UI.getCurrent();
        CompletableFuture.completedFuture(current.access(() -> {
            List<TripRecordEntity> all = tripRecordService.findAll();
            dataProvider = new ListDataProvider<>(all);
            grid.setDataProvider(dataProvider);
        })).thenRun(current::push);
    }

    private void addColumnsToGrid() {
        createIdColumn();
        createPriceColumn();
        createFromColumn();
        createToColumn();
        createBeginColumn();
        createEndColumn();
        createUuidColumn();
    }

    private void createIdColumn() {
        idColumn = grid.addColumn(TripRecordEntity::getId, "id").setHeader("ID");
    }

    private void createUuidColumn() {
        String header = "UUID";
        uuidColumn = grid.addColumn(TripRecordEntity::getUuid)
                .setHeader(header)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setSortable(false);
        log.debug("{} column created", header);
    }

    private void createFromColumn() {
        String header = "From address";
        fromColumn = grid.addColumn(TripRecordEntity::getFromAddressEntity).setHeader(header)
                .setFlexGrow(1)
                .setAutoWidth(true);
        log.debug("{} column created", header);
    }

    private void createToColumn() {
        String header = "To address";
        toColumn = grid.addColumn(TripRecordEntity::getToAddressEntity).setHeader(header)
                .setFlexGrow(1)
                .setAutoWidth(true);
        log.debug("{} column created", header);
    }

    private void createPriceColumn() {
        String header = "Trip price";
        priceColumn = grid
                .addColumn(new NumberRenderer<>(TripRecordEntity::getPrice, "%s₽"))
                .setComparator(TripRecordEntity::getPrice).setHeader(header)
                .setAutoWidth(true);
        log.debug("{} column created", header);
    }

    private void createBeginColumn() {
        String header = "Begin time";
        beginColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(TripRecordEntity::getTripBeginTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setComparator(TripRecordEntity::getTripBeginTime).setHeader(header)
                .setAutoWidth(true);
        log.debug("{} column created", header);
    }

    private void createEndColumn() {
        String header = "End time";
        endColumn = grid
                .addColumn(new LocalDateTimeRenderer<>(TripRecordEntity::getTripEndTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setComparator(TripRecordEntity::getTripEndTime).setHeader(header)
                .setAutoWidth(true);
        log.debug("{} column created", header);
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

        Stream.of(endColumn, beginColumn).forEach(col -> {
            var filter = getDefaultFilter();
            filter.setEnabled(false);
            filterRow.getCell(col).setComponent(filter);
        });

        var toFilter = getDefaultFilter();
        toFilter.addValueChangeListener(event -> dataProvider.addFilter(trip ->
                StringUtils.containsIgnoreCase(trip.getToAddressEntity().toString(), toFilter.getValue())));
        filterRow.getCell(toColumn).setComponent(toFilter);

        var fromFilter = getDefaultFilter();
        fromFilter.addValueChangeListener(event -> dataProvider.addFilter(trip ->
                StringUtils.containsIgnoreCase(trip.getFromAddressEntity().toString(), fromFilter.getValue())));
        filterRow.getCell(fromColumn).setComponent(fromFilter);

        var uuidFilter = getDefaultFilter();
        uuidFilter.addValueChangeListener(event -> dataProvider.addFilter(trip ->
                StringUtils.containsIgnoreCase(trip.getUuid().toString(), uuidFilter.getValue())));
        filterRow.getCell(uuidColumn).setComponent(uuidFilter);
    }


    private TextField getDefaultFilter() {
        var filter = new TextField();
        filter.setPlaceholder("Filter");
        filter.setClearButtonVisible(true);
        filter.setWidth("100%");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        return filter;
    }

};
