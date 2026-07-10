# 共享账本后端仓库

基于 Spring Boot 3.4.9 的多人协作账本管理系统，支持消费记录追踪、实时通知和 AI 智能分析。

## 核心功能

- 📊 **账本管理** - 创建、管理多个账本，支持多人共享
- 💰 **消费记录** - 记录和查询消费明细，支持分类统计
- 👥 **多人协作** - 邀请成员共享账本，支持角色权限（创建者/管理员/成员）
- 🤖 **AI 分析** - 集成 AI 大模型，智能分析消费数据（流式响应）
- 🔔 **实时通知** - 基于 WebSocket 的消息推送
- 🔐 **安全认证** - JWT Token 身份验证

## 技术栈

- **后端框架**: Spring Boot 3.4.9 + Spring WebSocket + Spring WebFlux
- **数据库**: MySQL 8.0 + MyBatis-Flex + Redis
- **安全认证**: JWT
- **第三方服务**: 阿里云 OSS、硅基流动 AI API
- **构建工具**: Gradle (Kotlin DSL)
- **开发工具**: Lombok + MapStruct + Hutool

## 快速开始

### 克隆仓库
```bash
git clone https://github.com/Siers1/BillSyncer_backend.git
cd BillSyncer_backend
```

### 环境要求
- JDK 21+
- MySQL 8.0+
- Redis 6.0+
- Gradle 8.x+

### 初始化数据库
```bash
mysql -u root -p < db.sql
```

### 修改配置
编辑 `src/main/resources/application.yml`，配置数据库、Redis、OSS 和 AI API 信息

### 运行
```bash
./gradlew bootRun
```

### Docker 构建镜像
```bash
# 构建
./gradlew bootJar
docker build -t consumption-bill .

# 运行
docker run -d -p 8080:8080 \
  -e MYSQL_HOST=your_mysql_host \
  -e MYSQL_USERNAME=root \
  -e MYSQL_PASSWORD=your_password \
  -e REDIS_HOST=your_redis_host \
  -e REDIS_PASSWORD=your_redis_password \
  --name consumption-bill \
  consumption-bill
```

## 作者

**Siersi**

---

⭐ 如果这个项目对您有帮助，欢迎 Star！
