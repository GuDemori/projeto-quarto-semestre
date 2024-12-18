package umfg.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import umfg.project.entity.*;
import umfg.project.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(
            ProductRepository productRepository,
            EstablishmentTypeRepository establishmentTypeRepository,
            ClientRepository clientRepository,
            OrderRepository orderRepository,
            UserRepository userRepository
    ) {
        return args -> {
            // Criação de tipos de estabelecimentos
            EstablishmentType padaria = establishmentTypeRepository.save(new EstablishmentType("Padaria"));
            EstablishmentType supermercado = establishmentTypeRepository.save(new EstablishmentType("Supermercado"));
            EstablishmentType buteco = establishmentTypeRepository.save(new EstablishmentType("Buteco"));

            // Criação de produtos
            Product pacoca = new Product("Paçoca", "Doce de amendoim", 100, 2.50);
            pacoca.setEstablishmentTypes(List.of(padaria));

            Product canudoFrito = new Product("Canudo Frito", "Salgado crocante", 50, 5.00);
            canudoFrito.setEstablishmentTypes(Arrays.asList(padaria, buteco));

            Product bubbloo = new Product("Bubbloo", "Chiclete com bolha", 200, 1.50);
            bubbloo.setEstablishmentTypes(Arrays.asList(padaria, supermercado, buteco));

            Product energetico = new Product("Energético", "Bebida energética", 30, 8.00);
            energetico.setEstablishmentTypes(List.of(supermercado));

            Product sacola = new Product("Sacola", "Sacola plástica", 500, 0.50);
            sacola.setEstablishmentTypes(Arrays.asList(padaria, supermercado));

            productRepository.saveAll(Arrays.asList(pacoca, canudoFrito, bubbloo, energetico, sacola));

            // Criação de clientes
            Client cliente1 = new Client();
            cliente1.setEstablishmentName("Padaria Central");
            cliente1.setCity("São Paulo");
            cliente1.setAddress("Rua A, 123");
            cliente1.setEstablishmentTypes(List.of(padaria));

            Client cliente2 = new Client();
            cliente2.setEstablishmentName("Supermercado Top");
            cliente2.setCity("Rio de Janeiro");
            cliente2.setAddress("Av. B, 456");
            cliente2.setEstablishmentTypes(List.of(supermercado));

            Client cliente3 = new Client();
            cliente3.setEstablishmentName("Bar do João");
            cliente3.setCity("Belo Horizonte");
            cliente3.setAddress("Rua C, 789");
            cliente3.setEstablishmentTypes(List.of(buteco));

            Client cliente4 = new Client();
            cliente4.setEstablishmentName("Mercadinho Popular");
            cliente4.setCity("Curitiba");
            cliente4.setAddress("Av. D, 101");
            cliente4.setEstablishmentTypes(Arrays.asList(padaria, supermercado));

            Client cliente5 = new Client();
            cliente5.setEstablishmentName("Doceria da Ana");
            cliente5.setCity("Porto Alegre");
            cliente5.setAddress("Rua E, 202");
            cliente5.setEstablishmentTypes(Arrays.asList(padaria, buteco));

            clientRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3, cliente4, cliente5));

            // Garante que exista um User no ID=1 ou cria um
            User user = userRepository.findById(1L).orElseGet(() -> {
                User u = new User();
                u.setUsername("admin");
                u.setPassword("adminpass");
                u.setFullName("Administrador Padrão");
                return userRepository.save(u);
            });

            // Busca produtos
            List<Product> products = productRepository.findAll();
            if (products.isEmpty()) {
                System.out.println("Nenhum produto cadastrado. Não foi possível criar pedido.");
                return;
            }

            Order order = new Order();
            order.setUser(user);
            order.setClient(cliente1);
            order.setStatus("Em espera");
            order.setOrderDate(LocalDateTime.now());

            List<OrderItem> orderItems = new ArrayList<>();
            double total = 0.0;

            for (int i = 0; i < Math.min(products.size(), 2); i++) {
                Product p = products.get(i);

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(p);
                item.setQuantity(2); // Quantidade fixa como exemplo
                item.setPrice(p.getPrice());

                total += item.getPrice() * item.getQuantity();
                orderItems.add(item);
            }

            order.setItems(orderItems);
            order.setTotalValue(total);

            orderRepository.save(order);

            System.out.println("Produtos, tipos de estabelecimentos, clientes e 1 pedido criados com sucesso!");
        };
    }
}
