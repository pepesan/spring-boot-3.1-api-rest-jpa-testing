# Ejemplo de uso de Spring boot 4.1.1 con JPA H2 MVC Test API y Testing

## Requisitos

- **JDK 21** (LTS). No usar JDK 25: Lombok (ni siquiera en su última versión publicada, 1.18.48)
  soporta todavía JDK 25 como compilador host, y la compilación falla en todo el proyecto sin
  errores de Lombok explícitos (solo "cannot find symbol" sobre getters/setters/constructores
  generados). Ver el comentario en `pom.xml` (propiedad `java.version`) para más detalle.
- **Maven** 3.9+ (el `pom.xml` no incluye Maven Wrapper).
- **Docker** en marcha, necesario para los tests que usan Testcontainers (MariaDB/MySQL).

## Arranque
mvn spring-boot:run

## Acceso swagger
http://localhost:8080/swagger-ui.html
ó
http://localhost:8080/swagger-ui/index.html

### Código JSON de Swagger/OpenApi
http://localhost:8080/v3/api-docs

## Ejecución de migraciones
mvn flyway:migrate
