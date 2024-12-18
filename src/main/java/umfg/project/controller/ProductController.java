package umfg.project.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.EstablishmentType;
import umfg.project.entity.Product;
import umfg.project.repository.ProductRepository;
import umfg.project.service.EstablishmentTypeService;
import umfg.project.repository.EstablishmentTypeRepository;
import umfg.project.service.ProductService;
import umfg.project.specification.ProductSpecification;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private EstablishmentTypeRepository establishmentTypeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EstablishmentTypeService establishmentTypeService;

    @Autowired
    private ProductService productService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("establishmentTypes", establishmentTypeService.findAll());
        return "createProduct";
    }


    @PostMapping("/add")
    public String createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Integer stockQuantity,
            @RequestParam List<Long> establishmentTypeIds,
            @RequestParam Double price,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setStockQuantity(stockQuantity);
            product.setPrice(price);

            if (establishmentTypeIds != null && !establishmentTypeIds.isEmpty()) {
                List<EstablishmentType> types = establishmentTypeRepository.findAllById(establishmentTypeIds);
                product.setEstablishmentTypes(types);
            }

            productRepository.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "Produto criado com sucesso!");
            return "redirect:/products/view";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar produto: " + e.getMessage());
            return "redirect:/products/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        model.addAttribute("product", product);
        model.addAttribute("establishmentTypes", establishmentTypeService.findAll());
        return "editProduct";
    }



    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Integer stockQuantity,
            @RequestParam List<Long> establishmentTypeIds,
            @RequestParam Double price,
            RedirectAttributes redirectAttributes
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        product.setName(name);
        product.setDescription(description);
        product.setStockQuantity(stockQuantity);
        product.setPrice(price);

        if (establishmentTypeIds != null && !establishmentTypeIds.isEmpty()) {
            List<EstablishmentType> types = establishmentTypeRepository.findAllById(establishmentTypeIds);
            product.setEstablishmentTypes(types);
        }

        productRepository.save(product);

        redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");
        logger.info("Produto atualizado: {}", product.getName());

        return "redirect:/products/view";
    }


    @GetMapping("/view")
    public String viewProducts(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Long> establishmentTypeIds,
            Model model
    ) {
        if (id != null && id.toString().matches("\\d+")) {
            System.out.println("ID: " + id);
        } else {
            System.out.println("ID inválido ou ausente.");
        }

        List<Product> products = productService.filterProducts(id, name, establishmentTypeIds);

        model.addAttribute("products", products);
        model.addAttribute("establishmentTypes", establishmentTypeService.findAll());
        return "viewProducts"; // HTML p/ exibir a lista de produtos filtrados
    }


    @GetMapping("/delete/confirm/{id}")
    public String confirmDeleteProduct(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
        model.addAttribute("product", product);
        return "deleteProductConfirmation";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produto deletado com sucesso!");
        } catch (DataIntegrityViolationException e) {
            // Mensagem amigável informando que o produto está associado a pedidos
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Não é possível excluir o produto, pois ele está associado a um pedido existente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao deletar produto: " + e.getMessage());
        }
        return "redirect:/products/view";
    }


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

    @GetMapping("/export-pdf")
    public void exportToPDF(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Long> establishmentTypeIds,
            HttpServletResponse response
    ) throws DocumentException, IOException {

        // Filtra os produtos
        List<Product> products = productService.filterProducts(id, name, establishmentTypeIds);

        // Configuração da resposta
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=produtos.pdf");

        // Cria o documento PDF
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        document.add(new Paragraph("Lista de Produtos Filtrados\n\n"));

        // Tabela com 4 colunas
        PdfPTable table = new PdfPTable(4);
        table.addCell("ID");
        table.addCell("Nome");
        table.addCell("Descrição");
        table.addCell("Preço");

        // Adiciona os dados dos produtos filtrados
        for (Product product : products) {
            table.addCell(String.valueOf(product.getId()));
            table.addCell(product.getName());
            table.addCell(product.getDescription());
            table.addCell(String.valueOf(product.getPrice()));
        }

        document.add(table);
        document.close();
    }
}
