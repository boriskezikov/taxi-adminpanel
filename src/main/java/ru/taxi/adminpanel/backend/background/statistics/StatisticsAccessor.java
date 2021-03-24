package ru.taxi.adminpanel.backend.background.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsAccessor {

    private final StatisticsRepository statisticsRepository;


    public GeneralStatisticsEntity loadStatistics() {
        Optional<GeneralStatisticsEntity> statisticsEntity = statisticsRepository.findById(BigInteger.ONE);
        return statisticsEntity.orElseGet(() -> GeneralStatisticsEntity.builder().build());
    }

}
