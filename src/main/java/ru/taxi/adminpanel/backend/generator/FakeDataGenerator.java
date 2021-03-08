package ru.taxi.adminpanel.backend.generator;

import com.google.maps.model.LatLng;
import com.namics.commons.random.generator.basic.LocalDateTimeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.adminpanel.backend.domain.AddressEntity;
import ru.taxi.adminpanel.backend.domain.TripRecordEntity;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiDistanceMatrix;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiGeoDecoder;
import ru.taxi.adminpanel.backend.repository.AddressRepository;
import ru.taxi.adminpanel.backend.repository.TripRecordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FakeDataGenerator {

    private final GeneratorAccessorService generatorAccessorService;
    private final GoogleApiGeoDecoder googleApiGeoDecoder;
    private final GoogleApiDistanceMatrix roadRetriever;
    private final TripRecordRepository tripRecordRepository;
    private final AddressRepository addressRepository;

    public void generate(GeneratorParametersEntity gParams) {
        long start = System.currentTimeMillis();
        if (gParams == null) {
            gParams = generatorAccessorService.loadParameters();
        }
        List<TripRecordEntity> generatedTrips = regenerateData(gParams);
        while (true) {
            if (generatedTrips.size() == gParams.getOrdersNumber()) break;
            else {
                int offset = generatedTrips.size() - gParams.getOrdersNumber();
                gParams.setOrdersNumber(offset);
                generatedTrips.addAll(regenerateData(gParams));
            }
        }
        tripRecordRepository.deleteAll();
        addressRepository.deleteAll();
        tripRecordRepository.saveAll(generatedTrips);
        log.info("Generation time: {}", start - System.currentTimeMillis());
    }

    @Transactional
    protected List<TripRecordEntity> regenerateData(GeneratorParametersEntity gParams) {
        List<LatLng> coordinatesFrom = generateMapPoints(gParams);
        List<LatLng> coordinatesTo = generateMapPoints(gParams);
        long roadDuration = roadRetriever.findRoadDuration(coordinatesFrom.get(0), coordinatesTo.get(0));
        List<AddressEntity> fromAddresses = loadAddresses(coordinatesFrom, gParams.getLanguage());
        List<AddressEntity> toAddresses = loadAddresses(coordinatesTo, gParams.getLanguage());
        fromAddresses.removeIf(Objects::isNull);
        toAddresses.removeIf(Objects::isNull);
        return Stream.generate(() -> supplyTrip(fromAddresses, toAddresses, roadDuration))
                .limit(gParams.getOrdersNumber()).collect(Collectors.toList());
    }

    private TripRecordEntity supplyTrip(List<AddressEntity> fromAddr, List<AddressEntity> toAddr, long roadDuration) {
        LocalDateTimeGenerator localDateTimeGenerator = new LocalDateTimeGenerator();
        int randomElementIndexTo = ThreadLocalRandom.current().nextInt(toAddr.size()) % toAddr.size();
        int randomElementIndexFrom = ThreadLocalRandom.current().nextInt(fromAddr.size()) % fromAddr.size();
        AddressEntity fromAddress = fromAddr.get(randomElementIndexFrom);
        AddressEntity toAddress = toAddr.get(randomElementIndexTo);
        LocalDateTime tripBegin = localDateTimeGenerator.random();
        LocalDateTime tripEnd = tripBegin.plusSeconds(roadDuration);
        return TripRecordEntity.builder()
                .fromAddressEntity(fromAddress)
                .toAddressEntity(toAddress)
                .tripBeginTime(tripBegin)
                .tripEndTime(tripEnd)
                .price(new Random().nextDouble())
                .build();
    }


    private List<AddressEntity> loadAddresses(List<LatLng> coordinatesList, String lang) {
        return coordinatesList.stream().map(latLng -> {
            List<AddressEntity> decoded = googleApiGeoDecoder.decode(latLng, lang);
            if (decoded.isEmpty()) {
                return null;
            }
            return decoded.iterator().next();
        }).collect(Collectors.toList());
    }

    private List<LatLng> generateMapPoints(GeneratorParametersEntity gParams) {
        return Stream.generate(() -> getRandomPoint(gParams.getLng(), gParams.getLat(), gParams.getRad()))
                .limit(gParams.getOrdersNumber())
                .collect(Collectors.toList());
    }

    private LatLng getRandomPoint(double cityCenterLng, double cityCenterLat, int radius) {

        Random random = new Random();

        double radiusInDegrees = radius / 111320f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);
        double new_x = x / Math.cos(Math.toRadians(cityCenterLat));

        double foundLatitude = cityCenterLat + y;
        double foundLongitude = cityCenterLng + new_x;

        return new LatLng(foundLatitude, foundLongitude);
    }
}
