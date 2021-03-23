package ru.taxi.adminpanel.backend.trip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripRecordService {

    private final TripRecordRepository tripRecordRepository;
    private final ModelMapper mapper;

    public void createRecord(TripRecordDTO tripRecordDTO) {
        TripRecordEntity entity = mapper.map(tripRecordDTO, TripRecordEntity.class);
        entity.setUuid(UUID.randomUUID());
        tripRecordRepository.save(entity);
        log.info("Added new record: {} ", entity.getId());
    }

    public void updateRecord(TripRecordEntity source) {
        tripRecordRepository.findById(source.getId())
                .map(record -> {
                    mapper.map(source, record);
                    return record;
                })
                .map(tripRecordRepository::save);
        log.info("Record updated: {} ", source.getId());
    }

    public void delete(BigInteger id) {
        tripRecordRepository.deleteById(id);
        log.info("Record deleted: {} ", id);
    }

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

    public Page<TripRecordEntity> findInRange(LocalDateTime l, LocalDateTime r, Pageable p) {
        return tripRecordRepository.findAllByTripBeginTimeAfterAndTripBeginTimeBefore(l, r, p);
    }

    public Page<TripRecordEntity> findAll(Pageable pageable) {
        return tripRecordRepository.findAll(pageable);
    }

}
