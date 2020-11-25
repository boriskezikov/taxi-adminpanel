package ru.taxi.adminpanel.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.taxi.adminpanel.backend.domain.TripRecord;
import ru.taxi.adminpanel.backend.dto.RecordDTO;
import ru.taxi.adminpanel.backend.jpa.RecordRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

    public void createRecord(RecordDTO recordDTO) {
        TripRecord tripRecord = TripRecord.builder()
                .tripBeginTime(recordDTO.getBegin())
                .tripEndTime(recordDTO.getEnd())
                .price(recordDTO.getPrice())
                .fromAddress(recordDTO.getFrom())
                .toAddress(recordDTO.getTo())
                .uuid(UUID.randomUUID()).build();
        tripRecord = recordRepository.save(tripRecord);
        log.info("Added new record: {} ", tripRecord.getUuid());
    }

    public List<TripRecord> findAll(){
        return (List<TripRecord>) recordRepository.findAll();
    }


    @PostConstruct
    public void initTestData(){
        EasyRandom gen = new EasyRandom();
        gen.objects(RecordDTO.class, 20).forEach(this::createRecord);
    }
}
