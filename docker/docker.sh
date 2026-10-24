#!/bin/bash
set -euo pipefail

# 基本变量
JAR_FILE="wing-blog.jar"
IMAGE_NAME="wing-blog"
CONTAINER_NAME="wing-blog"

# 进入构建输出目录（假设 jar 已经在 target 下）
cd target

echo "Current directory: $(pwd)"
[ -f "$JAR_FILE" ] || { echo "Jar $JAR_FILE not found. Please run 'mvn package' first."; exit 1; }

version="eva"

cat > Dockerfile <<'EOF'
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=wing-blog.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS="-Xms512m -Xmx512m" \
    TZ=Asia/Shanghai \
    SPRING_PROFILES_ACTIVE=prod
EXPOSE 8989 18080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
EOF

echo "Stopping and removing old container and image (if they exist)"
docker stop "${CONTAINER_NAME}" >/dev/null 2>&1 || true
docker rm "${CONTAINER_NAME}" >/dev/null 2>&1 || true
docker rmi "${IMAGE_NAME}:${version}" >/dev/null 2>&1 || true

echo "Building image: ${IMAGE_NAME}:${version}"
docker build --build-arg JAR_FILE="$JAR_FILE" -t "${IMAGE_NAME}:${version}" .


echo "Starting new container"
docker run -d \
  --name "${CONTAINER_NAME}" \
  -e ENABLE_DEBUG=true \
  -p 8989:8989 -p 18080:18080 \
  "${IMAGE_NAME}:${version}"

echo "Done. Current image: ${IMAGE_NAME}:${version}"
