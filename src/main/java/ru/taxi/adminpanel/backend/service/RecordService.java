package ru.taxi.adminpanel.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.taxi.adminpanel.backend.domain.TripRecord;
import ru.taxi.adminpanel.backend.dto.RecordDTO;
import ru.taxi.adminpanel.backend.jpa.RecordRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

    public TripRecord createRecord(RecordDTO recordDTO) {
        TripRecord tripRecord = TripRecord.builder()
                .begin(recordDTO.getBegin())
                .end(recordDTO.getEnd())
                .price(recordDTO.getPrice())
                .from(recordDTO.getFrom())
                .to(recordDTO.getTo()).build();
        tripRecord = recordRepository.save(tripRecord);
        return tripRecord;
    }

    public List<TripRecord> findAll(){
        return (List<TripRecord>) recordRepository.findAll();
    }
}
