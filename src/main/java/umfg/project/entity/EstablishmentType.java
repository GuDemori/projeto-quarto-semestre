package umfg.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import umfg.project.repository.EstablishmentTypeRepository;

@Entity
@Table(name = "establishment_type")
public class EstablishmentType {

    public EstablishmentType(String name) {
        this.name = name;
    }

    public EstablishmentType(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do tipo de estabelecimento é obrigatório.")
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
