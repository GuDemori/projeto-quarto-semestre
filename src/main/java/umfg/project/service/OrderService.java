package umfg.project.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import umfg.project.entity.*;
import umfg.project.repository.ClientRepository;
import umfg.project.repository.OrderRepository;
import umfg.project.repository.ProductRepository;
import umfg.project.repository.UserRepository;
import umfg.project.specification.OrderSpecification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public void updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (updatedOrder.getStatus() != null) {
            existingOrder.setStatus(updatedOrder.getStatus());
        }

        orderRepository.save(existingOrder);
    }


    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
    @Transactional
    public Order concludeOrder(Order order) {
        // Define a data do pedido como a data atual
        order.setOrderDate(LocalDateTime.now());

        double total = 0.0;

        // Validação e ajuste dos produtos no pedido
        for (var product : order.getProducts()) {
            var dbProduct = productRepository.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + product.getId()));

            // Validação do estoque
            Integer requestedQuantity = product.getStockQuantity();
            if (requestedQuantity == null || requestedQuantity <= 0) {
                throw new RuntimeException("Quantidade inválida para o produto: " + dbProduct.getName());
            }

            if (requestedQuantity > dbProduct.getStockQuantity()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + dbProduct.getName()
                        + ". Disponível: " + dbProduct.getStockQuantity());
            }

            // Calcula o total do pedido
            total += dbProduct.getPrice() * requestedQuantity;

            // Atualiza o estoque no banco de dados
            dbProduct.setStockQuantity(dbProduct.getStockQuantity() - requestedQuantity);
            productRepository.save(dbProduct);

            // Log informativo
            System.out.println("Estoque atualizado para produto: " + dbProduct.getName()
                    + " | Novo estoque: " + dbProduct.getStockQuantity());
        }
        // Define o valor total calculado no pedido
        order.setTotalValue(total);

        // Log do pedido antes do save
        System.out.println("Salvando pedido no banco de dados: " + order);

        // Salvar o pedido no banco de dados
        Order savedOrder = orderRepository.save(order);

        // Log do pedido salvo
        System.out.println("Pedido salvo com sucesso! ID: " + savedOrder.getId()
                + " | Total: R$ " + savedOrder.getTotalValue());

        return savedOrder;
    }

    @Transactional
    public void saveOrder(List<Long> productIds, List<Integer> quantities, Long clientId, String status, Long userId) {
        Order newOrder = new Order();

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        newOrder.setUser(user);

        if (productIds == null || productIds.isEmpty() || quantities == null || quantities.isEmpty()) {
            throw new IllegalArgumentException("Produtos e quantidades não podem ser vazios.");
        }

        if (productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Número de produtos e quantidades não corresponde.");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clientId));
        newOrder.setClient(client);
        newOrder.setStatus(status);
        newOrder.setOrderDate(LocalDateTime.now());

        double total = 0.0;

        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);

            Product dbProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));

            // Validação do estoque
            if (quantity > dbProduct.getStockQuantity()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + dbProduct.getName());
            }

            // Atualizar estoque
            dbProduct.setStockQuantity(dbProduct.getStockQuantity() - quantity);
            productRepository.save(dbProduct);

            // Criar item do pedido
            OrderItem item = new OrderItem(newOrder, dbProduct, quantity, dbProduct.getPrice());
            orderItems.add(item);

            // Somar ao total
            total += dbProduct.getPrice() * quantity;
        }

        newOrder.setTotalValue(total);
        newOrder.setItems(orderItems); // Associar os itens ao pedido

        // Salvar pedido e itens
        orderRepository.save(newOrder);
    }


    @Transactional
    public void updateOrderItems(Long orderId, List<Long> productIds, List<Integer> quantities, Long clientId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clientId));

        order.setClient(client);
        order.setStatus(status);
        order.setOrderDate(LocalDateTime.now());

        // Limpar a lista anterior (se ManyToMany em 'products')
        order.getProducts().clear();

        double total = 0.0;
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer qty = quantities.get(i);
            Product dbProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + productId));

            if (qty > dbProduct.getStockQuantity()) {
                throw new RuntimeException("Estoque insuficiente p/ produto: " + dbProduct.getName());
            }
            total += dbProduct.getPrice() * qty;

            // Atualiza estoque etc. se quiser
            dbProduct.setStockQuantity(dbProduct.getStockQuantity() - qty);
            productRepository.save(dbProduct);

            // Adiciona esse product no pedido
            order.getProducts().add(dbProduct);
        }
        order.setTotalValue(total);

        orderRepository.save(order);
    }

    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + id));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public List<Order> filterOrders(String city, LocalDate startDate, LocalDate endDate, Long clientId) {
        Specification<Order> spec = OrderSpecification.filterBy(city, startDate, endDate, clientId);
        return orderRepository.findAll(spec);
    }


}
