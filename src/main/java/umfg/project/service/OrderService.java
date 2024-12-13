package umfg.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umfg.project.entity.Order;
import umfg.project.entity.Product;
import umfg.project.repository.OrderRepository;
import umfg.project.repository.ProductRepository;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo;

    public Order concludeOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());

        // Calcular o valor total do pedido
        double total = order.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
        order.setTotalValue(total);

        // Verificar o estoque de todos os produtos no pedido
        for (Product product : order.getProducts()) {
            Product storedProduct = productRepo.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("Produto " + product.getName() + " não encontrado"));

            if (storedProduct.getStockQuantity() < 1) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + storedProduct.getName());
            }
        }

        // Salvar o pedido
        Order savedOrder = orderRepo.save(order);

        // Atualizar o estoque dos produtos
        for (Product product : order.getProducts()) {
            Product storedProduct = productRepo.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("Produto " + product.getName() + " não encontrado"));

            // Debitar o estoque
            storedProduct.setStockQuantity(storedProduct.getStockQuantity() - 1);
            productRepo.save(storedProduct);
        }

        return savedOrder;
    }
}
