package umfg.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "product_entity")
public class Product {
    @Id @GeneratedValue
    private Long id;

    private String image;

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Stock quantity cannot be null")
    private Integer stockQuantity;

    private String establishmentType;

    @NotNull(message = "Price cannot be null")
    private Double price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

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