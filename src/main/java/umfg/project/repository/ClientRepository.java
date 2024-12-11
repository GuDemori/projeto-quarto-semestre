package umfg.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umfg.project.entity.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {}
