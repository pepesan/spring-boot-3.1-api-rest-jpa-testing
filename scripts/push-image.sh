#!/usr/bin/env bash
# Sube a Docker Hub las tags generadas por build-image.sh: latest, la versión del pom.xml
# y el hash corto del último commit. Requiere haber hecho antes `docker login`.
set -euo pipefail
cd "$(dirname "$0")/.."

DOCKER_USER="${DOCKER_USER:-pepesan}"
IMAGE_NAME="${IMAGE_NAME:-spring-boot-api-rest-jpa-testing}"

POM_VERSION=$(sed -n '/<\/parent>/,/<\/project>/p' pom.xml | grep -m1 -oP '(?<=<version>)[^<]+')
GIT_COMMIT=$(git rev-parse --short HEAD)

IMAGE="${DOCKER_USER}/${IMAGE_NAME}"

for TAG in latest "${POM_VERSION}" "${GIT_COMMIT}"; do
	echo "Subiendo ${IMAGE}:${TAG}..."
	docker push "${IMAGE}:${TAG}"
done
