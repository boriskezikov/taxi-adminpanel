package ru.taxi.adminpanel.backend.background;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.taxi.adminpanel.backend.background.statistics.GeneralStatisticsEngine;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobRunner {

    private final GeneralStatisticsEngine statisticsEngine;

    @Scheduled(fixedDelay = 3600000)
    public void runStatisticsJob() {
        log.info("[JOB] statistics() - Statistics collector started");
        statisticsEngine.calculateGeneralStatistics();
        log.info("[JOB] statistics() - Statistics collector finished");
    }
}
