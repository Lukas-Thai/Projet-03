package com.example.back.Projet_3.Controller;

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

import dto.LoginRequest;
import helper.InfoBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


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
	    @Operation(summary = "Allow the user to register an account")
	    @ApiResponses(value = {@ApiResponse(responseCode= "200", description="Register Successful")})
	    public ResponseEntity<Map<String, String>> register(@RequestBody Users user) {
	    	try {
		        Users RegisteredUser = userService.registerUser(user);
		        String token = jwtService.generateTokenForUser(RegisteredUser);
		        return ResponseEntity.ok(Map.of("token", token));
	    	}
	    	catch(Exception e) {
	    		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Un compte avec cet email existe déjà"));
	    	}

	    }

	    @PostMapping("/login")
	    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
	        Users user = userService.findByEmail(loginRequest.getEmail()).orElse(null);
	        if(user==null) {
	        	System.out.println(loginRequest.getEmail());
	        	System.out.println(loginRequest.getPassword());
	    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "L'email ou le mot de passe est erroné"));
	        }

	        if (!new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(
	                loginRequest.getPassword(), user.getPassword())) {
	    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "L'email ou le mot de passe est erroné"));
	        }

	        String token = jwtService.generateTokenForUser(user);
	        return ResponseEntity.ok(Map.of("token", token));
	    }
	    @GetMapping("/me")
	    public ResponseEntity<Map<String, Object>> me(@RequestHeader("Authorization") String authHeader){
	        String token = authHeader.replace("Bearer ", "");
	    	String email = jwtService.verifyToken(token);
	    	if(email=="") {
	    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authorization invalid"));
	    	}
	    	Users user = userService.findByEmail(email).orElse(null);
	        if(user==null) {
	        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Unknown or deleted user"));
	        }
	        Map<String, Object> userInfo = InfoBuilder.userInfoBuilder(user);
	        return ResponseEntity.ok(userInfo);
	    }
}
