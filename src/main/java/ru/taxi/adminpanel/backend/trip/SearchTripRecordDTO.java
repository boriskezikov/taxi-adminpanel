package ru.taxi.adminpanel.backend.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchTripRecordDTO {

    private BigInteger id;

    private UUID uuid;

    private LocalDateTime fromTime;

    private LocalDateTime toTime;

    private Double price;

    private PriceComparatorEnum priceComparator;

    private String city;

    private Street streetFrom;

    private Street streetTo;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Street {

        private String streetName;
        private Integer streetNumber;
    }
}
