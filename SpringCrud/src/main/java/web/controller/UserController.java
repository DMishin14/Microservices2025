package web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.model.User;
import web.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "index";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "show";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "new";
    }

    @PostMapping()
    public String create(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute("user") User user) {
        userService.updateUser(id, user);
        return "redirect:/users";
    }

    // 🔹 Delete через POST
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
