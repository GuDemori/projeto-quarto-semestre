package umfg.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "client_entity")
public class Client {
    @Id @GeneratedValue
    private Long id;

    @NotBlank(message = "Establishment name cannot be blank")
    private String establishmentName;

    @NotBlank
    private String city;
    private String address;
    private String establishmentType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEstablishmentType() { return establishmentType; }
    public void setEstablishmentType(String establishmentType) { this.establishmentType = establishmentType; }
}