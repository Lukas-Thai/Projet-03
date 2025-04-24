package com.example.back.Projet_3.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.back.Projet_3.model.Users;

@Service
public class JWTService {

	private JwtEncoder jwtEncode; 
	private JwtDecoder jwtDecode;
	
	public JWTService (JwtEncoder jwt, JwtDecoder jwtDecode ) {
		this.jwtEncode = jwt;
		this.jwtDecode = jwtDecode;
	}
	public String generateToken(Authentication authentication) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("self").issuedAt(now).expiresAt(now.plus(3, ChronoUnit.DAYS)).subject(authentication.getName()).build();
		JwtEncoderParameters jwtParams = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims);
		return this.jwtEncode.encode(jwtParams).getTokenValue();
	}
	public String generateTokenForUser(Users user) {
	    Instant now = Instant.now();
	    
	    // You can choose what to put in the subject, like the username or email
	    String subject = user.getEmail(); // or user.getEmail() if you prefer
	    
	    // Set the JWT claims, including the subject, issuer, issued date, and expiration
	    JwtClaimsSet claims = JwtClaimsSet.builder()
	            .issuer("self")  // Issuer (you can change this to your app name)
	            .issuedAt(now)  // Token issue time
	            .expiresAt(now.plus(3, ChronoUnit.DAYS))  // Token expiration (3 days)
	            .subject(subject)  // Subject: usually a unique identifier for the user
	            .build();
	    
	    // Prepare the JWT header (specifying the signing algorithm)
	    JwtEncoderParameters jwtParams = JwtEncoderParameters.from(
	            JwsHeader.with(MacAlgorithm.HS256).build(),  // Set the algorithm (HS256 in this case)
	            claims
	    );
	    
	    // Encode and return the token
	    return this.jwtEncode.encode(jwtParams).getTokenValue();
	}
	public String verifyToken(String token) {
		try {
			Jwt decodedJwt = jwtDecode.decode(token);
			return decodedJwt.getSubject();
		}catch(Exception e) {
			return "";
		}
	}
}
