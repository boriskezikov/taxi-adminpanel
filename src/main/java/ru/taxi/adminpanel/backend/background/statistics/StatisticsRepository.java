package ru.taxi.adminpanel.backend.background.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface StatisticsRepository extends JpaRepository<GeneralStatisticsEntity, BigInteger> {
}
