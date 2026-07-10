import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("org.springframework.boot") version "3.4.9"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

group = "com.siersi"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
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

    // 工具库
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Lombok 与 MapStruct 的绑定器（关键！让两者协同工作）
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

// 配置 annotationProcessor 路径（等效于 Maven 的 maven-compiler-plugin 配置）
tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
    options.compilerArgs = listOf(
        "-parameters"  // 保留参数名称，用于 Spring 参数绑定
    )
}

// Spring Boot 打包配置
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    archiveFileName.set("${project.name}.jar")
}

// 排除 Lombok（Spring Boot 打包时不需要）
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    excludes.add("org/projectlombok/**")
}

// 测试配置
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// 可选：配置 Spring Boot 运行参数
tasks.withType<BootRun> {
    jvmArgs = listOf(
        "-Dfile.encoding=UTF-8"
    )
}

// 可选：配置构建信息
tasks.register("buildInfo") {
    doLast {
        println("Building ${project.name} version ${project.version}")
    }
}