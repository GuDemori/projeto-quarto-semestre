package umfg.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import umfg.project.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsByEstablishmentTypes_Id(Long establishmentTypeId);

    List<Product> findByIdOrNameContainingIgnoreCase(Long id, String name);

    List<Product> findByNameContainingIgnoreCase(String name);
}
