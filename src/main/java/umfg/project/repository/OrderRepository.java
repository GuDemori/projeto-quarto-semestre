package umfg.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umfg.project.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {}

