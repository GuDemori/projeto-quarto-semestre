package umfg.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umfg.project.entity.Order;
import umfg.project.entity.Product;
import umfg.project.repository.ClientRepository;
import umfg.project.repository.OrderRepository;
import umfg.project.repository.ProductRepository;
import umfg.project.repository.UserRepository;
import umfg.project.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired OrderRepository orderRepository;
    @Autowired UserRepository userRepository;
    @Autowired ClientRepository clientRepository;
    @Autowired ProductRepository productRepository;
    @Autowired OrderService orderService;

    @PostMapping
    public Order createOrder(@Valid @RequestBody Order order) {
        userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        clientRepository.findById(order.getClient().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        for (Product product : order.getProducts()) {
            productRepository.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        }
        order.setStatus("Aguardando confirmação");
        return orderRepository.save(order);
    }

    @GetMapping
    public List<Order> orderList() {
        return orderRepository.findAll();
    }

    @GetMapping("/{orderId}")
    public Order getOneOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    @PutMapping("/{orderId}")
    public Order updateOrder(@PathVariable Long orderId, @RequestBody Order updated) {
        Order ord = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        ord.setStatus(updated.getStatus());
        return orderRepository.save(ord);
    }

    @PostMapping("/{orderId}/conclude")
    public Order concludeOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não econtrado"));
        return orderService.concludeOrder(order);
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
