package ru.taxi.adminpanel.backend.background.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity(name = "statistics")
public class GeneralStatisticsEntity {

    @Id
    private BigInteger id;

    @Column(nullable = false)
    private Long addressesCount;

    @Column(nullable = false)
    private String mostPopularZip;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripsPerRangeEntity> tripsPerRangeEntities;

    @Column(length = 10000, nullable = false)
    private String citiesInArea;

    @Column(nullable = false)
    private Long tripsTotal;
}
