#!/usr/bin/env bash
# Ejecuta solo las pruebas de aceptación de Cucumber (features en español e
# inglés bajo src/test/resources/features), sin el resto de la suite.
# Informes:
#   - target/cucumber-report.html
#   - target/cucumber-report.json
set -euo pipefail
cd "$(dirname "$0")/.."

./mvnw -B clean test -Dtest=com.inetum.demo.cucumber.RunCucumberTest

echo
echo "Informes: target/cucumber-report.html, target/cucumber-report.json"
