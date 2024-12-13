package umfg.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.Client;
import umfg.project.repository.ClientRepository;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/view")
    public String viewClients(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "viewClients";
    }

    @GetMapping("/create")
    public String createClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "createClient";
    }

    @GetMapping("/edit/{id}")
    public String editClientForm(@PathVariable Long id, Model model) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", client);
        return "editClient";
    }

    @PostMapping("/create")
    public String createClient(
            @Valid @ModelAttribute Client client,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar cliente. Verifique os dados preenchidos.");
            return "redirect:/clients/create";
        }
        clientRepository.save(client);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente criado com sucesso!");
        return "redirect:/user/dashboard";
    }

    @PostMapping("/edit/{id}")
    public String updateClient(
            @PathVariable Long id,
            @Valid @ModelAttribute Client client,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar cliente. Verifique os dados preenchidos.");
            return "redirect:/clients/edit/" + id;
        }
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        existingClient.setEstablishmentName(client.getEstablishmentName());
        existingClient.setCity(client.getCity());
        existingClient.setAddress(client.getAddress());
        existingClient.setEstablishmentType(client.getEstablishmentType());
        clientRepository.save(existingClient);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente atualizado com sucesso!");
        return "redirect:/clients/view";
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
