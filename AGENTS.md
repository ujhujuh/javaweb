# JavaWeb 项目文档

## 项目概述

这是一个基于 Spring Boot + MyBatis-Plus + Shiro + Vue 3 的企业级 Web 应用管理系统。项目采用前后端分离架构，提供完整的用户管理、角色管理、菜单管理、字典管理、参数设置、通知公告、操作日志、登录日志和在线用户等功能。

**主要技术栈：**

**后端：**
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- Apache Shiro 1.10.1
- JWT (jjwt 0.9.1)
- MySQL 8.0.33
- Druid 1.2.20
- Knife4j 3.0.3
- Fastjson 2.0.43
- Hutool 5.8.23
- EasyExcel 3.3.2

**前端：**
- Vue 3.3.4
- Vue Router 4.2.4
- Pinia 2.1.6
- Element Plus 2.3.14
- Axios 1.5.0
- Vite 4.4.9

**项目架构：**
- 前后端分离架构
- RESTful API 设计
- JWT 认证授权
- Druid 数据库连接池监控
- Knife4j API 文档

## 项目结构

```
JavaWeb/
├── src/                           # 源代码目录
│   └── main/
│       ├── java/                  # Java 源代码
│       │   └── com/example/javaweb/
│       │       ├── App.java       # Spring Boot 主应用程序入口
│       │       ├── common/        # 公共模块
│       │       │   ├── base/      # 基础类
│       │       │   ├── exception/ # 异常处理
│       │       │   ├── log/       # 日志切面
│       │       │   └── result/    # 统一响应结果
│       │       ├── config/        # 配置类
│       │       ├── controller/    # 控制器层
│       │       ├── dto/           # 数据传输对象
│       │       ├── entity/        # 实体类
│       │       ├── mapper/        # MyBatis Mapper
│       │       ├── service/       # 服务层
│       │       ├── shiro/         # Shiro 配置
│       │       ├── util/          # 工具类
│       │       └── vo/            # 视图对象
│       └── resources/             # 资源文件
│           ├── application.properties    # 应用配置
│           └── mapper/                   # MyBatis XML 映射文件
├── frontend/                      # 前端项目
│   ├── src/
│   │   ├── api/                   # API 接口
│   │   ├── components/            # 组件
│   │   ├── router/                # 路由配置
│   │   ├── store/                 # 状态管理
│   │   ├── utils/                 # 工具函数
│   │   ├── views/                 # 页面组件
│   │   ├── App.vue                # 根组件
│   │   └── main.js                # 入口文件
│   ├── index.html                 # HTML 模板
│   ├── package.json               # NPM 依赖配置
│   └── vite.config.js             # Vite 配置
├── init.sql                       # 数据库初始化脚本
├── pom.xml                        # Maven 配置
├── .gitignore                     # Git 忽略规则
└── README.md                      # 项目说明
```

## 开发流程

### 首次启动

1. **初始化数据库**
   ```bash
   mysql -u root -p < init.sql
   ```

2. **启动后端服务**
   ```bash
   mvn spring-boot:run
   ```
   后端服务将运行在 `http://localhost:8080`

3. **启动前端服务**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   前端服务将运行在 `http://localhost:3000`

4. **访问系统**
   - 打开浏览器访问：`http://localhost:3000`
   - 使用 admin 账号登录（密码需要查看数据库或初始化脚本）

### 日常开发

1. **启动服务**
   - 后端：`mvn spring-boot:run`
   - 前端：`cd frontend && npm run dev`

2. **查看 API 文档**
   - 登录系统后，首页会显示"接口文档"卡片（仅超级管理员）
   - 或直接访问：`http://localhost:3000/doc.html`

3. **查看数据库监控**
   - 登录系统后，首页会显示"数据监控"卡片（仅超级管理员）
   - 或直接访问：`http://localhost:3000/druid/index.html`

4. **调试问题**
   - 打开浏览器开发者工具（F12）
   - 查看 Console 标签的日志输出
   - 查看 Network 标签的 API 请求

## 构建和运行

### 后端

#### 编译项目

```bash
# 使用 Maven 编译
mvn clean compile
```

#### 运行项目

```bash
# 使用 Maven 运行
mvn spring-boot:run

# 或使用 java 命令运行打包后的 jar
mvn clean package
java -jar target/JavaWeb-1.0.0.jar
```

#### 清理项目

```bash
# 清理 Maven 构建产物
mvn clean
```

### 前端

#### 安装依赖

```bash
cd frontend
npm install
```

#### 开发模式

```bash
cd frontend
npm run dev
```

前端开发服务器运行在 `http://localhost:3000`

#### 构建生产版本

```bash
cd frontend
npm run build
```

#### 预览生产版本

```bash
cd frontend
npm run preview
```

## 开发环境配置

### 后端配置

后端配置文件位于 `src/main/resources/application.properties`：

```properties
# 服务器端口
server.port=8080

# 数据库配置
spring.datasource.druid.url=jdbc:mysql://localhost:3306/javaweb
spring.datasource.druid.username=javaweb
spring.datasource.druid.password=sf123456

# JWT 配置
jwt.secret=javaweb_secret_key_2024
jwt.expiration=86400000
```

### 前端配置

前端代理配置位于 `frontend/vite.config.js`：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/doc.html': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/v2/api-docs': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/v3/api-docs': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/swagger-resources': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/webjars': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/favicon.ico': {
      target: 'http://localhost:8080',
      changeOrigin: true
    },
    '/druid': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

代理配置确保所有请求都通过前端开发服务器（3000端口）转发到后端服务（8080端口），无需修改访问地址。

## 系统功能

### 核心功能模块

1. **用户管理**
   - 用户增删改查
   - 用户状态管理
   - 密码重置
   - 角色授权

2. **角色管理**
   - 角色增删改查
   - 菜单授权
   - 用户分配
   - 权限管理

3. **菜单管理**
   - 菜单树结构管理
   - 按钮权限控制
   - 图标配置

4. **字典管理**
   - 字典类型管理
   - 字典数据管理
   - 字典缓存

5. **参数设置**
   - 系统参数配置
   - 参数缓存刷新

6. **通知公告**
   - 公告发布
   - 公告分类（通知/公告）
   - 阅读状态跟踪

7. **操作日志**
   - 操作记录
   - 日志查询
   - 日志清理

8. **登录日志**
   - 登录记录
   - 登录状态
   - 异常登录监控

9. **在线用户**
   - 在线用户监控
   - 强制下线

### 开发工具

1. **Knife4j API 文档**
   - 访问地址：`http://localhost:3000/doc.html`
   - 仅超级管理员可见
   - 提供 API 接口在线测试

2. **Druid 数据库监控**
   - 访问地址：`http://localhost:3000/druid/index.html`
   - 仅超级管理员可见
   - 监控 SQL 执行、连接池状态等

### 首页设计

首页采用现代化的渐变卡片设计，提供丰富的信息展示：

1. **统计卡片**（仅超级管理员可见）
   - 用户总数：显示系统用户数量，带有"活跃用户"标签
   - 菜单总数：显示系统菜单数量，带有"系统菜单"标签
   - 未读公告：显示当前用户未读的公告数量，带有"待处理"标签
   - 卡片使用渐变背景，悬停时有上移和阴影加深效果

2. **系统工具卡片**（仅超级管理员可见）
   - 接口文档：打开 Knife4j API 文档页面
   - 数据监控：打开 Druid 数据库监控页面
   - 使用紫色和绿色渐变背景，带有图标、描述和在线状态指示器

3. **欢迎卡片**
   - 显示系统欢迎信息和技术栈介绍
   - 9 个功能模块网格展示，每个模块都有独特的渐变色图标
   - 使用紫色渐变背景，带有浮动动画效果

4. **最新公告卡片**
   - 显示最新发布的公告列表
   - 未读公告有特殊标识和背景色
   - 支持点击查看公告详情，自动标记为已读

### 权限控制

系统采用基于角色的权限控制（RBAC）：

- **超级管理员（admin）**：拥有所有权限，可以看到首页统计卡片和系统工具
- **普通用户**：只能看到已授权的菜单和功能，首页只显示欢迎卡片和公告列表

权限通过 Shiro 框架实现，支持菜单级别的权限控制。

## 开发约定

### 代码风格

**Java：**
- 使用标准的 Java 命名约定
- 类名使用 PascalCase
- 方法名和变量名使用 camelCase
- 常量使用 UPPER_CASE
- 使用 Lombok 简化代码

**Vue：**
- 组件名使用 PascalCase
- 文件名使用 kebab-case
- 使用 Composition API（setup 语法糖）
- 统一使用 Element Plus 组件库
- 使用 Pinia 进行状态管理
- 使用 Axios 进行 HTTP 请求
- API 接口统一放在 `src/api` 目录
- 公共组件放在 `src/components` 目录
- 页面组件放在 `src/views` 目录
- 使用 `@/` 别名引用 `src` 目录

### API 设计

- 使用 RESTful 风格
- 统一响应格式：`{ code, message, data }`
- 统一异常处理
- 使用 JWT 进行认证

### 数据库设计

- 表名使用 `sys_` 前缀
- 字段使用 snake_case 命名
- 主键统一使用 `id`
- 必须包含 `create_time`、`create_by`、`update_time`、`update_by` 字段

### 版本控制

- 使用 Git 进行版本控制
- 使用 `.gitignore` 忽略：
  - `target/` 目录（Maven 构建产物）
  - `frontend/node_modules/` 目录（NPM 依赖）
  - `.idea/` 配置（IDE 配置）
  - `.DS_Store` 文件（macOS 系统文件）

## 部署说明

### 数据库初始化

```bash
# 执行数据库初始化脚本
mysql -u root -p < init.sql
```

### 生产环境配置

1. 修改 `application.properties` 中的数据库配置
2. 修改 JWT 密钥
3. 配置日志输出路径
4. 配置文件上传路径

### Docker 部署（可选）

项目支持 Docker 部署，可以创建 `Dockerfile` 进行容器化部署。

## 常见问题

### 1. 数据库连接失败

检查数据库配置是否正确，确保数据库服务已启动。
- 确认数据库地址、端口、用户名、密码正确
- 检查数据库服务是否正在运行
- 确认防火墙是否允许连接

### 2. 前端代理失效

确保后端服务运行在 8080 端口，前端开发服务器运行在 3000 端口。
- 检查后端服务是否已启动：访问 `http://localhost:8080`
- 检查前端服务是否已启动：访问 `http://localhost:3000`
- 修改 vite.config.js 后需要重启前端开发服务器

### 3. JWT Token 过期

默认 Token 有效期为 24 小时，可在配置文件中修改。
- Token 过期后需要重新登录
- 可在 `application.properties` 中修改 `jwt.expiration` 配置

### 4. 菜单总数/未读公告显示为 0

- 确保使用 admin 账号登录（统计功能仅超级管理员可见）
- 检查浏览器控制台是否有 API 调用错误（F12 -> Console）
- 确保数据库中有对应的菜单和公告数据
- 查看控制台日志，确认 API 返回的数据格式是否正确

### 5. Knife4j 文档页面无法加载

- 确保使用 admin 账号登录
- 检查前端代理配置是否完整（需要代理 `/v2/api-docs`、`/swagger-resources`、`/webjars` 等）
- 修改 vite.config.js 后需要重启前端开发服务器
- 检查后端 Knife4j 配置是否正确

### 6. Druid 监控页面无法访问

- 确保使用 admin 账号登录
- 检查前端代理配置中是否包含 `/druid` 路径
- Druid 监控页面默认账号密码：admin/admin123
- 检查后端 Druid 配置是否正确

## 扩展建议

1. **添加单元测试**：使用 JUnit 和 Mockito 进行单元测试
2. **添加集成测试**：使用 Spring Boot Test 进行集成测试
3. **添加 CI/CD**：使用 GitHub Actions 或 Jenkins 进行持续集成
4. **添加日志管理**：集成 ELK 或其他日志管理系统
5. **添加缓存**：使用 Redis 提高系统性能
6. **添加消息队列**：使用 RabbitMQ 或 Kafka 处理异步任务
7. **添加监控**：集成 Prometheus 和 Grafana 进行系统监控

## 注意事项

- **JDK 版本**：项目使用 Java 8，确保 JDK 版本正确（JDK 1.8.x）
- **数据库版本**：数据库使用 MySQL 8.0，注意驱动版本兼容性
- **Node.js 环境**：前端开发需要 Node.js 16+ 环境
- **生产环境安全**：
  - 部署时请修改默认密码和密钥
  - 修改数据库连接配置
  - 修改 JWT 密钥
  - 修改 Druid 监控页面账号密码
- **Druid 监控**：
  - 默认账号密码：admin/admin123
  - 建议在生产环境中修改默认密码
  - 可以通过配置 `allow` 参数限制访问 IP
- **超级管理员权限**：
  - 默认超级管理员用户名为 `admin`
  - 超级管理员可以看到首页统计卡片和系统工具
  - 普通用户只能看到已授权的菜单和功能
- **前端代理**：
  - 修改 `vite.config.js` 后需要重启前端开发服务器
  - 确保所有必要的代理路径都已配置
  - 开发环境使用代理，生产环境需要配置 Nginx 反向代理
- **日志级别**：
  - 开发环境 MyBatis 日志输出到控制台
  - 生产环境建议调整日志级别为 INFO 或 WARN

## Agent 协作约定

1. 默认使用中文回复。
2. 每次修改完代码后，必须自行执行验证（如编译、测试或关键流程检查）并反馈验证结果。
3. 严格遵循分层职责：Controller 层禁止编写业务逻辑，仅负责参数接收/校验、权限控制、调用 Service、返回统一响应；业务逻辑、事务处理与数据组装必须下沉到 Service 层。
4. 所有分页查询接口必须统一使用分页查询对象封装 `current`、`size`（统一继承 `PageQueryDTO`），禁止在 Controller 方法中零散声明分页参数。
5. Controller 接口入参超过 2 个时，必须封装为请求对象（QueryDTO/RequestDTO）进行接收，避免参数平铺。
6. 接口的用户身份获取遵循项目统一方式：登录态接口优先通过 Shiro `SecurityUtils.getSubject().getPrincipal()` 获取当前用户；仅匿名可访问场景允许保留可选 token 解析作为补充。
