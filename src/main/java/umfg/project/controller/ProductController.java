package umfg.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umfg.project.entity.Product;
import umfg.project.repository.ProductRepository;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired ProductRepository productRepository;

    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> productList() {
        return productRepository.findAll();
    }

    @GetMapping("/{productId}")
    public Product getOneProduct(@PathVariable Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @PutMapping("/{productId}")
    public Product updateProduct(@PathVariable Long productId, @Valid @RequestBody Product updated) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setImage(updated.getImage());
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setStockQuantity(updated.getStockQuantity());
        product.setEstablishmentType(updated.getEstablishmentType());
        product.setPrice(updated.getPrice());
        return productRepository.save(product);
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productRepository.deleteById(productId);
    }
}
