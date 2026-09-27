#!/usr/bin/env bash
# Ejecuta los tests con cobertura y deja los informes en target/ en JSON, XML,
# JUnit XML y HTML:
#   - target/surefire-reports/TEST-*.xml   -> resultados de test en XML/JUnit (los genera Surefire)
#   - target/reports/surefire-report.html  -> resultados de test en HTML
#   - target/site/jacoco/jacoco.xml        -> cobertura de código en XML
#   - target/site/jacoco/index.html        -> cobertura de código en HTML
#   - target/reports-json/junit-summary.json   -> resumen de tests en JSON
#   - target/reports-json/jacoco-summary.json  -> resumen de cobertura en JSON
set -euo pipefail
cd "$(dirname "$0")/.."

./mvnw -B clean test
./mvnw -B surefire-report:report-only -DoutputName=surefire-report

python3 scripts/reports/xml_to_json.py

echo
echo "Informes generados:"
echo "  - target/surefire-reports/TEST-*.xml"
echo "  - target/reports/surefire-report.html"
echo "  - target/site/jacoco/jacoco.xml"
echo "  - target/site/jacoco/index.html"
echo "  - target/reports-json/junit-summary.json"
echo "  - target/reports-json/jacoco-summary.json"
