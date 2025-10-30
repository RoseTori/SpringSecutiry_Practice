package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.models.Role;
import habsida.spring.boot_security.demo.models.User;
import habsida.spring.boot_security.demo.service.RoleService;
import habsida.spring.boot_security.demo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
public class    UserController {
    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserController(UserServiceImpl userService, PasswordEncoder passwordEncoder,  RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String home() {
        return "redirect:/admin/users";
    }
    @GetMapping("/user")
    public String userPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        model.addAttribute("currentUser", currentUser);
        return "user";
    }

    @GetMapping("/admin/users")
    public String users(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/admin/users/create")
    public String showCreateForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        List<Role> availableRoles = roleService.findAll();
        model.addAttribute("availableRoles", availableRoles);
        return "create";
    }
    @PostMapping("/admin/users/create")
    public String createUser(@ModelAttribute User user,
                             @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleService.findByIds(roleIds);
            user.setRoles(roles);
        }

        userService.saveUser(user);
        return "redirect:/admin/users";
    }


    @GetMapping("/admin/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        List<Role> availableRoles = roleService.findAll();

        model.addAttribute("user", user);
        model.addAttribute("availableRoles", availableRoles);

        if (user == null) {
            return "redirect:/admin/users";
        }
        return "edit";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @ModelAttribute User user,
                           @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                           @RequestParam(value = "password", required = false) String newPassword) {

        User existingUser = userService.getUserById(id);

        if (newPassword != null && !newPassword.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
        } else {
            user.setPassword(existingUser.getPassword());
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleService.findByIds(roleIds);
            user.setRoles(roles);
        } else {
            user.setRoles(existingUser.getRoles());
        }

        user.setId(id);
        userService.updateUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }



}
