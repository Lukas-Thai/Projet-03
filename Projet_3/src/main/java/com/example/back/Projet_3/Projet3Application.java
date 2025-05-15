package com.example.back.Projet_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.back.Projet_3", "Controller", "services", "configuration", "model"})//fichier permettant de lancer le serveur
public class Projet3Application {
	public static void main(String[] args) {
		SpringApplication.run(Projet3Application.class, args);
	}

}
