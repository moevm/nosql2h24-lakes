# Используем образ Java 17
FROM openjdk:23-jdk-slim as build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и maven wrapper
COPY pom.xml .
COPY mvnw .      
COPY mvnw.cmd .  
COPY src ./src

# Устанавливаем зависимости
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Собираем проект
RUN ./mvnw package -DskipTests

# Используем образ для запуска приложения
FROM openjdk:23-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный jar из предыдущего этапа
COPY --from=build /app/target/lake-catalog-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose port
EXPOSE 8081
