package umfg.project.specification;

import org.springframework.data.jpa.domain.Specification;
import umfg.project.entity.Client;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;


public class ClientSpecification {

    public static Specification<Client> filterBy(
            Long id,
            String establishmentName,
            String city,
            List<Long> establishmentTypeIds
    ) {
        return (Root<Client> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (establishmentName != null && !establishmentName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("establishmentName")), "%" + establishmentName.toLowerCase() + "%"));
            }

            if (city != null && !city.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            }

            if (establishmentTypeIds != null && !establishmentTypeIds.isEmpty()) {
                predicates.add(root.join("establishmentTypes").get("id").in(establishmentTypeIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

