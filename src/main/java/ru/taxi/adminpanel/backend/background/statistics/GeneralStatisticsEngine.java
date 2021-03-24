package ru.taxi.adminpanel.backend.background.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.adminpanel.backend.address.AddressEntity;
import ru.taxi.adminpanel.backend.address.AddressRepository;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;
import ru.taxi.adminpanel.backend.trip.TripRecordRepository;

import java.math.BigInteger;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeneralStatisticsEngine {

    private final AddressRepository addressRepository;
    private final TripRecordRepository tripsRepository;
    private final StatisticsRepository statisticsRepository;
    private static final int TRIPS_RANGE_CONST = 30;


    public void calculateGeneralStatistics() {
        List<TripRecordEntity> all = tripsRepository.findAll();
        String zip = calculateMostPopularZip(all);
        String cities = calculateCitiesConnected(all);
        Long addressesCount = calculateAddressesCount();
        Long tripsTotal = calculateTripsTotal(all);
        List<TripsPerRangeEntity> tripsPerRange = calculateTripsPerRange(all);

        GeneralStatisticsEntity generalStatistics = new GeneralStatisticsEntity();
        generalStatistics.setAddressesCount(addressesCount);
        generalStatistics.setMostPopularZip(zip);
        generalStatistics.setTripsPerRangeEntities(tripsPerRange);
        generalStatistics.setCitiesInArea(cities);
        generalStatistics.setTripsTotal(tripsTotal);
        generalStatistics.setId(BigInteger.ONE);
        refresh(generalStatistics);
    }

    protected void refresh(GeneralStatisticsEntity generalStatistics) {
        statisticsRepository.deleteAll();
        statisticsRepository.save(generalStatistics);
    }


    private String calculateMostPopularZip(List<TripRecordEntity> trips) {
        var areas = trips.parallelStream().map(t -> t.getFromAddressEntity().getZipCode()).collect(Collectors.groupingBy(s -> Objects.requireNonNullElse(s, "NO ZIPCODE"), Collectors.counting()));
        String mostPopularZip = Collections.max(areas.entrySet(), Map.Entry.comparingByValue()).getKey();
        log.info("[JOB] statistics() - Most popular zip: {}", mostPopularZip);
        return mostPopularZip;
    }

    private String calculateCitiesConnected(List<TripRecordEntity> trips) {
        Set<String> citiesInTheArea = trips.stream().map(TripRecordEntity::getFromAddressEntity)
                .map(AddressEntity::getCity)
                .collect(Collectors.toSet());
        log.info("[JOB] statistics() - Cities in the area: {}", citiesInTheArea);
        return StringUtils.join(citiesInTheArea, ";");
    }

    private long calculateAddressesCount() {
        long addr = addressRepository.count();
        log.info("[JOB] statistics() - Addresses count: {}", addr);
        return addr;
    }

    private long calculateTripsTotal(List<TripRecordEntity> trips) {
        return trips.size();
    }

    private List<TripsPerRangeEntity> calculateTripsPerRange(List<TripRecordEntity> trips) {
        Map<Long, Long> rangeToCountMap = trips.stream().map(TripRecordEntity::getTripBeginTime).collect(Collectors.groupingBy(time -> {
            var minutes = time.getMinute();
            var minutesOver = minutes % TRIPS_RANGE_CONST;
            return time.truncatedTo(ChronoUnit.MINUTES).withMinute(minutes - minutesOver).toInstant(ZoneOffset.UTC).toEpochMilli();
        }, Collectors.counting()));
        List<TripsPerRangeEntity> stats = rangeToCountMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> TripsPerRangeEntity.builder()
                .timestamp(entry.getKey())
                .tripsCount(entry.getValue()).build()).collect(Collectors.toList());
        log.info("[JOB] statistics() - Trips per {} range calculated", TRIPS_RANGE_CONST);
        return stats;
    }
}
