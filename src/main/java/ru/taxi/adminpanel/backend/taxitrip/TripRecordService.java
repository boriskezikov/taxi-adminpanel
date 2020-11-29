package ru.taxi.adminpanel.backend.taxitrip;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripRecordService {

    private final TripRecordRepository tripRecordRepository;
    private final TripRecordMapper tripRecordMapper;

    public void createRecord(TripRecordDTO tripRecordDTO) {
        TripRecordEntity entity = tripRecordMapper.fromDto(tripRecordDTO);
        tripRecordRepository.save(entity);
        log.info("Added new record: {} ", entity.getId());
    }

    public void updateRecord(TripRecordEntity source) {
        tripRecordRepository.findById(source.getId())
                .map(record -> {
                    tripRecordMapper.update(source, record);
                    return record; })
                .map(tripRecordRepository::save);
        log.info("Record updated: {} ", source.getId());
    }

    public void delete(BigInteger id){
        tripRecordRepository.deleteById(id);
        log.info("Record deleted: {} ", id);
    }

    public List<TripRecordEntity> findAll() {
        return (List<TripRecordEntity>) tripRecordRepository.findAll();
    }

    @PostConstruct
    public void initTestData() {
        EasyRandom gen = new EasyRandom();
        Faker faker =new Faker();
        gen.objects(TripRecordDTO.class, 3).peek(record->{
            record.setFromAddress(faker.address().fullAddress());
            record.setToAddress(faker.address().fullAddress());
        }).forEach(this::createRecord);
    }
}
