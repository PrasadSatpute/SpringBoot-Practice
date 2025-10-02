package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model)
    {
        User userNormal = new User(101,"ABC","NormalUser");
        User userAdmin = new User(102,"XYZ","Admin");
        model.addAttribute("user",userNormal);
        return "dashboard";
    }
}
