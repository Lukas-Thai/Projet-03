package com.example.back.Projet_3.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.Projet_3.model.Rentals;
import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.services.JWTService;
import com.example.back.Projet_3.services.MessagesService;
import com.example.back.Projet_3.services.RentalsService;
import com.example.back.Projet_3.services.UserService;

import dto.MessageRequest;
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
    private JWTService jwtService;
    @Autowired
    public MessageController(MessagesService ms, UserService us, RentalsService rs, JWTService jwt) {
        this.MessagesServ = ms;
        this.userService = us;
        this.rentService = rs;
        this.jwtService = jwt;
    }
    @PostMapping
    ResponseEntity<Map<String, Object>> me(@RequestHeader("Authorization") String authHeader, @RequestBody MessageRequest content){
        String token = authHeader.replace("Bearer ", "");
    	String email = jwtService.verifyToken(token);
    	if(email=="") {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
    	}
    	Users user = userService.findByEmail(email).orElse(null);
        if(user==null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
        }
        if(content == null) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","No message to save"));
        }
        if(content.getMessage()==null || content.getRental_id()==null ) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","The message need a content, a rental id"));
        }
        Rentals rent = this.rentService.getRentalsById(content.getRental_id()).orElseGet(null);
        if(rent == null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "The rent does not exist"));
        }
        content.setUser_id(user.getId());
        this.MessagesServ.createMessage(user, rent, content.toString());
        return ResponseEntity.ok(Map.of("message","Message send with success"));
    }
}
