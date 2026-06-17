# 喵伴守护 - CatGuardian

> 流浪猫救助与领养平台后端服务

## 项目简介

喵伴守护是一个基于 Spring Boot 构建的流浪猫救助与领养平台后端服务，提供完整的猫咪档案管理、领养流程、TNR绝育管理、上门喂养服务预约、社区互动等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 编程语言 |
| Spring Boot | 3.2.5 | 后端框架 |
| Spring Security | 6.x | 认证授权 |
| Spring Data JPA | 3.2.x | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Redis | - | 缓存/验证码存储 |
| JWT | 0.12.5 | Token认证 |
| Lombok | - | 简化代码 |
| Maven | 3.9+ | 项目构建 |

## 项目结构

```
backend/
├── src/main/java/com/example/catguardian/
│   ├── controller/          # 控制器层（API接口）
│   ├── service/             # 服务层
│   ├── repository/          # 数据访问层
│   ├── entity/              # 实体类
│   ├── dto/                # 数据传输对象
│   │   ├── request/        # 请求DTO
│   │   └── response/       # 响应DTO
│   ├── enums/             # 枚举类
│   ├── exception/          # 异常处理
│   ├── security/          # 安全配置
│   ├── config/            # 配置类
│   └── utils/             # 工具类
├── src/main/resources/
│   └── application.yml     # 应用配置
└── pom.xml                # Maven配置

sql/
└── full_init.sql          # 数据库初始化脚本

根目录/
├── API接口文档.md          # API接口文档
└── README.md              # 项目说明文档
```

## 功能模块

### 核心模块

| 模块 | 说明 | 接口数 |
|------|------|--------|
| 认证模块 | 用户注册、登录、Token管理 | 5 |
| 用户模块 | 用户信息管理、实名认证 | 4 |
| 猫咪档案模块 | 猫咪档案CRUD、免疫驱虫提醒 | 5 |
| 领养模块 | 流浪猫发布、领养申请、跟踪回访、黑名单 | 17 |
| TNR模块 | 流浪猫绝育申请管理 | 6 |

### 服务模块

| 模块 | 说明 | 接口数 |
|------|------|--------|
| 订单模块 | 上门喂养服务订单、服务记录、评价 | 13 |
| 积分模块 | 积分余额、签到、AI使用次数 | 4 |
| 积分兑换模块 | 积分兑换商品 | 4 |

### 社区模块

| 模块 | 说明 | 接口数 |
|------|------|--------|
| 社区模块 | 帖子发布、评论、点赞、任务系统 | 11 |
| 关注模块 | 用户关注关系管理 | 5 |
| 私信模块 | 即时消息、对话管理 | 4 |

### 资源模块

| 模块 | 说明 | 接口数 |
|------|------|--------|
| 医院模块 | 宠物医院信息、优惠活动 | 3 |
| 商城模块 | 商品浏览 | 2 |

### AI模块

| 模块 | 说明 | 接口数 |
|------|------|--------|
| AI模块 | 养猫建议、头像生成、语录生成 | 4 |

### 文件上传模块

| 模块 | 说明 | 接口数 |
|------|------|--------|
| 文件上传模块 | 通用图片/视频上传、文件删除 | 4 |

**总计：96个API接口**

## 业务流程

### 用户注册登录
```
注册 → 登录 → 获取Token → 请求受保护接口
```

### 领养流程
```
发布流浪猫 → 平台审核 → 提交领养申请 → 平台初审 → 喂猫人复审 → 领养成功 → 跟踪回访
```

### 订单流程
```
创建订单 → 服务方接单 → 开始服务 → 服务记录 → 完成订单 → 评价
```

### TNR流程
```
创建TNR申请 → 选择医院 → 设置手术时间 → 平台审核 → 上传手术照片 → 完成
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8.0+
- Redis（可选，用于验证码缓存）

### 配置数据库

1. 创建数据库：
```sql
CREATE DATABASE weimao DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入数据库初始化脚本（推荐）：
```bash
# 使用MySQL命令导入完整脚本
mysql -u your_username -p weimao < sql/full_init.sql

# 或者登录MySQL后执行
mysql -u your_username -p
USE weimao;
SOURCE sql/full_init.sql;
```

3. 修改 `backend/src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/weimao?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

**数据库脚本说明**：
- `sql/full_init.sql` - 完整初始化脚本，包含所有表结构、索引、存储过程
- 脚本会自动创建数据库（如果不存在）并创建32张数据表
- 表结构与当前生产环境完全一致

### 运行项目

```bash
# 进入后端目录
cd backend

# 打包项目
mvn clean package -DskipTests

# 运行项目
mvn spring-boot:run
```

或者直接运行打包后的 JAR 文件：
```bash
java -jar target/cat-guardian.jar
```

### 访问服务

- 基础地址：`http://localhost:8080`
- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON：`http://localhost:8080/api-docs`

## API认证

除以下接口外，其他接口均需要认证：

- `/api/auth/**` - 认证相关
- `/api/adopt/cats` - 浏览可领养猫咪
- `/api/hospitals/**` - 医院信息
- `/api/mall/products/**` - 商品浏览
- `/api/exchange/items/**` - 兑换商品浏览
- `/api/upload/**` - 文件上传（图片/视频）
- `/uploads/**` - 上传文件访问
- `/swagger-ui/**` - API文档

认证方式：在请求头中添加 `Authorization: Bearer <token>`

## 数据库初始化

首次部署时，执行数据库初始化脚本：

```bash
mysql -u root -p cat_guardian < sql/full_init.sql
```

## 角色说明

| role值 | 说明 |
|--------|------|
| 0 | 普通用户 |
| 1 | 喂猫人 |
| 2 | 服务方 |
| 3 | 管理员 |

## 开发说明

### 代码规范

- 使用 Lombok 简化代码
- 统一使用 ApiResponse 包装响应
- 使用枚举类管理状态值
- Controller 层只做参数校验和响应包装

### 异常处理

- 业务异常使用 `BusinessException`
- 全局异常由 `GlobalExceptionHandler` 统一处理
- 返回格式：`{"code": xxx, "message": "xxx", "data": xxx}`

## 相关文档

- [API接口文档](./API接口文档.md) - 详细的接口说明和示例
- [技术方案](./backend-tech-plan.md) - 技术架构设计
- [需求文档](./喵伴守护小程序开发需求设计（文字版）.md) - 产品需求说明

## License

MIT License
