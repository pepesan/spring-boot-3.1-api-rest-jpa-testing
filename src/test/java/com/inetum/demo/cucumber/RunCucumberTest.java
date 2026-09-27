package com.inetum.demo.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Punto de entrada para que Surefire/JUnit Platform descubra y ejecute las
 * features de Cucumber (src/test/resources/features) con el glue de
 * com.inetum.demo.cucumber. Sin esta clase, cucumber-junit-platform-engine no
 * tiene ni el classpath a escanear ni el paquete de step definitions.
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.inetum.demo.cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-report.html, json:target/cucumber-report.json")
public class RunCucumberTest {
}
