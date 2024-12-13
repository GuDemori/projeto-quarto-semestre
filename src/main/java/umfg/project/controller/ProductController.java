package umfg.project.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.Product;
import umfg.project.repository.ProductRepository;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    // Página para criar produto
    @GetMapping("/create")
    public String showCreateForm() {
        return "createProduct";
    }

    @PostMapping("/add")
    public String createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Integer stockQuantity,
            @RequestParam String establishmentType,
            @RequestParam Double price,
            RedirectAttributes redirectAttributes
    ) {
        try {
            logger.info("Recebendo produto: Nome={}, Descrição={}, Quantidade={}, Tipo={}, Preço={}",
                    name, description, stockQuantity, establishmentType, price);

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setStockQuantity(stockQuantity);
            product.setEstablishmentType(establishmentType);
            product.setPrice(price);

            productRepository.save(product);

            redirectAttributes.addFlashAttribute("successMessage", "Produto criado com sucesso!");
            return "redirect:/user/dashboard";
        } catch (Exception e) {
            logger.error("Erro ao criar produto: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar produto: " + e.getMessage());
            return "redirect:/products/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        model.addAttribute("product", product);
        return "editProduct";
    }


    // Atualizar produto
    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid Product updated,
            RedirectAttributes redirectAttributes
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setStockQuantity(updated.getStockQuantity());
        product.setEstablishmentType(updated.getEstablishmentType());
        product.setPrice(updated.getPrice());

        productRepository.save(product);

        redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");
        logger.info("Produto atualizado: {}", product.getName());

        return "redirect:/products/view";
    }

    // Página para visualizar produtos
    @GetMapping("/view")
    public String viewProducts(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Product> products;
        if (search == null || search.isEmpty()) {
            products = productRepository.findAll();
        } else {
            try {
                Long id = Long.parseLong(search);
                products = productRepository.findByIdOrNameContainingIgnoreCase(id, search);
            } catch (NumberFormatException e) {
                products = productRepository.findByNameContainingIgnoreCase(search);
            }
        }
        model.addAttribute("products", products);
        return "viewProducts";
    }

    // Página de confirmação de exclusão
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        model.addAttribute("product", product);
        return "deleteProductConfirmation";
    }

    // Excluir produto
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produto excluído com sucesso!");
        logger.info("Produto excluído: {}", product.getName());
        return "redirect:/products/view";
    }

    // Página inicial de lista de produtos
    @GetMapping
    public String productList(Model model) {
        try {
            List<Product> products = productRepository.findAll();
            model.addAttribute("products", products);
            logger.info("Produtos carregados: {}", products.size());
            return "productList";
        } catch (Exception e) {
            logger.error("Erro ao carregar a lista de produtos: ", e);
            throw e;
        }
    }
}
