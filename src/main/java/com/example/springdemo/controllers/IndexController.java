package com.example.springdemo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @GetMapping({"/", "/home"})
    public String home(Model model, @RequestParam(value="name", required=false, defaultValue="man") String name) {
        model.addAttribute("name", name);
        return "home";
    }
}
