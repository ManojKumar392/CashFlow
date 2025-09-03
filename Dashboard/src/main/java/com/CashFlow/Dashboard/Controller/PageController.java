package com.CashFlow.Dashboard.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // will render login.html from templates
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // will render register.html
    }
}
