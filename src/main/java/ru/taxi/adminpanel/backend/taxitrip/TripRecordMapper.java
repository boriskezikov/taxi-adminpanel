package ru.taxi.adminpanel.backend.taxitrip;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper
public interface TripRecordMapper {

    TripRecordEntity map(TripRecordDTO dto);

    default TripRecordEntity fromDto(TripRecordDTO dto) {
        TripRecordEntity entity = map(dto);
        entity.setUuid(UUID.randomUUID());
        return entity;
    }

    void update(TripRecordEntity dto, @MappingTarget TripRecordEntity entity);
}
