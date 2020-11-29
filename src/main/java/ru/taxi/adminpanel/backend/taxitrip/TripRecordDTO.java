package ru.taxi.adminpanel.backend.taxitrip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripRecordDTO {

    private String fromAddress;

    private String toAddress;

    private LocalDateTime tripBeginTime;

    private LocalDateTime tripEndTime;

    private Double price;

    private BigInteger id;
}
