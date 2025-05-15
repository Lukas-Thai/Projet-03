package com.example.back.Projet_3.configuration;

import java.util.Arrays;
import java.util.Collections;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig{
	@Value("${jwt.key}")
	private String JwtKey;
	@Value("${front.url}")
	private String frontUrl;
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
        .cors(Customizer.withDefaults()) 
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()//exception à la règle d'avoir besoin d'un token JWT
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/v3/**").permitAll()
            .requestMatchers("/uploads/**").permitAll()
            .anyRequest().authenticated()//force tous les endpoints à avoir besoin d'un token JWT
        )
        .httpBasic(Customizer.withDefaults())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {//message par défaut si le token est manquant
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Unauthorized: Invalid or missing token\"}");
                response.getWriter().flush(); 
                response.getWriter().close();
            })
        )
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));


    return http.build(); 
	}
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();//permet d'accepter les requetes CORS
        configuration.setAllowedOrigins(Collections.singletonList(this.frontUrl)); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
	@Bean
	public JwtDecoder jwtDecoder() {//décodeur de token JWT
		SecretKeySpec secret = new SecretKeySpec(this.JwtKey.getBytes(), 0 ,this.JwtKey.getBytes().length, "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(secret).macAlgorithm(MacAlgorithm.HS256).build();
	}
	@Bean
    public JwtEncoder jwtEncoder() {//encodeur de token JWT
        return new NimbusJwtEncoder(new ImmutableSecret<>(this.JwtKey.getBytes()));
    }
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {//hasheur de mot de passe
		return new BCryptPasswordEncoder();
	}
	private SecurityScheme createAPIKeyScheme() {//configuration de sécurité pour Swagger
	    return new SecurityScheme().type(SecurityScheme.Type.HTTP)
	        .bearerFormat("JWT")
	        .scheme("bearer");
	}
	@Bean
	public OpenAPI openAPI() {//configuration général pour Swagger
	    return new OpenAPI()
	        .components(new Components().addSecuritySchemes(
	            "Bearer Authentication",
	            createAPIKeyScheme()
	        ))
	        .info(new Info()
	            .title("Rental House API")
	            .description("In this page, you will be able to discover every route used for the API !")
	            .version("1.0")
	            .contact(new Contact().name("Lukas THAI"))
	        );
	}
}
