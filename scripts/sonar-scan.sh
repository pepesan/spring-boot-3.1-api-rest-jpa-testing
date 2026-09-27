#!/usr/bin/env bash
# Lanza el análisis de SonarQube/SonarCloud (mvn verify + sonar:sonar), reutilizando
# el informe de cobertura de JaCoCo (target/site/jacoco/jacoco.xml, generado en la
# fase test). El informe final se ve en el propio servidor SonarQube (dashboard),
# no como fichero local.
#
# Variables de entorno requeridas (nunca se fijan en el pom ni en este script:
# son secretos / la URL de un servidor real):
#   SONAR_HOST_URL  - p.ej. https://sonarcloud.io o la URL de tu SonarQube
#   SONAR_TOKEN     - token de autenticación
# Opcional:
#   SONAR_PROJECT_KEY - si se quiere sobrescribir el sonar.projectKey del pom.xml
set -euo pipefail
cd "$(dirname "$0")/.."

if [[ -z "${SONAR_HOST_URL:-}" || -z "${SONAR_TOKEN:-}" ]]; then
	echo "Error: hay que definir SONAR_HOST_URL y SONAR_TOKEN como variables de entorno." >&2
	echo "Ejemplo: SONAR_HOST_URL=https://sonarcloud.io SONAR_TOKEN=xxx scripts/sonar-scan.sh" >&2
	exit 1
fi

EXTRA_ARGS=()
if [[ -n "${SONAR_PROJECT_KEY:-}" ]]; then
	EXTRA_ARGS+=("-Dsonar.projectKey=${SONAR_PROJECT_KEY}")
fi

./mvnw -B verify sonar:sonar \
	-Dsonar.host.url="${SONAR_HOST_URL}" \
	-Dsonar.token="${SONAR_TOKEN}" \
	-Dsonar.qualitygate.wait=true \
	"${EXTRA_ARGS[@]}"
