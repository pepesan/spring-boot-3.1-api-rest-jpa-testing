package com.inetum.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Dato API",
                version = "2.0",
                description = "API de demostración para gestión de datos",
                termsOfService = "https://ejemplo.com/terminos",
                contact = @Contact(
                        name = "Soporte Técnico",
                        email = "soporte@ejemplo.com",
                        url = "https://ejemplo.com/soporte"
                ),
                license = @License(
                        name = "GNU General Public License v3.0",
                        url = "https://www.gnu.org/licenses/gpl-3.0.html"
                )
        ),
        servers = {
                @Server(
                        url = "https://localhost:8080/",
                        description = "Servidor de pruebas"
                ),
                @Server(
                        url = "https://staging.ejemplo.com/v2",
                        description = "Servidor de produccion"
                )
        },
        tags = {
                @Tag(name = "rest", description = "Operaciones relacionadas con un api rest simple"),
                @Tag(name = "dato", description = "Gestión de datos , así en general")
        },
        externalDocs = @ExternalDocumentation(
                description = "Documentación completa",
                url = "https://docs.ejemplo.com"
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
