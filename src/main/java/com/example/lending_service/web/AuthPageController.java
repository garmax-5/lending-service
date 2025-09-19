package com.example.lending_service.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class AuthPageController {

    @GetMapping({"", "/"})
    public String showLoginForm() {
        return "login";
    }
}
