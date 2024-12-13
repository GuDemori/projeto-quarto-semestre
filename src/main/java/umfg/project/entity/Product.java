package umfg.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "product_entity")
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório.")
    @Size(max = 255, message = "O nome não pode exceder 255 caracteres.")
    private String name;

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres.")
    private String description;

    @Size(max = 255, message = "O tipo de estabelecimento não pode exceder 255 caracteres.")
    private String establishmentType;


    @NotNull(message = "A quantidade em estoque não pode ser nula.")
    @Min(value = 0, message = "A quantidade não pode ser menor que zero")
    private int stockQuantity;

    @NotNull(message = "O preço não pode ser nulo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O preço não pode ser menor que 0")
    private Double price;

    public Product(String name, String description, Integer stockQuantity, String establishmentType, Double price) {
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.establishmentType = establishmentType;
        this.price = price;
    }

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getEstablishmentType() { return establishmentType; }
    public void setEstablishmentType(String establishmentType) { this.establishmentType = establishmentType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
