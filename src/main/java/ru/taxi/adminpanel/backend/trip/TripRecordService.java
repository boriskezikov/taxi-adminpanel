package ru.taxi.adminpanel.backend.trip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.taxi.adminpanel.backend.trip.repository.TripRecordRepository;
import ru.taxi.adminpanel.backend.trip.repository.TripRecordRepositoryCustom;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripRecordService {

    private final TripRecordRepository tripRecordRepository;
    private final TripRecordRepositoryCustom tripRecordRepositoryCustom;

    @Async
    public CompletableFuture<List<TripRecordEntity>> findAllAsync() {
        return CompletableFuture.completedFuture(tripRecordRepository.findAll());
    }

    public List<TripRecordEntity> findAll() {
        return tripRecordRepository.findAll();
    }

    public Optional<TripRecordEntity> findById(BigInteger id) {
        return tripRecordRepository.findById(id);
    }

    public List<TripRecordEntity> findInRange(LocalDateTime l, LocalDateTime r) {
        return tripRecordRepository.findAllByTripBeginTimeAfterAndTripBeginTimeBefore(l, r);
    }

    public Page<TripRecordEntity> findAll(Pageable pageable) {
        return tripRecordRepository.findAll(pageable);
    }

    public List<TripRecordEntity> searchRecords(SearchTripRecordDTO searchTripRecordDTO) {
        log.info("search() - Start fetching with params {}", searchTripRecordDTO);
        List<TripRecordEntity> tripRecordEntities = tripRecordRepositoryCustom.find(searchTripRecordDTO);
        log.info("search() - {} records found", tripRecordEntities.size());
        return tripRecordEntities;
    }

}
