package umfg.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "user_entity")
public class User {
    @Id @GeneratedValue
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @NotBlank(message = "Full name cannot be blank")
    @Pattern(regexp = "^[^\\d]+$", message = "Full name cannot contain digits")
    private String fullName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
