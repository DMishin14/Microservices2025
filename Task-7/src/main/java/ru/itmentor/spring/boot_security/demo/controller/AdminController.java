package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

/**
 * Контроллер для админских веб-страниц
 * Доступен только пользователям с ролью ADMIN
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Админ панель - список всех пользователей
     */
    @GetMapping()
    public String adminPanel(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/index";
    }

    /**
     * Форма создания нового пользователя
     */
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/new";
    }

    /**
     * Создание нового пользователя
     */
    @PostMapping("/new")
    public String createUser(@ModelAttribute User user, @RequestParam("roles") String[] roles) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Set<Role> userRoles = new HashSet<>();
        for (String roleName : roles) {
            Role role = roleService.findByName(roleName);
            if (role != null) {
                userRoles.add(role);
            }
        }
        user.setRoles(userRoles);
        
        userService.saveUser(user);
        return "redirect:/admin";
    }

    /**
     * Просмотр пользователя
     */
    @GetMapping("/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "admin/show";
        }
        return "redirect:/admin";
    }

    /**
     * Форма редактирования пользователя
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit";
        }
        return "redirect:/admin";
    }

    /**
     * Обновление пользователя
     */
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, @RequestParam("roles") String[] roles) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Set<Role> userRoles = new HashSet<>();
        for (String roleName : roles) {
            Role role = roleService.findByName(roleName);
            if (role != null) {
                userRoles.add(role);
            }
        }
        user.setRoles(userRoles);
        
        userService.updateUser(id, user);
        return "redirect:/admin";
    }

    /**
     * Удаление пользователя
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}