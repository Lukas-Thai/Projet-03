package com.example.back.Projet_3.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
public class HelloController {
	@Hidden
    @GetMapping("/")
    public String hello() {//controlleur permettant de maintenir le serveur
        return "Server is running!";
    }
}