package com.example.back.Projet_3.Controller;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.Projet_3.model.Users;
import com.example.back.Projet_3.services.JWTService;
import com.example.back.Projet_3.services.UserService;
import java.util.HashMap;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	    @Autowired
	    private UserService userService;

	    @Autowired
	    private JWTService jwtService;
	    @Autowired
	    public AuthController(UserService userService, JWTService jwt) {
	        this.userService = userService;
	        this.jwtService = jwt;
	    }

	    @PostMapping("/register")
	    public Map<String, String> register(@RequestBody Users user) {
	    	try {
		        Users RegisteredUser = userService.registerUser(user);
		        String token = jwtService.generateTokenForUser(RegisteredUser);
		        return Map.of("token", token);
	    	}
	    	catch(Exception e) {
	    		return Map.of("message", "Un compte avec cet email existe déjà");
	    	}

	    }

	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody Users loginRequest) {
	        Users user = userService.findByEmail(loginRequest.getEmail()).orElse(null);
	        if(user==null) {
	    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "L'email ou le mot de passe est erroné"));
	        }

	        if (!new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(
	                loginRequest.getPassword(), user.getPassword())) {
	    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "L'email ou le mot de passe est erroné"));
	        }

	        String token = jwtService.generateTokenForUser(user);
	        return ResponseEntity.ok(Map.of("token", token));
	    }
	    @GetMapping("/me")
	    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader){
	        String token = authHeader.replace("Bearer ", "");
	    	String email = jwtService.verifyToken(token);
	    	if(email=="") {
	    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
	    	}
	    	Users user = userService.findByEmail(email).orElse(null);
	        if(user==null) {
	        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
	        }
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	        Map<String, Object> userInfo = new HashMap<>();
	        userInfo.put("id", user.getId());
	        userInfo.put("name", user.getName());
	        userInfo.put("email", user.getEmail());
	        userInfo.put("created_at", user.getCreated_at().format(formatter));
	        userInfo.put("updated_at", user.getUpdated_at()!=null ? user.getUpdated_at().format(formatter) : "");
	        return ResponseEntity.ok(userInfo);
	    }
}
