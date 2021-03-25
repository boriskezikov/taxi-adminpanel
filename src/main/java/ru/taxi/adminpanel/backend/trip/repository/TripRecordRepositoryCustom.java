package ru.taxi.adminpanel.backend.trip.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.taxi.adminpanel.backend.trip.SearchTripRecordDTO;
import ru.taxi.adminpanel.backend.trip.TripRecordEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class TripRecordRepositoryCustom {

    private final EntityManager entityManager;

    public List<TripRecordEntity> find(@NonNull SearchTripRecordDTO criteria) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TripRecordEntity> query = criteriaBuilder.createQuery(TripRecordEntity.class);
        Root<TripRecordEntity> root = query.from(TripRecordEntity.class);

        if (criteria.getUuid() != null) {
            query.where(criteriaBuilder.equal(root.get("uuid"), criteria.getUuid()));
            return entityManager.createQuery(query).getResultList();
        }

        if (criteria.getId() != null) {
            query.where(criteriaBuilder.equal(root.get("id"), criteria.getId()));
            return entityManager.createQuery(query).getResultList();
        }

        List<Predicate> predicates = new ArrayList<>();

        ofNullable(criteria.getCity())
                .ifPresent(option -> predicates.add(criteriaBuilder.equal(root.get("fromAddressEntity").get("city"), option)));

        /*
         * street from predicate
         */
        ofNullable(criteria.getStreetFrom().getStreetName())
                .ifPresent(option -> predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("fromAddressEntity").get("street")),
                        "%" + option.toUpperCase() + "%")));

        ofNullable(criteria.getStreetFrom().getStreetNumber())
                .ifPresent(option -> predicates.add(criteriaBuilder.equal(root.get("fromAddressEntity").get("streetNumber"), option)));

        /*
         * street to predicate
         */
        ofNullable(criteria.getStreetTo().getStreetName())
                .ifPresent(option -> predicates.add(criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("toAddressEntity").get("street")),
                        "%" + option.toUpperCase() + "%")));

        ofNullable(criteria.getStreetTo().getStreetNumber())
                .ifPresent(option -> predicates.add(criteriaBuilder.equal(root.get("toAddressEntity").get("streetNumber"), option)));

        ofNullable(criteria.getPriceComparator()).ifPresent(priceComparator -> {
            switch (priceComparator) {
                case EQUAL:
                    ofNullable(criteria.getPrice())
                            .ifPresent(option -> predicates.add(criteriaBuilder.equal(root.get("price"), option)));
                    break;
                case LOWER:
                    ofNullable(criteria.getPrice())
                            .ifPresent(option -> predicates.add(criteriaBuilder.lessThan(root.get("price"), option)));
                    break;
                case HIGHER:
                    ofNullable(criteria.getPrice())
                            .ifPresent(option -> predicates.add(criteriaBuilder.greaterThan(root.get("price"), option)));
                    break;
                default:
                    throw new IllegalArgumentException("Price comparator does not belong to PriceComparatorEnum.class");
            }
        });
        ofNullable(criteria.getFromTime())
                .ifPresent(option -> predicates.add(criteriaBuilder.greaterThan(root.get("tripBeginTime"), option)));

        ofNullable(criteria.getToTime())
                .ifPresent(option -> predicates.add(criteriaBuilder.lessThan(root.get("tripEndTime"), option)));


        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

}
