package com.example.back.Projet_3.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
	public String generateTokenForUser(Users user) {//génère un token pour l'utilisateur fourni
	    Instant now = Instant.now();
	    
	    String subject = user.getEmail(); 
	    
	    //on spécifie les caractéristiques du token
	    JwtClaimsSet claims = JwtClaimsSet.builder()
	            .issuer("self")
	            .issuedAt(now)  
	            .expiresAt(now.plus(3, ChronoUnit.DAYS)) 
	            .subject(subject) 
	            .build();
	    
	    //on définit l'algorithme du token
	    JwtEncoderParameters jwtParams = JwtEncoderParameters.from(
	            JwsHeader.with(MacAlgorithm.HS256).build(),  
	            claims
	    );
	    
	    //on le créer avec les paramètres fournis
	    return this.jwtEncode.encode(jwtParams).getTokenValue();
	}
	public String verifyToken(String token) {
		try {
			Jwt decodedJwt = jwtDecode.decode(token);//si le token est valide on retourne son mail
			return decodedJwt.getSubject();
		}catch(Exception e) {
			return "";//sinon on retourne rien
		}
	}
}
