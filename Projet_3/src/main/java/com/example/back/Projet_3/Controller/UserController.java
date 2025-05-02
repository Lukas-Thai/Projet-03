package com.example.back.Projet_3.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.services.UserService;

import helper.InfoBuilder;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/:id")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestParam Integer id){
    	Users searchedUser = userService.findById(id).orElseGet(null);
    	if(searchedUser == null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","No user exist with that id"));
    	}
    	return ResponseEntity.ok(InfoBuilder.userInfoBuilder(searchedUser));
    }
}
