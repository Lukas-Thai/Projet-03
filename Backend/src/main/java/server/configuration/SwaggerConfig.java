package server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
@Configuration
public class SwaggerConfig {
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
