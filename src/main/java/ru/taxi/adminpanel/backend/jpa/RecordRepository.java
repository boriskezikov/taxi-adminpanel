package ru.taxi.adminpanel.backend.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.taxi.adminpanel.backend.domain.TripRecord;

import java.math.BigInteger;

@Repository
public interface RecordRepository extends CrudRepository<TripRecord, BigInteger> {
}
