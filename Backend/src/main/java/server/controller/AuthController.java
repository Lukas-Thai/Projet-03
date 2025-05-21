package server.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import server.dto.LoginRequest;
import server.dto.UserResponse;
import server.helper.InfoBuilder;
import server.model.Users;
import server.services.JWTService;
import server.services.UserService;


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
	    public ResponseEntity<Map<String, String>> register(@RequestBody Users user) {
	    	try {
		        Users RegisteredUser = userService.registerUser(user);//enregistre l'utilisateur en bd
		        String token = jwtService.generateTokenForUser(RegisteredUser);//génère un token JWT pour l'user
		        return ResponseEntity.ok(Map.of("token", token));
	    	}
	    	catch(Exception e) {
	    		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Un compte avec cet email existe déjà"));
	    	}

	    }
	    @Operation(summary = "Allow the user to login into their account")
	    @PostMapping("/login")
	    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
	        Users user = userService.findByEmail(loginRequest.getEmail()).orElse(null);//on cherche si un utilisateur existe avec l'email
	        if(user==null) {
	    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "L'email ou le mot de passe est erroné"));//pas d'email existant
	        }

	        if (!new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(
	                loginRequest.getPassword(), user.getPassword())) {
	    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "L'email ou le mot de passe est erroné"));//l'email existe mais le mot de passe est incorrect
	        }

	        String token = jwtService.generateTokenForUser(user);//le login est bon, on génère un token JWT
	        return ResponseEntity.ok(Map.of("token", token));
	    }
	    @Operation(summary = "Fetch the information of the current user")
	    @SecurityRequirement(name = "Bearer Authentication")
	    @GetMapping("/me")
	    public ResponseEntity<UserResponse> me(Authentication auth){
	    	if(auth == null) {
	    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	}
	    	String email = auth.getName();// à partir du token JWT on retrouve l'email
	    	if(email=="") {
	    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	}
	    	Users user = userService.findByEmail(email).orElse(null);//on cherche l'utilisateur à partir de son email
	        if(user==null) {
	        	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	        UserResponse userInfo = InfoBuilder.userInfoBuilder(user);//on formate les données de l'utilisateur
	        return ResponseEntity.ok(userInfo);
	    }
}
