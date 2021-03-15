package ru.taxi.adminpanel.backend.trip;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.taxi.adminpanel.backend.address.AddressEntity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRecordRepository extends JpaRepository<TripRecordEntity, BigInteger> {

    Page<TripRecordEntity> findAllByTripBeginTimeAfterAndTripBeginTimeBefore(LocalDateTime b, LocalDateTime a, Pageable p);
}
