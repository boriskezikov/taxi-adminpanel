package ru.taxi.adminpanel.backend.generator;

import com.google.maps.model.LatLng;
import ru.taxi.adminpanel.backend.address.AddressEntity;

import java.util.Objects;
import java.util.Random;

public class ArtificialDataHelper {

    public static LatLng getRandomPoint(GeneratorParametersEntity gParams) {
        Random random = new Random();
        double radiusInDegrees = gParams.getRad() / 111320f;
        double w = radiusInDegrees * Math.sqrt(random.nextDouble());
        double t = 2 * Math.PI * random.nextDouble();
        double new_x = w * Math.cos(t) / Math.cos(Math.toRadians(gParams.getLat()));
        double foundLatitude = gParams.getLat() + w * Math.sin(t);
        double foundLongitude = gParams.getLng() + new_x;
        return new LatLng(foundLatitude, foundLongitude);
    }

    public static boolean validateAddress(AddressEntity ae) {
        return !(Objects.nonNull(ae) && ae.getCity() != null);
    }

    public static LatLng generatePointIntoCircle(LatLng center, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(center.lat);

        double foundLongitude = new_x + center.lng;
        double foundLatitude = y + center.lat;
        System.out.println("Longitude: " + foundLongitude + "  Latitude: " + foundLatitude);
        return new LatLng(foundLatitude, foundLongitude);
    }
}
