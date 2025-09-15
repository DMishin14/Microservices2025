package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Controller
public class DataInitController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public DataInitController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initData() {

        if (roleService.getAllRoles().isEmpty()) {
            System.out.println("Создаем базовые роли...");

            Role adminRole = new Role("ROLE_ADMIN");
            roleService.saveRole(adminRole);
            System.out.println("Роль ROLE_ADMIN создана");

            Role userRole = new Role("ROLE_USER");
            roleService.saveRole(userRole);
            System.out.println("Роль ROLE_USER создана");
        } else {
            System.out.println("Роли уже существуют: " + roleService.getAllRoles());
        }


        if (userService.getAllUsers().isEmpty()) {
            System.out.println("Создаем тестовых пользователей...");

            User admin = new User("Admin", "Adminov", 30, "admin", "admin", "admin@example.com");

            admin.setPassword(passwordEncoder.encode(admin.getPassword()));

            Set<Role> adminRoles = new HashSet<>();
            Role adminRole = roleService.findByName("ROLE_ADMIN");
            if (adminRole != null) {
                adminRoles.add(adminRole);
                System.out.println("Роль ROLE_ADMIN найдена и назначена админу");
            } else {
                System.out.println("ОШИБКА: Роль ROLE_ADMIN не найдена!");
            }
            admin.setRoles(adminRoles);
            
            userService.saveUser(admin);
            System.out.println("Админ создан: логин=admin, пароль=admin");

            User user = new User("User", "Userov", 25, "user", "user", "user@example.com");

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Set<Role> userRoles = new HashSet<>();
            Role userRole = roleService.findByName("ROLE_USER");
            if (userRole != null) {
                userRoles.add(userRole);
                System.out.println("Роль ROLE_USER найдена и назначена пользователю");
            } else {
                System.out.println("ОШИБКА: Роль ROLE_USER не найдена!");
            }
            user.setRoles(userRoles);
            
            userService.saveUser(user);
            System.out.println("Пользователь создан: логин=user, пароль=user");
        }
        
        System.out.println("Инициализация данных завершена!");
    }

    @GetMapping("/init")
    public String initDataManually() {
        System.out.println("Ручная инициализация данных...");
        initData();
        return "redirect:/";
    }
}
