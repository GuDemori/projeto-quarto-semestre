package umfg.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.EstablishmentType;
import umfg.project.service.EstablishmentTypeService;


@Controller
@RequestMapping("/establishment-types")
public class EstablishmentTypeController {

    @Autowired
    private EstablishmentTypeService establishmentTypeService;

    @GetMapping("/view")
    public String viewAll(Model model) {
        model.addAttribute("types", establishmentTypeService.findAll());
        return "viewEstablishmentTypes";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("type", new EstablishmentType());
        return "createEstablishmentType";
    }

    @PostMapping("/create")
    public String createEstablishmentType(@ModelAttribute EstablishmentType type, RedirectAttributes redirectAttributes) {
        establishmentTypeService.save(type);
        redirectAttributes.addFlashAttribute("successMessage", "Tipo de estabelecimento criado com sucesso!");
        return "redirect:/user/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        EstablishmentType type = establishmentTypeService.findById(id);
        model.addAttribute("type", type);
        return "editEstablishmentType";
    }


    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute EstablishmentType type, RedirectAttributes redirectAttributes) {
        EstablishmentType existing = establishmentTypeService.findById(id);
        existing.setName(type.getName());
        establishmentTypeService.save(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Tipo de estabelecimento atualizado!");
        return "redirect:/establishment-types/view";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            establishmentTypeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de estabelecimento deletado com sucesso.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/establishment-types/view";
    }

    @GetMapping("/delete/confirm/{id}")
    public String confirmDelete(@PathVariable Long id, Model model) {
        model.addAttribute("establishmentType", establishmentTypeService.findById(id)); // Corrigido o nome
        return "deleteEstablishmentTypeConfirmation";
    }



}
