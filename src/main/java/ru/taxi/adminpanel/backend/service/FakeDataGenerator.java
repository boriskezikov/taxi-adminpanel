package ru.taxi.adminpanel.backend.service;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.stereotype.Component;
import ru.taxi.adminpanel.backend.domain.AddressEntity;
import ru.taxi.adminpanel.backend.domain.TripRecordEntity;
import ru.taxi.adminpanel.backend.repository.AddressRepository;
import ru.taxi.adminpanel.backend.repository.TripRecordRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FakeDataGenerator {

    private final TripRecordService tripRecordService;
    private final TripRecordRepository tripRecordRepository;
    private final AddressRepository addressRepository;

    private static final int ADDRESSES_COUNT = 200;
    private static final int TRIPS_COUNT = 200;

    @PostConstruct
    public void initTestData() {

        EasyRandom gen = new EasyRandom();

        //generate addresses
        List<AddressEntity> addressEntities = gen.objects(AddressEntity.class, ADDRESSES_COUNT)
                .peek(address -> {
                    Faker faker = new Faker();
                    var adr = faker.address();
                    address.setCity(adr.city());
                    address.setCountry(adr.country());
                    address.setZipCode(adr.zipCode());
                    address.setStreet(adr.streetName());
                    address.setLatitude(adr.latitude());
                    address.setLongitude(adr.longitude());
                })
                .collect(Collectors.toList());
        addressRepository.saveAll(addressEntities);
        log.info("{} addresses generated", ADDRESSES_COUNT);

        List<TripRecordEntity> trips = gen.objects(TripRecordEntity.class, TRIPS_COUNT).peek(record -> {
            Random r = new Random();
            record.setFromAddressEntity(addressEntities.get(r.nextInt(addressEntities.size())));
            record.setToAddressEntity(addressEntities.get(r.nextInt(addressEntities.size())));
            record.setPrice(0.10 + (320.01 - 0.10) * r.nextDouble());
        }).collect(Collectors.toList());
        tripRecordRepository.saveAll(trips);
        log.info("{} trips generated", TRIPS_COUNT);

    }
}
