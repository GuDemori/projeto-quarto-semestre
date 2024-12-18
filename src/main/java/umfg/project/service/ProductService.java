package umfg.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umfg.project.repository.ProductRepository;
import umfg.project.entity.Product;
import umfg.project.specification.ProductSpecification;


import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> filterProducts(Long id, String name, List<Long> establishmentTypeIds) {
        var spec = ProductSpecification.filterBy(id, name, establishmentTypeIds);
        return productRepository.findAll(spec);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
    }

    public List<Product> findByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }


}
