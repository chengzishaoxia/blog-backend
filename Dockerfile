FROM eclipse-temurin:17-jre-alpine

ARG JAR_FILE=wing-blog.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-Xms512m -Xmx512m" \
    TZ=Asia/Shanghai \
    SPRING_PROFILES_ACTIVE=prod

EXPOSE 8989 18080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Djava.security.egd=file:/prod/./urandom -jar app.jar"]
