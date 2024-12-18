package umfg.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import umfg.project.entity.Client;
import umfg.project.entity.EstablishmentType;
import umfg.project.repository.ClientRepository;
import umfg.project.repository.EstablishmentTypeRepository;
import umfg.project.specification.ClientSpecification;

import java.util.List;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EstablishmentTypeRepository establishmentTypeRepository;



    public List<Client> findAll() {
        return clientRepository.findAllWithEstablishmentTypes();
    }

    public List<Client> filterClients(Long id, String establishmentName, String city, List<Long> establishmentTypeIds) {
        Specification<Client> spec = ClientSpecification.filterBy(id, establishmentName, city, establishmentTypeIds);
        return clientRepository.findAll(spec);
    }


    /**
     * Retorna todos os tipos de estabelecimento.
     */
    public List<EstablishmentType> findAllEstablishmentTypes() {
        return establishmentTypeRepository.findAll();
    }

}
