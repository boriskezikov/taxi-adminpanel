package ru.taxi.adminpanel.backend.generator;

import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.address.AddressRepository;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiDistanceMatrix;
import ru.taxi.adminpanel.backend.geoapi.GoogleApiGeoDecoderGoogle;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.taxi.adminpanel.backend.generator.ArtificialDataHelper.getRandomPoint;
import static ru.taxi.adminpanel.backend.generator.ArtificialDataHelper.getTime;
import static ru.taxi.adminpanel.backend.utils.Constants.DEFAULT_PRICE_PER_MIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArtificialDataGenerator {

    private final GoogleApiGeoDecoderGoogle googleApiGeoDecoder;
    private final GoogleApiDistanceMatrix roadRetriever;
    private final TripRecordRepository tripRecordRepository;
    private final AddressRepository addressRepository;


    @Async
    @SneakyThrows
    public void generate(GeneratorParametersEntity gParams) {
        if (gParams.isClean()) {
            tripRecordRepository.deleteAll();
            addressRepository.deleteAll();
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        final int days = (int) gParams.getTripsDateLeftBorder().until(gParams.getTripsDateRightBorder(), ChronoUnit.DAYS);
        List<LocalDate> dateRange = Stream.iterate(gParams.getTripsDateLeftBorder(), d -> d.plusDays(1))
                .limit(days).collect(Collectors.toList());
        List<Callable<Boolean>> tasks = dateRange.stream().<Callable<Boolean>>map(date -> () -> generate(gParams, date))
                .collect(Collectors.toList());
        List<Future<Boolean>> results = executorService.invokeAll(tasks);
        log.info(results.toString());
    }

    public boolean generate(GeneratorParametersEntity gParams, LocalDate date) {
        IntStream.range(0, gParams.getOrdersPerDayNumber()).forEach(i -> generateTrip(gParams, date));
        return true;
    }

    private void generateTrip(GeneratorParametersEntity gParams, LocalDate date) {
        try {
            Pair<AddressEntity, AddressEntity> fromTo = supplyAddresses(supplyMapPoints(gParams), gParams.getLanguage());
            TripRecordEntity trip = supplySingleTrip(fromTo, date);
            saveOnComplete(trip);
            log.info("Успешная генерация");
        } catch (Exception ignored) {
            log.info("Не удалось сгенерировать");
        }
    }

    private Pair<LatLng, LatLng> supplyMapPoints(GeneratorParametersEntity gParams) {
        return Pair.of(getRandomPoint(gParams), getRandomPoint(gParams));
    }

    private TripRecordEntity supplySingleTrip(Pair<AddressEntity, AddressEntity> fromTo, LocalDate date) {
        LocalDateTime tripBegin = LocalDateTime.of(date, getTime());
//        long roadDuration = roadRetriever.findRoadDuration(fromTo.getFirst().getGeometry(), fromTo.getSecond().getGeometry());
        long roadDuration = ThreadLocalRandom.current().nextLong(1000, 5000);
        LocalDateTime tripEnd = tripBegin.plusSeconds(roadDuration);
        return TripRecordEntity.builder()
                .fromAddressEntity(fromTo.getFirst())
                .toAddressEntity(fromTo.getSecond())
                .tripBeginTime(tripBegin)
                .tripEndTime(tripEnd)
                .price(DEFAULT_PRICE_PER_MIN * roadDuration / 60)
                .build();
    }


//    private Pair<AddressEntity, AddressEntity> supplyAddresses(Pair<LatLng, LatLng> fromToPair, String lang) {
//        CompletableFuture<AddressEntity> from = CompletableFuture.supplyAsync(() -> supplyAddress(fromToPair.getFirst(), lang));
//        CompletableFuture<AddressEntity> to = CompletableFuture.supplyAsync(() -> supplyAddress(fromToPair.getSecond(), lang));
//        return Pair.of(from.join(), to.join());
//    }

    private Pair<AddressEntity, AddressEntity> supplyAddresses(Pair<LatLng, LatLng> fromToPair, String lang) {
        AddressEntity from = supplyAddress(fromToPair.getFirst(), lang);
        AddressEntity to = supplyAddress(fromToPair.getSecond(), lang);
        return Pair.of(from, to);
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
