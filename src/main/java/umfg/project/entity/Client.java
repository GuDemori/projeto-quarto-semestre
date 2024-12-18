package umfg.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "client_entity")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do estabelecimento não pode estar em branco.")
    @Size(max = 255, message = "O nome do estabelecimento não pode exceder 255 caracteres.")
    private String establishmentName;

    @NotBlank(message = "A cidade é um campo obrigatório.")
    @Size(max = 255, message = "A cidade não pode exceder 255 caracteres.")
    private String city;

    @Size(max = 255, message = "O endereço não pode exceder 255 caracteres.")
    private String address;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_establishment_type",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "establishment_type_id")
    )
    private List<EstablishmentType> establishmentTypes;

    // Construtor padrão
    public Client() {}

    // Construtor parametrizado
    public Client(String establishmentName, String city, String address) {
        this.establishmentName = establishmentName;
        this.city = city;
        this.address = address;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<EstablishmentType> getEstablishmentTypes() { return establishmentTypes; }
    public void setEstablishmentTypes(List<EstablishmentType> establishmentTypes) {
        this.establishmentTypes = establishmentTypes;
    }
}
