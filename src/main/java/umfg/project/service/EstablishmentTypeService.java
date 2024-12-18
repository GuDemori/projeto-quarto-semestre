package umfg.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umfg.project.entity.EstablishmentType;
import umfg.project.repository.ClientRepository;
import umfg.project.repository.ProductRepository;
import umfg.project.repository.EstablishmentTypeRepository;

import java.util.List;

@Service
public class EstablishmentTypeService {

    @Autowired
    private EstablishmentTypeRepository repository;

    public List<EstablishmentType> findAll() {
        return repository.findAll();
    }

    public EstablishmentType save(EstablishmentType type) {
        return repository.save(type);
    }

    public EstablishmentType findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de estabelecimento não encontrado"));
    }

    public List<EstablishmentType> findAllByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Autowired
    private ClientRepository clientRepository; // Repositório dos clientes
    @Autowired
    private ProductRepository productRepository; // Repositório dos produtos
    @Autowired
    private EstablishmentTypeRepository establishmentTypeRepository;

    public void delete(Long id) {
        // Verifica se existem clientes associados ao tipo de estabelecimento
        if (clientRepository.existsByEstablishmentTypes_Id(id)) {
            throw new IllegalStateException("Não é possível excluir: existem clientes associados a este tipo de estabelecimento.");
        }

        // Verifica se existem produtos associados ao tipo de estabelecimento
        if (productRepository.existsByEstablishmentTypes_Id(id)) {
            throw new IllegalStateException("Não é possível excluir: existem produtos associados a este tipo de estabelecimento.");
        }

        // Se não houver associações, realiza a exclusão
        establishmentTypeRepository.deleteById(id);
    }

}
