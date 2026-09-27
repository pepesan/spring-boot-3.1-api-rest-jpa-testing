#!/usr/bin/env bash
# Construye la imagen Docker de la app y la etiqueta con: latest, la versión del pom.xml
# y el hash corto del último commit.
set -euo pipefail
cd "$(dirname "$0")/.."

DOCKER_USER="${DOCKER_USER:-pepesan}"
IMAGE_NAME="${IMAGE_NAME:-spring-boot-api-rest-jpa-testing}"

POM_VERSION=$(sed -n '/<\/parent>/,/<\/project>/p' pom.xml | grep -m1 -oP '(?<=<version>)[^<]+')
GIT_COMMIT=$(git rev-parse --short HEAD)

IMAGE="${DOCKER_USER}/${IMAGE_NAME}"

echo "Construyendo ${IMAGE} con tags: latest, ${POM_VERSION}, ${GIT_COMMIT}"

docker build \
	-t "${IMAGE}:latest" \
	-t "${IMAGE}:${POM_VERSION}" \
	-t "${IMAGE}:${GIT_COMMIT}" \
	.

echo
echo "Imagenes generadas:"
docker images "${IMAGE}"
