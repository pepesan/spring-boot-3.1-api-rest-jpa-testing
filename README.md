# Ejemplo de uso de Spring boot 4.1.1 con JPA H2 MVC Test API y Testing

## Requisitos

- **JDK 25** (LTS). Compila también con JDK 21, pero el proyecto está fijado a 25
  (propiedad `java.version` del `pom.xml`).
  - **Importante si tocas el `pom.xml`:** el `maven-compiler-plugin` declara
    explícitamente `annotationProcessorPaths` con Lombok. No lo quites. Sin ese
    bloque, en JDK 25 Lombok deja de generar getters/setters/constructores
    **sin ningún error de Lombok** — el build falla con "cannot find symbol"
    repartido por todo el proyecto, lo que parece (y no es) un problema de
    incompatibilidad de Lombok con JDK 25. La causa real: el descubrimiento
    *implícito* de anotaciones (javac escaneando el classpath de compilación en
    busca de procesadores, que es lo que pasa si no se declara
    `annotationProcessorPaths`) falla en silencio en JDK 25 — con `javac` puro,
    sin Maven de por medio, Lombok funciona perfecto en JDK 25. Declarar el
    processor path de forma explícita (carga de clases aislada, no depende del
    escaneo implícito) es lo que lo arregla, y funciona igual en JDK 21.
- **Maven Wrapper incluido** (`./mvnw`, `./mvnw.cmd`): no hace falta tener Maven
  instalado aparte.
- **Docker** en marcha, necesario para los tests que usan Testcontainers (MariaDB/MySQL).

## Arranque
mvn spring-boot:run

## Acceso swagger
http://localhost:8080/swagger-ui.html
ó
http://localhost:8080/swagger-ui/index.html

### Código JSON de Swagger/OpenApi
http://localhost:8080/v3/api-docs

### Acceso H2 Console (SQL Web DDBB)
http://localhost:8080/h2-console

Pilla el datasource de la bbdd desde el log de arranque de la app:
jdbc:h2:mem:testdb
Test Connection
Y Connect

## Ejecución de migraciones
mvn flyway:migrate

## Tests

mvn test

Los resultados quedan en `target/surefire-reports/TEST-*.xml` (formato JUnit XML).

## Pruebas de aceptación con Cucumber

    scripts/run-cucumber.sh

Ejemplo de pruebas de aceptación en Gherkin con **Cucumber-JVM**
(`io.cucumber:cucumber-java`/`cucumber-spring`/`cucumber-junit-platform-engine`,
gestionadas por el `cucumber-bom`: si se fija `<version>` por dependencia en
vez de importar el BOM, `mvn dependency:tree` falla con
`Could not resolve version conflict` sobre `io.cucumber:messages`).
Viven en `src/test/resources/features/` (`com.inetum.demo.cucumber` para el
glue): un feature en español (`dato.feature`) y su equivalente en inglés
(`dato_en.feature`), ambos con los mismos step definitions, contra el mismo
endpoint `/api/v1/dato` que ya cubren los tests de `ApiControllerTest`/
`ApiControllerWebTestClientTest` (aquí como ejemplo de cómo lanzarlos, no como
sustituto de esos tests).

Se ejecutan también dentro de `mvn test` (los recoge automáticamente
`com.inetum.demo.cucumber.RunCucumberTest`, que es lo que hace por debajo
`scripts/run-cucumber.sh` con `-Dtest`). Informes propios de Cucumber:

- `target/cucumber-report.html`
- `target/cucumber-report.json`

## Cobertura de código y tests (JSON, XML, JUnit y HTML)

    scripts/run-coverage.sh

Genera:

- `target/surefire-reports/TEST-*.xml` — resultados de test en XML/JUnit (los genera Surefire).
- `target/reports/surefire-report.html` — resultados de test en HTML.
- `target/site/jacoco/jacoco.xml` — cobertura de código en XML.
- `target/site/jacoco/index.html` — cobertura de código en HTML, navegable por paquete/clase.
- `target/reports-json/junit-summary.json` — resumen de tests en JSON (por clase y total).
- `target/reports-json/jacoco-summary.json` — resumen de cobertura en JSON (por paquete y total).

## Análisis SonarQube/SonarCloud

    SONAR_HOST_URL=https://sonarcloud.io SONAR_TOKEN=xxx scripts/sonar-scan.sh

`SONAR_HOST_URL` y `SONAR_TOKEN` son obligatorios como variables de entorno
(nunca se guardan en el repo). El informe se ve en el propio servidor SonarQube
(dashboard del proyecto), no como fichero local. Reaprovecha la cobertura de
JaCoCo (`target/site/jacoco/jacoco.xml`), así que conviene haber corrido antes
`scripts/run-coverage.sh` o dejar que `mvn verify` (que ya ejecuta `sonar-scan.sh`)
la regenere.

## Escaneo de vulnerabilidades (Trivy)

    scripts/trivy-scan.sh

Escanea con [Trivy](https://trivy.dev/) el sistema de ficheros (dependencias del
`pom.xml`, `Dockerfile`, secretos) y, si ya la has construido con
`scripts/build-image.sh`, también la imagen Docker. Informes en:

- `target/trivy/fs.json` / `target/trivy/fs.html` — filesystem.
- `target/trivy/image.json` / `target/trivy/image.html` — imagen Docker (si existe).

Variables opcionales (mismo criterio que `scripts/build-image.sh`):
`DOCKER_USER`, `IMAGE_NAME`, `IMAGE_TAG`.

## Imagen Docker

    scripts/build-image.sh   # construye y etiqueta la imagen (latest, versión del pom, hash del commit)
    scripts/push-image.sh    # sube esas mismas tags a Docker Hub (requiere `docker login` antes)

Variables opcionales: `DOCKER_USER` (por defecto `pepesan`), `IMAGE_NAME`
(por defecto `spring-boot-api-rest-jpa-testing`).
