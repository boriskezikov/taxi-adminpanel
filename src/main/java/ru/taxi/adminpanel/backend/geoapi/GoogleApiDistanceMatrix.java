package ru.taxi.adminpanel.backend.geoapi;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.util.Arrays.stream;

@Component
public class GoogleApiDistanceMatrix extends BasicGeoConfigurator {

    private static final long DEFAULT = 3600;

    public long findRoadDuration(LatLng from, LatLng to) {
        try {
            initializeContext();
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(from).destinations(to)
                    .mode(TravelMode.DRIVING)
                    .await();
            DistanceMatrixRow[] rows = distanceMatrix.rows;
            return stream(rows).flatMap(row -> stream(row.elements)).map(distanceMatrixElement -> distanceMatrixElement.duration)
                    .map(duration -> duration.inSeconds)
                    .findFirst().orElse(DEFAULT);
        } catch (ApiException | InterruptedException | IOException e) {
            throw new ExternalApiException("Error:", e);
        } finally {
            geoApiContext.shutdown();
        }
    }
}
