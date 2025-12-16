# Sử dụng Java 21 làm base image (eclipse-temurin là ổn)
FROM eclipse-temurin:21-jdk-alpine

# Cài đặt Maven (Alpine không có sẵn Maven)
RUN apk add --no-cache maven

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file cấu hình Maven
COPY pom.xml .

# Sao chép toàn bộ mã nguồn
COPY src ./src

# Biên dịch ứng dụng
RUN mvn clean package -DskipTests

# Xuất cổng ứng dụng
EXPOSE 22986

# Biến môi trường (Render sẽ gán giá trị thực tế ở dashboard)
ENV SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
ENV SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
ENV SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}

# Lệnh chạy ứng dụng
CMD ["java", "-jar", "target/BlueMoonApp-1.0-SNAPSHOT.jar"]
