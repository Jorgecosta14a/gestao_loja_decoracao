package pt.ipvc.estg.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginWebController {

    private static final String USER_SESSION_KEY = "user";

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute(USER_SESSION_KEY) != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             HttpSession session,
                             Model model) {
        if ("admin".equals(username) && "admin".equals(password)) {
            session.setAttribute(USER_SESSION_KEY, username);
            return "redirect:/";
        }

        model.addAttribute("erro", "Credenciais inválidas.");
        model.addAttribute("username", username);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
