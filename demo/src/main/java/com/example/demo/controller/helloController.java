package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class helloController {

    @GetMapping("/hello")
    public String getHello(Model model)
    {
        model.addAttribute("message","Hello From SpringBoot");
        return "hello";
    }

    @GetMapping("/bye")
    public String getBye(Model model)
    {
        model.addAttribute("message","Bye From SpringBoot");
        return "hello";
    }

    @GetMapping("/fragment")
    public String getFragment(Model model)
    {
        model.addAttribute("fragment","Fragment Expression");
        return "index";
    }
}
