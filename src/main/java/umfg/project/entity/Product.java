package umfg.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

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

    @NotNull(message = "A quantidade em estoque não pode ser nula.")
    @Min(value = 0, message = "A quantidade não pode ser menor que zero")
    private int stockQuantity;

    @NotNull(message = "O preço não pode ser nulo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O preço não pode ser menor que 0")
    private Double price;

    @Transient
    private int quantity;

    @ManyToMany
    @JoinTable(
            name = "product_establishment_type",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "establishment_type_id")
    )
    private List<EstablishmentType> establishmentTypes;

    // Getter e Setter
    public List<EstablishmentType> getEstablishmentTypes() {
        return establishmentTypes;
    }

    public void setEstablishmentTypes(List<EstablishmentType> establishmentTypes) {
        this.establishmentTypes = establishmentTypes;
    }


    public Product(String name, String description, Integer stockQuantity, Double price) {
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
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



    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
