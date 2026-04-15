//Swagger = A visual website that shows ALL your API endpoints
//          so you can TEST them without writing any code!
package com.christinamai.project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info() //the header of your Swagger page
                        .title("Job Search API")
                        .version("1.0")
                        .description("Job Search Application REST API"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", //ομα σε αυτόν τον κανόνα ασφαλείας
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP) // What TYPE of security is this
                                        .scheme("bearer") //Which HTTP authentication scheme.Όταν ο χρήστης σου δώσει το token, βάλε τη λέξη Bearer μπροστά από αυτό στο Authorization header
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }
}