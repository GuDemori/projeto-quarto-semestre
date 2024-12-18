package umfg.project.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.Client;
import umfg.project.entity.EstablishmentType;
import umfg.project.repository.ClientRepository;
import umfg.project.service.EstablishmentTypeService;
import umfg.project.repository.EstablishmentTypeRepository;
import umfg.project.service.ClientService;
import umfg.project.specification.ClientSpecification;


import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private EstablishmentTypeService establishmentTypeService;

    @Autowired
    private EstablishmentTypeRepository establishmentTypeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @ManyToMany
    @JoinTable(
            name = "client_establishment_type",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "establishment_type_id")
    )
    private List<EstablishmentType> establishmentTypes;


    @GetMapping("/export-pdf")
    public void exportClientsToPDF(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String establishmentName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) List<Long> establishmentTypesIds,
            HttpServletResponse response
    ) throws DocumentException, IOException {

        List<Client> clients = clientService.filterClients(id, establishmentName, city, establishmentTypesIds);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=clientes.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("Lista de Clientes Filtrados\n\n"));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Cabeçalhos da tabela
        table.addCell("ID");
        table.addCell("Nome do Estabelecimento");
        table.addCell("Cidade");
        table.addCell("Endereço");
        table.addCell("Tipos de Estabelecimento");

        for (Client client : clients) {
            table.addCell(String.valueOf(client.getId()));
            table.addCell(client.getEstablishmentName());
            table.addCell(client.getCity());
            table.addCell(client.getAddress());

            String establishmentTypes = client.getEstablishmentTypes()
                    .stream()
                    .map(EstablishmentType::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Não definido");

            table.addCell(establishmentTypes);
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/create")
    public String showCreateClientForm(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("establishmentTypes", establishmentTypeService.findAll());
        return "createClient"; // Nome do HTML
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        List<EstablishmentType> establishmentTypes = establishmentTypeRepository.findAll();

        model.addAttribute("client", client);
        model.addAttribute("establishmentTypes", establishmentTypes);

        return "editClient"; // Template correto
    }


    @PostMapping("/create")
    public String createClient(
            @RequestParam String establishmentName,
            @RequestParam String city,
            @RequestParam String address,
            @RequestParam(required = false) List<Long> establishmentTypeIds,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Cria um novo cliente
            Client client = new Client();
            client.setEstablishmentName(establishmentName);
            client.setCity(city);
            client.setAddress(address);

            // Garante que os EstablishmentTypes estão carregados e gerenciados
            if (establishmentTypeIds != null && !establishmentTypeIds.isEmpty()) {
                List<EstablishmentType> types = establishmentTypeRepository.findAllById(establishmentTypeIds);
                client.setEstablishmentTypes(types);
            }

            // Salva o cliente
            clientRepository.save(client);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente criado com sucesso!");

            return "redirect:/clients/view";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar cliente: " + e.getMessage());
            return "redirect:/clients/create";
        }
    }


    @GetMapping("/view")
    public String viewClients(
            Model model,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String establishmentName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) List<Long> establishmentTypeIds
    ) {
        // Log para verificar parâmetros recebidos
        logger.debug("Filtros - ID: {}, Nome: {}, Cidade: {}, Tipos: {}", id, establishmentName, city, establishmentTypeIds);

        // Buscar clientes com base nos filtros
        List<Client> clients = clientService.filterClients(id, establishmentName, city, establishmentTypeIds);

        // Buscar os tipos de estabelecimento
        List<EstablishmentType> establishmentTypes = establishmentTypeRepository.findAll();

        // Adicionar os dados ao modelo
        model.addAttribute("clients", clients);
        model.addAttribute("establishmentTypes", establishmentTypes);

        return "viewClients";
    }


    @PostMapping("/edit/{id}")
    public String updateClient(
            @PathVariable Long id,
            @RequestParam String establishmentName,
            @RequestParam String city,
            @RequestParam String address,
            @RequestParam(required = false) List<Long> establishmentTypeIds,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Busca o cliente existente
            Client existingClient = clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

            // Atualiza os atributos básicos
            existingClient.setEstablishmentName(establishmentName);
            existingClient.setCity(city);
            existingClient.setAddress(address);

            // Carrega os EstablishmentTypes e atualiza
            if (establishmentTypeIds != null && !establishmentTypeIds.isEmpty()) {
                List<EstablishmentType> types = establishmentTypeRepository.findAllById(establishmentTypeIds);
                existingClient.setEstablishmentTypes(types);
            } else {
                existingClient.setEstablishmentTypes(null); // Remove associações se nenhum tipo for selecionado
            }

            // Salva o cliente
            clientRepository.save(existingClient);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente atualizado com sucesso!");

            return "redirect:/clients/view";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar cliente: " + e.getMessage());
            return "redirect:/clients/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clientRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente excluído com sucesso!");
        return "redirect:/clients/view";
    }

    @GetMapping("/delete/confirm/{id}")
    public String deleteClientConfirmation(@PathVariable Long id, Model model) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", client);
        return "deleteClientConfirmation";
    }


}
