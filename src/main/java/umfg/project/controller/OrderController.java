package umfg.project.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.Order;
import umfg.project.entity.Product;
import umfg.project.repository.OrderRepository;
import umfg.project.repository.ProductRepository;
import umfg.project.service.ClientService;
import umfg.project.service.OrderService;
import umfg.project.service.ProductService;
import umfg.project.specification.ClientSpecification;
import umfg.project.specification.ProductSpecification;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    private ClientSpecification OrderSpecification;

    @PostMapping("/create")
    public String createOrder(
            @RequestParam("products") List<Long> productIds,
            @RequestParam("quantities") List<Integer> quantities,
            @RequestParam("clientId") Long clientId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            for (int i = 0; i < productIds.size(); i++) {
                Product product = productService.findById(productIds.get(i));

                if (quantities.get(i) > product.getStockQuantity()) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Quantity of product " + product.getName() +
                                    " exceeds available stock (" + product.getStockQuantity() + ").");
                    return "redirect:/orders/create";
                }
            }

            for (int i = 0; i < productIds.size(); i++) {
                Product product = productService.findById(productIds.get(i));

                product.setStockQuantity(product.getStockQuantity() - quantities.get(i));
                productRepository.save(product);
            }

            orderService.saveOrder(productIds, quantities, clientId, status, 1L);

            redirectAttributes.addFlashAttribute("successMessage", "Order created successfully!");
            return "redirect:/orders/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating order: " + e.getMessage());
            return "redirect:/orders/create";
        }
    }

    @GetMapping("/create")
    public String showCreateOrderPage(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Long> establishmentTypeIds,
            Model model
    ) {
        if (orderId != null) {
            var existingOrder = orderService.findOrderById(orderId);
            if (existingOrder != null) {
                model.addAttribute("editingOrder", existingOrder); // thymeleaf checa se é edição
            }
        }
        var spec = ProductSpecification.filterBy(null, name, establishmentTypeIds);
        var products = productRepository.findAll(spec);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("products", products);
        model.addAttribute("establishmentTypes", clientService.findAllEstablishmentTypes());
        return "selectProducts";
    }

    @PostMapping("/preview-products")
    public String previewSelectedProducts(
            @RequestParam("products") List<Long> productIds,
            @RequestParam("clientId") Long clientId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            List<Product> selectedProducts = productService.findByIds(productIds);

            double totalValue = selectedProducts.stream()
                    .mapToDouble(Product::getPrice)
                    .sum();

            model.addAttribute("products", selectedProducts);
            model.addAttribute("clientId", clientId);
            model.addAttribute("status", status);
            model.addAttribute("totalValue", totalValue);

            return "previewProducts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading selected products.");
            return "redirect:/orders/create";
        }
    }

    @PostMapping("/finalize")
    public String finalizeOrder(
            @RequestParam("products") List<Long> productIds,
            @RequestParam("quantities") List<Integer> quantities,
            @RequestParam("clientId") Long clientId,
            @RequestParam("status") String status,
            @RequestParam(required = false) Long orderId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (orderId != null) {
                orderService.updateOrderItems(orderId, productIds, quantities, clientId, status);
                redirectAttributes.addFlashAttribute("successMessage", "Pedido atualizado com sucesso!");
            } else {
                orderService.saveOrder(productIds, quantities, clientId, status, 1L);
                // userId fixo, por ex
                redirectAttributes.addFlashAttribute("successMessage", "Pedido criado com sucesso!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar pedido: " + e.getMessage());
            return "redirect:/orders/create";
        }
        return "redirect:/orders/list";
    }

    @GetMapping("/list")
    public String listOrders(Model model) {
        var orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "orderList";
    }

    @GetMapping("/view")
    public String viewOrder(
            Model model,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long clientId
    ) {
        var orders = orderService.filterOrders(city, startDate, endDate, clientId);
        model.addAttribute("orders", orders);
        model.addAttribute("clients", clientService.findAll()); // Carrega lista de clientes
        return "orderList";
    }

    @GetMapping("/edit-via-creation/{id}")
    public String editOrderViaCreationFlow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var order = orderService.findOrderById(id);
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/orders/list";
        }
        return "redirect:/orders/create?orderId=" + id;
    }

    @GetMapping("/view/{id}")
    public String viewOrderById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Order order = orderService.findOrderById(id);

        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/orders/list";
        }

        model.addAttribute("order", order);
        return "viewOrder";
    }

    @GetMapping("/edit/{id}")
    public String editOrderForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Order order = orderService.findOrderById(id);
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/orders/list";
        }
        model.addAttribute("order", order);
        return "editOrder"; // Template com o form
    }

    @PostMapping("/edit/{id}")
    public String editOrder(
            @PathVariable Long id,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            // Busca o pedido existente
            Order order = orderService.findOrderById(id);
            if (order == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
                return "redirect:/orders/list";
            }

            // Atualiza o status
            order.setStatus(status);
            orderService.updateOrder(id, order);

            redirectAttributes.addFlashAttribute("successMessage", "Status do pedido atualizado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar o pedido: " + e.getMessage());
        }
        return "redirect:/orders/list";
    }



    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrderById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido excluído com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir pedido: " + e.getMessage());
        }
        return "redirect:/orders/list";
    }

    @GetMapping("/export-pdf")
    public void exportOrdersToPDF(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long clientId,
            HttpServletResponse response
    ) throws DocumentException, IOException {

        // Buscar pedidos com base nos filtros
        List<Order> orders = orderService.filterOrders(city, startDate, endDate, clientId);

        // Configurar resposta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Cabeçalho do PDF
        document.add(new Paragraph("Lista de Pedidos Filtrados\n\n"));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Adicionar cabeçalhos
        table.addCell("ID");
        table.addCell("Cliente");
        table.addCell("Data");
        table.addCell("Total");
        table.addCell("Status");

        // Preencher tabela
        for (Order order : orders) {
            table.addCell(String.valueOf(order.getId()));
            table.addCell(order.getClient().getEstablishmentName());
            table.addCell(order.getOrderDate().toString());
            table.addCell("R$ " + order.getTotalValue());
            table.addCell(order.getStatus());
        }

        document.add(table);
        document.close();
    }

}