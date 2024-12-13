package umfg.project.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import umfg.project.entity.User;
import umfg.project.repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "errorMessage", required = false) String errorMessage,
                                @RequestParam(value = "successMessage", required = false) String successMessage,
                                Model model) {
        if (errorMessage != null) model.addAttribute("errorMessage", errorMessage);
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(@RequestParam(value = "errorMessage", required = false) String errorMessage,
                                   @RequestParam(value = "successMessage", required = false) String successMessage,
                                   Model model) {
        if (errorMessage != null) model.addAttribute("errorMessage", errorMessage);
        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String fullName,
                                  @RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  RedirectAttributes redirectAttributes) {
        if (fullName.isBlank()) {
            logger.warn("Erro de validação: Nome completo vazio.");
            redirectAttributes.addFlashAttribute("errorMessage", "O nome completo não pode ser vazio.");
            return "redirect:/user/register";
        }
        if (username.isBlank()) {
            logger.warn("Erro de validação: Username vazio.");
            redirectAttributes.addFlashAttribute("errorMessage", "O username não pode ser vazio.");
            return "redirect:/user/register";
        }
        if (password.length() < 8) {
            logger.warn("Erro de validação: Senha menor que 8 caracteres.");
            redirectAttributes.addFlashAttribute("errorMessage", "A senha deve ter pelo menos 8 caracteres.");
            return "redirect:/user/register";
        }
        if (!password.equals(confirmPassword)) {
            logger.warn("Erro de validação: Senhas não conferem.");
            redirectAttributes.addFlashAttribute("errorMessage", "As senhas não conferem.");
            return "redirect:/user/register";
        }

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setUsername(username);
        newUser.setPassword(password);
        userRepository.save(newUser);

        logger.info("Usuário registrado com sucesso: {}", username);
        redirectAttributes.addFlashAttribute("successMessage", "Conta criada com sucesso! Agora você pode fazer login.");
        return "redirect:/user/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        logger.info("Tentativa de login com username={} e password={}", username, password);

        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null) {
            logger.info("Login bem-sucedido para o usuário: {}", username);
            redirectAttributes.addFlashAttribute("successMessage", "Login bem-sucedido! Bem-vindo, " + username);
            return "redirect:/user/dashboard";
        } else {
            logger.warn("Falha no login: credenciais inválidas para username={}", username);
            redirectAttributes.addFlashAttribute("errorMessage", "Credenciais inválidas. Tente novamente.");
            return "redirect:/user/login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // Nome da nova página inicial
    }

    @PostMapping("/create")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @Valid @RequestBody User updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());
        user.setFullName(updatedUser.getFullName());
        return userRepository.save(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        logger.error("Erro inesperado: {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("errorMessage", "Erro interno. Tente novamente mais tarde.");
        return "redirect:/user/login";
    }
}
