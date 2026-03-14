# JavaWeb 管理系统

一个基于 Spring Boot + MyBatis-Plus + Shiro + Vue 3 的企业级 Web 应用框架。

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- Shiro 1.13.0
- MySQL 8.0
- Fastjson 2.0.43
- Hutool 5.8.23

### 前端
- Vue 3.3.4
- Vite 4.4.9
- Element Plus 2.3.14
- Axios 1.5.0
- Pinia 2.1.6
- Vue Router 4.2.4

## 功能模块

- 用户注册/登录
- 用户管理
- 角色管理
- 菜单管理
- 字典管理
- 参数设置
- 通知公告
- 操作日志
- 登录日志

## 项目结构

```
JavaWeb/
├── src/main/java/com/example/javaweb/
│   ├── config/          # 配置类
│   ├── controller/      # 控制层
│   ├── service/         # 业务层
│   ├── mapper/          # DAO层
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── shiro/           # Shiro相关
│   ├── util/            # 工具类
│   └── common/          # 公共包
│       ├── exception/   # 异常处理
│       ├── result/      # 响应工具类
│       ├── log/         # 日志注解
│       └── base/        # 基类
├── src/main/resources/
│   ├── application.properties  # 配置文件
│   └── init.sql                # 数据库初始化脚本
├── frontend/             # 前端项目
│   ├── src/
│   │   ├── views/       # 页面
│   │   ├── components/  # 组件
│   │   ├── router/      # 路由
│   │   ├── store/       # 状态管理
│   │   ├── api/         # 接口
│   │   └── utils/       # 工具类
│   └── package.json
└── pom.xml
```

## 快速开始

### 1. 数据库初始化

确保已安装 MySQL 8.0+，然后执行 `init.sql` 脚本：

```bash
mysql -u root -p < init.sql
```

默认管理员账号：
- 用户名：admin
- 密码：123456

### 2. 后端启动

```bash
# 进入项目根目录
cd JavaWeb

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 3. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

## API 接口

### 认证接口

- POST `/api/auth/login` - 用户登录
- POST `/api/auth/register` - 用户注册
- POST `/api/auth/logout` - 用户退出

### 系统管理接口

- GET `/api/system/user/list` - 分页查询用户
- GET `/api/system/user/{id}` - 根据ID查询用户
- POST `/api/system/user` - 新增用户
- PUT `/api/system/user` - 修改用户
- DELETE `/api/system/user/{id}` - 删除用户

其他接口类似...

## 开发规范

### 后端开发规范

1. 统一使用 `@Autowired` 注入 Bean
2. 统一使用 `Result<T>` 返回结果
3. 统一使用 `@ApiLog` 注解记录日志
4. 统一使用 `@RequiresPermissions` 注解控制权限
5. 统一使用 `@Valid` 注解进行参数校验

### 前端开发规范

1. 使用 Composition API (setup语法糖)
2. 使用 Pinia 进行状态管理
3. 使用 Element Plus 组件库
4. 遵循 Vue 3 官方风格指南

## 许可证

MIT License