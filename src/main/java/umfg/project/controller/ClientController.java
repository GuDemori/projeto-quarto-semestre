package umfg.project.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umfg.project.entity.Client;
import umfg.project.repository.ClientRepository;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    ClientRepository clientRepository;

    @PostMapping
    public Client createClient(@Valid @RequestBody Client client) {
        return clientRepository.save(client);
    }

    @GetMapping
    public List<Client> list() {
        return clientRepository.findAll();
    }

    @GetMapping("/{clientId}")
    public Client getOneClient(@PathVariable Long clientId) {
        logger.info("Busca por cliente específico realizada");
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    @PutMapping("/{clientId}")
    public Client updateClient(@PathVariable Long clientId, @Valid @RequestBody Client updated) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setEstablishmentName(updated.getEstablishmentName());
        client.setCity(updated.getCity());
        client.setAddress(updated.getAddress());
        client.setEstablishmentType(updated.getEstablishmentType());

        return clientRepository.save(client);
    }

    @DeleteMapping("/{clientId}")
    public void deleteClient(@PathVariable Long clientId) {
        clientRepository.deleteById(clientId);
    }
}