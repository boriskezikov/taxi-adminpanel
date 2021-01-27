package ru.taxi.adminpanel.backend.taxitrip;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface TripRecordRepository extends CrudRepository<TripRecordEntity, BigInteger> {

}
