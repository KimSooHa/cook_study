# jdk:11 Image Start
FROM openjdk:11-jdk-slim

# 애플리케이션 디렉토리 설정
WORKDIR /app

# 인자 설정 - JAR_FILE
ARG JAR_FILE=build/libs/*.jar
# jar 파일 복제
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]