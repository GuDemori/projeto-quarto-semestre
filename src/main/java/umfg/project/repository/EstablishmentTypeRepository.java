package umfg.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umfg.project.entity.EstablishmentType;

public interface EstablishmentTypeRepository extends JpaRepository<EstablishmentType, Long> {
}
