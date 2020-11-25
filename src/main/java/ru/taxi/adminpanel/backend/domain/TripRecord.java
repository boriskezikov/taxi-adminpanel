package ru.taxi.adminpanel.backend.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@EqualsAndHashCode(of = "id")
@Entity(name = "records")
@NoArgsConstructor
@AllArgsConstructor
public class TripRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "records_ids_gen")
    @SequenceGenerator(name = "records_ids_gen", sequenceName = "records_id_seq", allocationSize = 1)
    private BigInteger id;

    @Column(nullable = false)
    private String from;

    @Column(nullable = false)
    private String to;

    @Column(nullable = false)
    private LocalDateTime begin;

    @Column(nullable = false)
    private LocalDateTime end;

    @Column(nullable = false)
    private Double price;

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
}
