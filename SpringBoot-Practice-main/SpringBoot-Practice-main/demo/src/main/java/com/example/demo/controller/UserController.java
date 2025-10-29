package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/link-expression")
    public String linkExpression(Model model)
    {
        model.addAttribute("id",1);
        return "link-expression";
    }

    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable int id, Model model) {
        // logic here
        return "edit-user";
    }


}
