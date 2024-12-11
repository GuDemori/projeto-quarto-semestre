package umfg.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umfg.project.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {}
