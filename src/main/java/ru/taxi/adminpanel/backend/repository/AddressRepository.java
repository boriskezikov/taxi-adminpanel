package ru.taxi.adminpanel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.taxi.adminpanel.backend.domain.AddressEntity;

import java.math.BigInteger;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, BigInteger> {
}
