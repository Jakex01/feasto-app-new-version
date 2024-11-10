package org.restaurant.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.restaurant.model.RestaurantEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSpecification {

    public static Specification<RestaurantEntity> columnEqual(String foodType, Double rating, Integer priceRange) {
        return (Root<RestaurantEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!foodType.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("foodType"), foodType));
            }
            System.out.println("Jestem tutaj");

            if(rating != null && rating > 0)
            {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), rating));
            }

            System.out.println("tu mnie nie ma");


            if (priceRange !=null && priceRange > 0) {
                predicates.add(criteriaBuilder.between(root.get("prices"), priceRange, priceRange+20));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
