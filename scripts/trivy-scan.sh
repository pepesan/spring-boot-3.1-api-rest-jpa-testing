#!/usr/bin/env bash
# Escanea vulnerabilidades con Trivy:
#   - del sistema de ficheros (dependencias de pom.xml, IaC, secretos) -> target/trivy/fs.*
#   - de la imagen Docker generada por build-image.sh (si existe)     -> target/trivy/image.*
# Genera informes en JSON y HTML (con la plantilla oficial de Trivy en
# scripts/trivy/html.tpl), muestra la tabla resumen por pantalla, y AL FINAL
# falla (exit 1) si se ha encontrado alguna vulnerabilidad CRITICAL o HIGH con
# fix disponible — es una gate, no solo un informe.
set -uo pipefail
cd "$(dirname "$0")/.."

DOCKER_USER="${DOCKER_USER:-pepesan}"
IMAGE_NAME="${IMAGE_NAME:-spring-boot-api-rest-jpa-testing}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
IMAGE="${DOCKER_USER}/${IMAGE_NAME}:${IMAGE_TAG}"

OUT_DIR="target/trivy"
TEMPLATE="scripts/trivy/html.tpl"
GATE_SEVERITY="CRITICAL,HIGH"
mkdir -p "$OUT_DIR"

GATE_FAILED=0

echo "== Trivy: sistema de ficheros (dependencias, IaC, secretos) =="
trivy fs --scanners vuln,misconfig,secret --format table .
trivy fs --scanners vuln,misconfig,secret --format json --output "${OUT_DIR}/fs.json" .
trivy fs --scanners vuln,misconfig,secret --format template --template "@${TEMPLATE}" --output "${OUT_DIR}/fs.html" .
echo "Informes: ${OUT_DIR}/fs.json, ${OUT_DIR}/fs.html"

trivy fs --scanners vuln --exit-code 1 --severity "$GATE_SEVERITY" --quiet . >/dev/null
if [[ $? -ne 0 ]]; then
	echo "GATE: vulnerabilidades ${GATE_SEVERITY} en el filesystem." >&2
	GATE_FAILED=1
fi

if docker image inspect "$IMAGE" >/dev/null 2>&1; then
	echo
	echo "== Trivy: imagen Docker ${IMAGE} =="
	trivy image --format table "$IMAGE"
	trivy image --format json --output "${OUT_DIR}/image.json" "$IMAGE"
	trivy image --format template --template "@${TEMPLATE}" --output "${OUT_DIR}/image.html" "$IMAGE"
	echo "Informes: ${OUT_DIR}/image.json, ${OUT_DIR}/image.html"

	trivy image --exit-code 1 --severity "$GATE_SEVERITY" --quiet "$IMAGE" >/dev/null
	if [[ $? -ne 0 ]]; then
		echo "GATE: vulnerabilidades ${GATE_SEVERITY} en la imagen Docker." >&2
		GATE_FAILED=1
	fi
else
	echo
	echo "Imagen ${IMAGE} no encontrada localmente: omitido el escaneo de imagen."
	echo "Constrúyela antes con scripts/build-image.sh si quieres incluirla."
fi

if [[ "$GATE_FAILED" -eq 1 ]]; then
	echo
	echo "FALLO: hay vulnerabilidades ${GATE_SEVERITY} con corrección disponible. Ver los informes en ${OUT_DIR}/." >&2
	exit 1
fi
