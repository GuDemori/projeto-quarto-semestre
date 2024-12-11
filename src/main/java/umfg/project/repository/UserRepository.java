package umfg.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umfg.project.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}

