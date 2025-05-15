package com.example.back.Projet_3.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.services.UserService;

import helper.InfoBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Operation(summary = "Get the data of an user with their id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Integer id){
    	Users searchedUser = userService.findById(id).orElseGet(null);//on cherche l'user avec son id
    	if(searchedUser == null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","No user exist with that id"));
    	}
    	return ResponseEntity.ok(InfoBuilder.userInfoBuilder(searchedUser));//on formate les données
    }
}
