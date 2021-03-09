package ru.taxi.adminpanel.backend.generator;

import com.google.maps.model.LatLng;
import ru.taxi.adminpanel.backend.address.AddressEntity;

import java.util.Objects;
import java.util.Random;

public class ArtificialDataHelper {

    public static LatLng getRandomPoint(double cityCenterLng, double cityCenterLat, int radius) {
        Random random = new Random();
        double radiusInDegrees = radius / 111320f;
        double w = radiusInDegrees * Math.sqrt(random.nextDouble());
        double t = 2 * Math.PI * random.nextDouble();
        double new_x = w * Math.cos(t) / Math.cos(Math.toRadians(cityCenterLat));
        double foundLatitude = cityCenterLat + w * Math.sin(t);
        double foundLongitude = cityCenterLng + new_x;
        return new LatLng(foundLatitude, foundLongitude);
    }

    public static boolean validateAddress(AddressEntity ae) {
        return  !(Objects.nonNull(ae) && ae.getCity() != null);
    }
}
