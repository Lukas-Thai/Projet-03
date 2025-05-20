package com.example.back.Projet_3.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RessourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {//permet à l'utilisateur de récupérer les images upload
        registry.addResourceHandler("/uploads/**")
        	.addResourceLocations("file:uploads/");
    }
}
