package com.inetum.demo.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;

/**
 * Arranca un único contexto Spring (servidor en puerto aleatorio) compartido por
 * todas las clases de steps de Cucumber de este módulo.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CucumberSpringConfiguration {
}
