package ru.taxi.adminpanel.backend.generator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "generatorParams")
public class GeneratorParametersEntity {

    @Id
    private BigInteger id;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column(nullable = false)
    private int rad;

    @Column(nullable = false)
    private int ordersNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String predictorUrl;

    @Column
    private boolean clean;

}
