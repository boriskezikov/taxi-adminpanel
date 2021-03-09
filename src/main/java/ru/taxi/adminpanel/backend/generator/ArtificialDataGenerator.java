package ru.taxi.adminpanel.backend.generator;

import com.google.maps.model.LatLng;
import com.namics.commons.random.generator.basic.LocalDateTimeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.address.AddressRepository;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiDistanceMatrix;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiException;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiGeoDecoderGoogle;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.taxi.adminpanel.backend.generator.ArtificialDataHelper.getRandomPoint;
import static ru.taxi.adminpanel.backend.utils.Constants.DEFAULT_PRICE_PER_MIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArtificialDataGenerator {

    private final GoogleApiGeoDecoderGoogle googleApiGeoDecoder;
    private final GoogleApiDistanceMatrix roadRetriever;
    private final TripRecordRepository tripRecordRepository;
    private final AddressRepository addressRepository;

    private static final Integer MAX_RETRIES = 10;
    
    public CompletableFuture<Void> generateAsync(GeneratorParametersEntity gParams) {
        return CompletableFuture.runAsync(() -> generateUntilComplete(gParams)).thenRun(() -> {
            log.info("Generation finished");
        }).exceptionally(ex -> {
            log.error("Generation failed with error {}, retry.", ex.getCause().toString());
            generateUntilComplete(gParams);
            return null;
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOnComplete(List<TripRecordEntity> generatedTrips, boolean cleanUp) {
        log.info("saveOnComplete");
        if (cleanUp) {
            tripRecordRepository.deleteAll();
            addressRepository.deleteAll();
        }

        tripRecordRepository.saveAll(generatedTrips);
    }

    private void generateUntilComplete(GeneratorParametersEntity gParams) {
        long start = System.currentTimeMillis();
        List<TripRecordEntity> generatedTrips = new ArrayList<>();
        int offset = gParams.getOrdersNumber();
        while (offset > 0) {
            log.info("Next Generation step. Offset: {}", offset);
            gParams.setOrdersNumber(offset);
            List<TripRecordEntity> tripRecordEntities = supplyTripsAsync(gParams);
            generatedTrips.addAll(tripRecordEntities);
            offset = gParams.getOrdersNumber() - generatedTrips.size();
        }
        saveOnComplete(generatedTrips, gParams.isClean());
        log.info("Generation time: {}", System.currentTimeMillis() - start);
    }

    private List<TripRecordEntity> supplyTripsAsync(GeneratorParametersEntity gParams) {
        CompletableFuture<List<AddressEntity>> fromAddressesFuture = CompletableFuture.supplyAsync(() -> supplyMapPoints(gParams))
                .thenApply((res) -> supplyAddresses(res, gParams.getLanguage()))
                .exceptionally(ex -> {
                    log.error(ex.getCause().toString());
                    throw new GoogleApiException("Error:", ex);
                });

        CompletableFuture<List<AddressEntity>> toAddressesFuture = CompletableFuture.supplyAsync(() -> supplyMapPoints(gParams))
                .thenApply((res) -> supplyAddresses(res, gParams.getLanguage()))
                .exceptionally(ex -> {
                    log.error(ex.getCause().toString());
                    throw new GoogleApiException("Error:", ex);
                });
        
        List<AddressEntity> fromAddresses = fromAddressesFuture.join();
        List<AddressEntity> toAddresses = toAddressesFuture.join();

        fromAddresses.removeIf(ArtificialDataHelper::validateAddress);
        toAddresses.removeIf(ArtificialDataHelper::validateAddress);

        int limit = fromAddresses.size() < gParams.getOrdersNumber() ? fromAddresses.size() : gParams.getOrdersNumber();
        return Stream.generate(() -> supplySingleTrip(fromAddresses, toAddresses))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<LatLng> supplyMapPoints(GeneratorParametersEntity gParams) {
        log.info("supplyMapPoints");
        return Stream.generate(() -> getRandomPoint(gParams.getLng(), gParams.getLat(), gParams.getRad()))
                .limit(gParams.getOrdersNumber())
                .collect(Collectors.toList());
    }

    private TripRecordEntity supplySingleTrip(List<AddressEntity> fromAddr, List<AddressEntity> toAddr) {
        LocalDateTimeGenerator localDateTimeGenerator = new LocalDateTimeGenerator();
        int toIndex = ThreadLocalRandom.current().nextInt(toAddr.size()) % toAddr.size();
        int fromIndex = ThreadLocalRandom.current().nextInt(fromAddr.size()) % fromAddr.size();
        AddressEntity fromAddress = fromAddr.get(fromIndex);
        AddressEntity toAddress = toAddr.get(toIndex);
        LocalDateTime tripBegin = localDateTimeGenerator.random();
        long roadDuration = roadRetriever.findRoadDuration(fromAddress.getGeometry(), toAddress.getGeometry());
        LocalDateTime tripEnd = tripBegin.plusSeconds(roadDuration);
        log.info("Single trip supplied");
        return TripRecordEntity.builder()
                .fromAddressEntity(fromAddress)
                .toAddressEntity(toAddress)
                .tripBeginTime(tripBegin)
                .tripEndTime(tripEnd)
                .price(DEFAULT_PRICE_PER_MIN * roadDuration / 60)
                .build();
    }

    private List<AddressEntity> supplyAddresses(List<LatLng> coordinatesList, String lang) {
        return coordinatesList.stream().parallel().flatMap(latLng -> {
            List<AddressEntity> decoded = googleApiGeoDecoder.decode(latLng, lang);
            return decoded.isEmpty() ? null : decoded.stream();
        }).collect(Collectors.toList());
    }
}
