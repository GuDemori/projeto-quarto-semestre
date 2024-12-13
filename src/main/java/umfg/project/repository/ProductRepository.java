package umfg.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umfg.project.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Busca por ID ou parte do nome, ignorando maiúsculas e minúsculas
    List<Product> findByIdOrNameContainingIgnoreCase(Long id, String name);

    // Busca apenas por parte do nome, ignorando maiúsculas e minúsculas
    List<Product> findByNameContainingIgnoreCase(String name);
}
