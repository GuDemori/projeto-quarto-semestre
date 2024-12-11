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

    public Order concludeOrder(Order o) {
        o.setOrderDate(LocalDateTime.now());
        double total = o.getProducts().stream().mapToDouble(Product::getPrice).sum();
        o.setTotalValue(total);

        // Check stock
        for (Product prod : o.getProducts()) {
            if (prod.getStockQuantity() <= 0) {
                throw new RuntimeException("Product " + prod.getName() + " is out of stock.");
            }
        }

        Order saved = orderRepo.save(o);

        // Debit stock
        for (Product prod : o.getProducts()) {
            prod.setStockQuantity(prod.getStockQuantity() - 1);
            productRepo.save(prod);
        }

        return saved;
    }
}
