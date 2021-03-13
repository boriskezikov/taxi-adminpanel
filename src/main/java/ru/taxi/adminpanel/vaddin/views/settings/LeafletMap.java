package ru.taxi.adminpanel.vaddin.views.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import lombok.SneakyThrows;

import java.util.List;

@CssImport("leaflet/dist/leaflet.css")
@JsModule("./components/leafletmap/leaflet-map.ts")
@Tag("leaflet-map")
public class LeafletMap extends Component implements HasSize {

    public void setView(double latitude, double longitude, int zoomLevel) {
        getElement().callJsFunction("setView", latitude, longitude, zoomLevel);
    }

    public void setPoint(double latitude, double longitude, String fullAddress) {
        getElement().callJsFunction("setPoint", latitude, longitude, fullAddress);
    }

    @SneakyThrows
    public void setPoints(MapDto[] points){
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(points);
        getElement().callJsFunction("setPoints", json);
    }
}