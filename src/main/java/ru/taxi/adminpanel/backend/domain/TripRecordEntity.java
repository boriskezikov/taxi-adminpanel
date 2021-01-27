package ru.taxi.adminpanel.backend.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class TripRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "records_ids_gen")
    @SequenceGenerator(name = "records_ids_gen", sequenceName = "records_id_seq", allocationSize = 1)
    private BigInteger id;

    @JoinColumn(name = "address_from_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AddressEntity fromAddressEntity;

    @JoinColumn(name = "address_to_id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AddressEntity toAddressEntity;

    @Column(nullable = false)
    private LocalDateTime tripBeginTime;

    @Column(nullable = false)
    private LocalDateTime tripEndTime;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private UUID uuid;

    @UpdateTimestamp
    private LocalDateTime updated;
}
