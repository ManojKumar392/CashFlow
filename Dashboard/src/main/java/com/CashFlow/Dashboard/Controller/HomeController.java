package com.CashFlow.Dashboard.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Value("${spring.application.name}")
    private String applicationName;

    @RequestMapping("/")
    public String index(){
        System.out.println("appName : "+ applicationName + "app");
        return "index.html";
    }
}
