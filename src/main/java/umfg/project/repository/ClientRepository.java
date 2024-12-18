package umfg.project.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umfg.project.entity.Client;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client>{

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.establishmentTypes")
    List<Client> findAllWithEstablishmentTypes();

    @EntityGraph(attributePaths = "establishmentTypes")
    List<Client> findAll();

    boolean existsByEstablishmentTypes_Id(Long establishmentTypeId);
    List<Client> findByEstablishmentNameContaining(String establishmentName);
}

