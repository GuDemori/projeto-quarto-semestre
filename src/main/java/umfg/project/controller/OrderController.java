package umfg.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.Order;
import umfg.project.entity.Product;
import umfg.project.repository.ClientRepository;
import umfg.project.repository.OrderRepository;
import umfg.project.repository.ProductRepository;
import umfg.project.repository.UserRepository;
import umfg.project.service.OrderService;
import umfg.project.service.ClientService;
import umfg.project.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProductService productService;

    @GetMapping("/orders/create")
    public String showOrderForm(Model model) {

        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("statuses", List.of("PENDENTE", "CONFIRMADO", "CANCELADO"));
        return "createOrder";
    }

    @PostMapping("/create")
    public String createOrder(
            @Valid @ModelAttribute Order order,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar pedido. Verifique os dados preenchidos.");
            return "redirect:/orders/create";
        }

        try {
            orderService.concludeOrder(order);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido criado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/orders/create";
        }

        return "redirect:/orders/view";
    }

    @GetMapping("/create")
    public String createOrderForm(Model model, RedirectAttributes redirectAttributes) {
        var products = productRepository.findAll();
        var clients = clientRepository.findAll();

        if (products.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não existem produtos cadastrados no sistema. Cadastre um produto antes de criar pedidos.");
            return "redirect:/products/create"; // Redireciona para a página de criação de produtos
        }

        if (clients.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não existem clientes cadastrados no sistema. Cadastre um cliente antes de criar pedidos.");
            return "redirect:/clients/create"; // Redireciona para a página de criação de clientes
        }

        model.addAttribute("order", new Order());
        model.addAttribute("clients", clients);
        model.addAttribute("products", products);

        return "createOrder"; // Nome do template
    }



    @GetMapping("/view")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "orderView";
    }


    @PutMapping("/{orderId}")
    public String updateOrder(
            @PathVariable Long orderId,
            @Valid @ModelAttribute Order updatedOrder,
            RedirectAttributes redirectAttributes
    ) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if ("Entregue".equals(updatedOrder.getStatus())) {
            orderService.concludeOrder(existingOrder);
        } else {
            existingOrder.setStatus(updatedOrder.getStatus());
            orderRepository.save(existingOrder);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Pedido atualizado com sucesso!");
        return "redirect:/orders/view";
    }
}
