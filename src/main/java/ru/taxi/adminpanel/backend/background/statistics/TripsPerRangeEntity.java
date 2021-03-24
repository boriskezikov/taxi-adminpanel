package ru.taxi.adminpanel.backend.background.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity(name = "trips_in_range")
public class TripsPerRangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trips_in_range_ids_gen")
    @SequenceGenerator(name = "trips_in_range_ids_gen", sequenceName = "trips_in_range_id_seq", allocationSize = 1)
    private BigInteger id;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Long tripsCount;
}
