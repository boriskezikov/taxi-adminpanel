package ru.taxi.adminpanel.backend.domain;

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
import java.util.SimpleTimeZone;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity(name = "addresses")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addresses_ids_gen")
    @SequenceGenerator(name = "addresses_ids_gen", sequenceName = "addresses_id_seq", allocationSize = 1)
    private BigInteger id;

    @Builder.Default
    @Column
    private UUID uuid = UUID.randomUUID();

    @Column
    private SimpleTimeZone timeZone;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String streetNumber;

    @Column
    private String zipCode;

    private String formattedAddress;

    @Column(nullable = false)
    private String lng;

    @Column(nullable = false)
    private String lat;

    @Override
    public String toString() {
        return country + "," + city + "," + street + "," + streetNumber + "," + zipCode;
    }

}
