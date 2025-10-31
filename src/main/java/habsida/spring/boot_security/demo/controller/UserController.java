package habsida.spring.boot_security.demo.controller;

import habsida.spring.boot_security.demo.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class    UserController {

    @GetMapping("/user/info")
    public String userPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        model.addAttribute("currentUser", currentUser);
        return "user/info";
    }

    @GetMapping("/")
    public String login(Model model) {
        return "redirect:/login";
    }
}
