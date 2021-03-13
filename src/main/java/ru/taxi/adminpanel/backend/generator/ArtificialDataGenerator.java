package ru.taxi.adminpanel.backend.generator;

import com.google.maps.model.LatLng;
import com.namics.commons.random.generator.basic.LocalDateTimeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.address.AddressRepository;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiDistanceMatrix;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiGeoDecoderGoogle;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    public void generateAsync(GeneratorParametersEntity gParams) {
        if (gParams.isClean()) {
            tripRecordRepository.deleteAll();
            addressRepository.deleteAll();
        }
        CompletableFuture.supplyAsync(() -> Stream.generate(() -> generateTrip(gParams)).parallel()
                .limit(gParams.getOrdersNumber())
                .filter(r -> r).count()).thenAccept((success) -> {
            log.info("Generation finished: tasks {} - succeed, {} failed", success, gParams.getOrdersNumber() - success);
        });
    }


    private boolean generateTrip(GeneratorParametersEntity gParams) {
        try {
            Pair<AddressEntity, AddressEntity> fromTo = supplyAddresses(supplyMapPoints(gParams), gParams.getLanguage());
            TripRecordEntity trip = supplySingleTrip(fromTo);
            saveOnComplete(trip);
            return true;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    private Pair<LatLng, LatLng> supplyMapPoints(GeneratorParametersEntity gParams) {
        return Pair.of(getRandomPoint(gParams), getRandomPoint(gParams));
    }

    private TripRecordEntity supplySingleTrip(Pair<AddressEntity, AddressEntity> fromTo) {
        LocalDateTimeGenerator localDateTimeGenerator = new LocalDateTimeGenerator();
        LocalDateTime tripBegin = localDateTimeGenerator.random();
        long roadDuration = roadRetriever.findRoadDuration(fromTo.getFirst().getGeometry(), fromTo.getSecond().getGeometry());
        LocalDateTime tripEnd = tripBegin.plusSeconds(roadDuration);
        log.info("Single trip supplied");
        return TripRecordEntity.builder()
                .fromAddressEntity(fromTo.getFirst())
                .toAddressEntity(fromTo.getSecond())
                .tripBeginTime(tripBegin)
                .tripEndTime(tripEnd)
                .price(DEFAULT_PRICE_PER_MIN * roadDuration / 60)
                .build();
    }

    private Pair<AddressEntity, AddressEntity> supplyAddresses(Pair<LatLng, LatLng> fromToPair, String lang) {
        CompletableFuture<AddressEntity> from = CompletableFuture.supplyAsync(() -> supplyAddress(fromToPair.getFirst(), lang));
        CompletableFuture<AddressEntity> to = CompletableFuture.supplyAsync(() -> supplyAddress(fromToPair.getSecond(), lang));
        return Pair.of(from.join(), to.join());
    }

    private AddressEntity supplyAddress(LatLng latLng, String lang) {
        List<AddressEntity> decoded = googleApiGeoDecoder.decode(latLng, lang);
        return decoded.isEmpty() ? null : decoded.stream().findFirst().get();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOnComplete(TripRecordEntity generatedTrip) {
        tripRecordRepository.save(generatedTrip);
    }
}
