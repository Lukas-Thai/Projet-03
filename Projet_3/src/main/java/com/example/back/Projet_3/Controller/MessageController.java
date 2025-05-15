package com.example.back.Projet_3.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.services.MessagesService;
import com.example.back.Projet_3.services.RentalsService;
import com.example.back.Projet_3.services.UserService;

import dto.MessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessagesService MessagesServ;
    @Autowired
    private UserService userService;
    @Autowired
    private RentalsService rentService;

    @Autowired
    public MessageController(MessagesService ms, UserService us, RentalsService rs) {
        this.MessagesServ = ms;
        this.userService = us;
        this.rentService = rs;
    }
    @Operation(summary = "Allow the user to send a message about a rental")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    ResponseEntity<Map<String, Object>> me(Authentication auth, @RequestBody MessageRequest content){
    	String email = auth.getName();//on retrouve le mail de l'user à partir de son token
    	if(email=="") {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
    	}
    	Users user = userService.findByEmail(email).orElse(null);//on retrouve l'user à partir de son email
        if(user==null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
        }
        if(content == null) {//controle que la requete est bien formé
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","No message to save"));
        }
        if(content.getMessage()==null || content.getRental_id()==null ) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","The message need a content, a rental id"));
        }
        Rentals rent = this.rentService.getRentalsById(content.getRental_id()).orElseGet(null);//on vérifie que le message concerne un rental existant 
        if(rent == null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "The rent does not exist"));
        }
        content.setUser_id(user.getId());//on ignore l'user Id fourni et se base sur celui qui provient du token
        this.MessagesServ.createMessage(user, rent, content.getMessage().trim());//on enregistre le message
        return ResponseEntity.ok(Map.of("message","Message send with success"));
    }
}
