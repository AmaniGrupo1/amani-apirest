package com.amani.amaniapirest.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion global de OpenAPI / Swagger para la documentacion de la API.
 *
 * <p>Define la informacion general de la API, el esquema de seguridad JWT Bearer
 * y lo aplica como requisito por defecto a todos los endpoints.</p>
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtenido del endpoint POST /auth/login"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI amaniOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Amani API REST")
                        .version("1.0.0")
                        .description("""
                                API REST para la plataforma de psicologia clinica Amani.
                                
                                Gestiona usuarios, pacientes, psicologos, citas, sesiones,
                                historiales clinicos, mensajeria, diario emocional, progreso
                                emocional y el test inicial de preguntas.
                                
                                **Autenticacion:** Usa el endpoint `POST /auth/login` para obtener
                                un token JWT y luego pulsa el boton "Authorize" de arriba para
                                introducirlo.
                                """)
                        .contact(new Contact()
                                .name("Equipo Amani")
                                .email("contacto@amani.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

