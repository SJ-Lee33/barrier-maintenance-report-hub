FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# Gradle build로 만들어진 jar 파일을
# 컨테이너 안의 /app/app.jar로 복사한 뒤
# java -jar app.jar로 실행한다.