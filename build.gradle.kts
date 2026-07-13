plugins {
    id("org.springframework.boot") version "3.4.9"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

group = "com.siersi"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // 数据库相关
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("com.zaxxer:HikariCP")

    // MyBatis-Flex
    implementation("com.mybatis-flex:mybatis-flex-spring-boot3-starter:1.11.1")
    annotationProcessor("com.mybatis-flex:mybatis-flex-processor:1.11.1")

    // 工具库
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Lombok 与 MapStruct 的绑定器
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // Hutool
    implementation("cn.hutool:hutool-all:5.8.40")

    // 阿里云 OSS
    implementation("com.aliyun.oss:aliyun-sdk-oss:3.17.4")

    // Redisson (Redis 客户端)
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")
    implementation("org.apache.commons:commons-pool2")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
    options.compilerArgs = listOf(
        "-parameters",
        "-Amapstruct.unmappedTargetPolicy=IGNORE"
    )
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    excludes.add("org/projectlombok/**")
}

tasks.withType<Test> {
    useJUnitPlatform()
}