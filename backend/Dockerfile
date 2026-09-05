# ========== 阶段 1：Maven 构建 ==========
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 先复制 pom.xml，利用 Docker 层缓存（依赖不变就跳过 mvn dependency:go-offline）
COPY pom.xml .
RUN mvn dependency:go-offline -q

# 再复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -q

# ========== 阶段 2：运行 ==========
FROM eclipse-temurin:17-jre-alpine AS runtime

# 创建非 root 用户（安全最佳实践）
RUN addgroup -S app && adduser -S appuser -G app

WORKDIR /app

# 只拷贝打好的 JAR（比整个 target 目录小很多）
COPY --from=builder /app/target/library-management-system-1.0.0.jar app.jar

RUN chown -R appuser:app /app
USER appuser

EXPOSE 8080

# JVM 调优（容器环境下堆内存默认可能太大）
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
