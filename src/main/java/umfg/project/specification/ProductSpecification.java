package umfg.project.specification;

import org.springframework.data.jpa.domain.Specification;
import umfg.project.entity.Product;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterBy(
            Long id,
            String name,
            List<Long> establishmentTypeIds
    ) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (establishmentTypeIds != null && !establishmentTypeIds.isEmpty()) {
                predicates.add(root.join("establishmentTypes").get("id").in(establishmentTypeIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}