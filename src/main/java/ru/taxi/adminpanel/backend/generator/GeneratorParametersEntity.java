package ru.taxi.adminpanel.backend.generator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneratorParametersEntity {

    private BigInteger id;
    private double lat;
    private double lng;
    private int rad;
    private int ordersPerDayNumber;
    private String city;
    private LocalDate tripsDateLeftBorder;
    private LocalDate tripsDateRightBorder;
    private String language;
    private String predictorUrl;
    private boolean clean;
}
