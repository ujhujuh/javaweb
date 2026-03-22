# JavaWeb 后端开发规范 Skill

## 命名规范

**⭐ 类名 - PascalCase**
- Entity: SysUser 
- Service: SysUserService 
- Controller: SysUserController
- Mapper: SysUserMapper 
- DTO: LoginDTO 
- 异常: BusinessException

**⭐ 方法 - camelCase**
- 查询: selectUserList()、getById()
- 新增: insertUser() 
- 修改: updateUser() 
- 删除: deleteUserByIds()

**⭐ 数据库 - snake_case**
- 表名: sys_user（必须 sys_ 前缀）
- 字段: user_id、create_time
- 主键: 统一 id
- 公共字段: create_time、create_by、update_time、update_by

## 必用注解

**⭐ Controller**
- @RestController + @RequestMapping
- @ApiLog("操作描述") - 记录日志（必加）
- @RequiresPermissions("system:user:list") - 权限控制（必加）
- @GetMapping/@PostMapping/@PutMapping/@DeleteMapping

**⭐ Entity**
- @TableName("sys_user") - 表名映射
- @TableId(type = IdType.AUTO) - 主键自增
- @TableField(fill = FieldFill.INSERT) - 插入时自动填充
- @Data - Lombok注解

**⭐ Service**
- @Service - 标记为服务
- @Transactional(rollbackFor = Exception.class) - 事务控制（必加）
- 继承 ServiceImpl<Mapper, Entity>
- 实现 Service 接口

## 层级职责

Controller (接收请求、校验、权限) 
→ Service (业务逻辑、事务) 
→ Mapper (数据操作) 
→ Database

**Controller职责**
- 接收HTTP请求
- 参数校验和权限验证
- 调用Service层
- 返回统一结果
- 记录操作日志

**Service职责**
- 实现业务逻辑
- 事务管理
- 调用Mapper层
- 抛出业务异常
- 数据转换和封装

**Mapper职责**
- 数据库操作
- SQL查询
- 继承BaseMapper

## 统一返回格式

- 成功: Result.success(data)
- 成功无数据: Result.success()
- 失败: Result.failed(message)
- 业务异常: throw new BusinessException("错误信息")

## RESTful API 设计

- GET /api/system/user/list - 分页查询
- GET /api/system/user/{id} - 根据ID查询
- POST /api/system/user - 新增
- PUT /api/system/user - 修改
- DELETE /api/system/user/{ids} - 删除

## 入参出参规范

**Controller层**
- 入参超过2个: 使用 @RequestBody 对象
- 单个参数: 使用 @PathVariable 或 @RequestParam
- 列表查询: 使用 QueryDTO（包含 current、size）
- 新增/修改: 使用 Entity 或 DTO

**Service层**
- 查询: 返回 Entity、VO 或 Page
- 新增/修改/删除: 返回 boolean
- 复杂业务: 返回自定义 DTO

## 数据库设计规范

**表设计**
- 表名: sys_user（sys_ 前缀，snake_case）
- 主键: id（BIGINT 自增）
- 公共字段: create_time、create_by、update_time、update_by、del_flag、status

**字段类型**
- 字符串: VARCHAR
- 数字: BIGINT（主键）、INT、DECIMAL（金额）
- 日期时间: DATETIME
- 文本: TEXT
- 布尔: TINYINT（0/1）

**索引规范**
- 主键索引: 自动创建
- 唯一索引: 业务唯一字段（如 username）
- 普通索引: 常用查询字段（如 status、create_time）

## 检查清单

**⭐ 后端必查**
- 类名PascalCase，方法名camelCase
- Controller: @RestController + @ApiLog + @RequiresPermissions
- Service: @Transactional(rollbackFor = Exception.class)
- Entity: @TableName + @TableId
- 返回: Result.success() / Result.failed()
- 异常: throw new BusinessException()
- 表名: sys_ 前缀
- 公共字段: create_time/create_by/update_time/update_by

## 常见问题

- **Q: 参数超过2个？** → 用 @RequestBody 对象
- **Q: 业务异常？** → throw new BusinessException()
- **Q: 分页查询？** → Page对象 + IPage返回
- **Q: 密码加密？** → BCrypt.hashpw() 加密，BCrypt.checkpw() 验证
- **Q: 操作日志？** → @ApiLog 注解
- **Q: 事务控制？** → @Transactional(rollbackFor = Exception.class)
