package umfg.project.specification;

import org.springframework.data.jpa.domain.Specification;
import umfg.project.entity.Order;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> filterBy(
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Long clientId
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (city != null && !city.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("client").get("city")), "%" + city.toLowerCase() + "%"));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), startDate.atStartOfDay()));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), endDate.atTime(23, 59, 59)));
            }

            if (clientId != null) {
                predicates.add(cb.equal(root.get("client").get("id"), clientId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
