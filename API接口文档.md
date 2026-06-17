# 喵伴守护 - API 接口文档

> **基础地址**：`http://localhost:8080`  
> **Swagger UI**：`http://localhost:8080/swagger-ui/index.html`  
> **OpenAPI JSON**：`http://localhost:8080/api-docs`  
> **认证方式**：Bearer Token（JWT），在请求头添加 `Authorization: Bearer <token>`

---

## 📋 目录

| 模块 | 说明 | 接口数 |
|------|------|--------|
| [一、认证模块](#一认证模块-authcontroller) | 用户注册、登录、Token管理 | 5 |
| [二、用户模块](#二用户模块-usercontroller) | 用户信息管理、实名认证 | 4 |
| [三、猫咪档案模块](#三猫咪档案模块-catprofilecontroller) | 猫咪档案CRUD、提醒功能 | 5 |
| [四、领养模块](#四领养模块-adoptioncontroller) | 流浪猫发布、领养申请、跟踪回访 | 17 |
| [五、社区模块](#五社区模块-communitycontroller) | 帖子、评论、任务 | 11 |
| [六、关注模块](#六关注模块-followcontroller) | 用户关注关系管理 | 5 |
| [七、私信模块](#七私信模块-messagecontroller) | 即时消息、对话管理 | 4 |
| [八、医院模块](#八医院模块-hospitalcontroller) | 医院信息、优惠活动 | 3 |
| [九、商城模块](#九商城模块-mallcontroller) | 商品浏览 | 2 |
| [十、订单模块](#十订单模块-ordercontroller) | 服务订单、服务记录、评价 | 13 |
| [十一、积分模块](#十一积分模块-pointscontroller) | 积分余额、签到、记录 | 4 |
| [十二、积分兑换模块](#十二积分兑换模块-exchangecontroller) | 积分兑换商品 | 4 |
| [十三、服务方资质模块](#十三服务方资质模块-serviceprovidercontroller) | 服务方认证审核 | 5 |
| [十四、TNR模块](#十四tnr模块-tnrcontroller) | 流浪猫绝育申请 | 6 |
| [十五、AI模块](#十五ai模块-aicontroller) | AI咨询、头像生成 | 4 |
| [十六、文件上传模块](#十六文件上传模块-uploadcontroller) | 通用图片/视频上传 | 4 |

---

## 🔄 业务流程概览

### 用户注册登录流程
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

### 积分获取途径
```
每日签到(3分) → 社区任务 → AI咨询消耗 → 积分兑换
```

---

## 📝 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1781590862416
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200=成功，400=业务错误，403=无权限，404=未找到 |
| message | String | 响应消息 |
| data | Object | 响应数据 |
| timestamp | Long | 时间戳 |

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数校验失败 / 业务错误 |
| 401 | 未认证（Token无效或过期） |
| 403 | 无权限（角色不匹配） |
| 404 | 资源未找到 |
| 500 | 服务器内部错误 |

---

## 📊 模块关联关系

```
认证模块 ──→ 用户模块 ──→ 猫咪档案模块
               │               │
               ↓               ↓
           领养模块 ←────── 订单模块
               │               │
               ↓               ↓
           TNR模块          积分模块 ──→ 积分兑换模块
                               ↑
                               │
                           社区模块 ──→ 关注模块 ──→ 私信模块
                               │
                               ↓
                           服务方资质模块

医院模块 ←─────────────────────────────────────┐
                                               │
商城模块 ──────────────────────────────────────┘
```

---

## 一、认证模块 (AuthController)

### 模块说明
负责用户身份认证，包括注册、登录、Token刷新和验证码验证。所有接口均无需认证即可访问。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 1.1 | `/api/auth/register` | POST | 否 | 用户注册 |
| 1.2 | `/api/auth/login` | POST | 否 | 用户登录 |
| 1.3 | `/api/auth/refresh-token` | POST | 否 | 刷新Token |
| 1.4 | `/api/auth/verify-phone` | POST | 否 | 发送验证码 |
| 1.5 | `/api/auth/verify-code` | POST | 否 | 验证验证码 |

### 1.1 用户注册

- **URL**：`POST /api/auth/register`

**请求体**：
```json
{
  "phone": "13800138001",
  "password": "123456",
  "name": "张三",
  "idCard": "110101199001011234"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号，1开头11位 |
| password | String | 是 | 密码 |
| name | String | 是 | 姓名 |
| idCard | String | 否 | 身份证号 |

**响应数据**：
```json
{
  "userId": 1,
  "token": "eyJhbGci...",
  "expireTime": 1781598062416,
  "refreshToken": "eyJhbGci...",
  "refreshExpireTime": 1781677262416
}
```

### 1.2 用户登录

- **URL**：`POST /api/auth/login`

**请求体**：
```json
{
  "phone": "13800138001",
  "password": "123456"
}
```

**响应数据**：同注册响应

### 1.3 刷新Token

- **URL**：`POST /api/auth/refresh-token`

**请求体**：
```json
{
  "refreshToken": "eyJhbGci..."
}
```

**响应数据**：同登录响应

### 1.4 发送验证码

- **URL**：`POST /api/auth/verify-phone`

**请求体**：
```json
{
  "phone": "13800138001"
}
```

> 当前为Mock模式，验证码会返回给前端用于测试

### 1.5 验证验证码

- **URL**：`POST /api/auth/verify-code`

**请求体**：
```json
{
  "phone": "13800138001",
  "code": "123456"
}
```

**响应数据**：`true` / `false`

---

## 二、用户模块 (UserController)

### 模块说明
管理用户基本信息和实名认证。用户注册后可完善个人资料、提交实名认证申请。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 2.1 | `/api/users/profile` | GET | 是 | 获取个人资料 |
| 2.2 | `/api/users/profile` | PUT | 是 | 更新个人资料 |
| 2.3 | `/api/users/auth/realname` | POST | 是 | 提交实名认证 |
| 2.4 | `/api/users/auth/realname` | GET | 是 | 获取实名认证状态 |

### 2.1 获取个人资料

- **URL**：`GET /api/users/profile`

**响应数据**：
```json
{
  "id": 1,
  "phone": "13800138001",
  "name": "张三",
  "avatar": "https://example.com/avatar.jpg",
  "address": "北京市",
  "role": 0,
  "roleDescription": "普通用户",
  "creditScore": 100,
  "pointsBalance": 0
}
```

| role值 | 说明 |
|--------|------|
| 0 | 普通用户 |
| 1 | 喂猫人 |
| 2 | 服务方 |
| 3 | 管理员 |

### 2.2 更新个人资料

- **URL**：`PUT /api/users/profile`

**请求体**：
```json
{
  "name": "新名字",
  "avatar": "https://example.com/new-avatar.jpg",
  "address": "北京市朝阳区"
}
```

### 2.3 提交实名认证

- **URL**：`POST /api/users/auth/realname`

**请求体**：
```json
{
  "realName": "张三",
  "idCard": "110101199001011234",
  "faceImage": "https://example.com/face.jpg"
}
```

**响应数据**：
```json
{
  "userId": 1,
  "realName": "张三",
  "idCard": "110***********1234",
  "status": 0,
  "statusDescription": "待审核",
  "rejectReason": null,
  "authenticatedAt": null
}
```

| status值 | 说明 |
|----------|------|
| 0 | 待审核 |
| 1 | 已通过 |
| 2 | 已拒绝 |

### 2.4 获取实名认证状态

- **URL**：`GET /api/users/auth/realname`

**响应数据**：同 2.3

---

## 三、猫咪档案模块 (CatProfileController)

### 模块说明
管理用户的猫咪档案，支持创建、编辑、删除猫咪信息。包含免疫和驱虫提醒功能（到期前7天自动触发）。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 3.1 | `/api/users/cats` | POST | 是 | 创建猫咪档案 |
| 3.2 | `/api/users/cats` | GET | 是 | 获取我的猫咪列表 |
| 3.3 | `/api/users/cats/{id}` | GET | 否 | 获取猫咪档案详情 |
| 3.4 | `/api/users/cats/{id}` | PUT | 是 | 更新猫咪档案 |
| 3.5 | `/api/users/cats/{id}` | DELETE | 是 | 删除猫咪档案 |

### 3.1 创建猫咪档案

- **URL**：`POST /api/users/cats`

**请求体**：
```json
{
  "name": "小橘",
  "breed": "橘猫",
  "age": "2岁",
  "gender": 1,
  "healthStatus": "健康",
  "dietaryHabits": "爱吃鱼",
  "taboos": "不能吃巧克力",
  "sterilized": 1,
  "vaccinated": 1,
  "nextVaccineDate": "2026-12-01",
  "lastDewormDate": "2026-06-01",
  "nextDewormDate": "2026-09-01",
  "insuranceInfo": "已购买宠物保险",
  "medicalRecords": "2026年5月体检正常",
  "avatar": "https://example.com/cat.jpg"
}
```

**响应数据**：
```json
{
  "id": 1,
  "userId": 4,
  "name": "小橘",
  "breed": "橘猫",
  "age": "2岁",
  "gender": 1,
  "healthStatus": "健康",
  "dietaryHabits": "爱吃鱼",
  "taboos": "不能吃巧克力",
  "sterilized": 1,
  "vaccinated": 1,
  "nextVaccineDate": "2026-12-01",
  "lastDewormDate": "2026-06-01",
  "nextDewormDate": "2026-09-01",
  "insuranceInfo": "已购买宠物保险",
  "medicalRecords": "2026年5月体检正常",
  "avatar": "https://example.com/cat.jpg",
  "createdAt": "2026-06-16T14:30:11",
  "updatedAt": "2026-06-16T14:30:11",
  "vaccineReminder": false,
  "dewormReminder": true
}
```

> ⚠️ **提醒机制**：`vaccineReminder` 和 `dewormReminder` 在日期到期前7天自动变为 `true`

### 3.2 获取我的猫咪列表

- **URL**：`GET /api/users/cats`

**响应数据**：CatProfileResponse 数组

### 3.3 获取猫咪档案详情

- **URL**：`GET /api/users/cats/{id}`
- **认证**：不需要

### 3.4 更新猫咪档案

- **URL**：`PUT /api/users/cats/{id}`
- **请求体**：同 3.1

### 3.5 删除猫咪档案

- **URL**：`DELETE /api/users/cats/{id}`

---

## 四、领养模块 (AdoptionController)

### 模块说明
完整的领养业务流程，包括流浪猫发布审核、领养申请、跟踪回访和黑名单管理。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 4.1 | `/api/adopt/cats` | POST | 是 | 发布流浪猫信息 |
| 4.2 | `/api/adopt/cats` | GET | 否 | 获取可领养猫咪列表 |
| 4.3 | `/api/adopt/cats/{id}` | GET | 否 | 获取流浪猫详情 |
| 4.4 | `/api/adopt/cats/feeder` | GET | 是 | 获取我发布的流浪猫 |
| 4.5 | `/api/adopt/cats/{id}/review` | PUT | 是 | 审核流浪猫信息 |
| 4.6 | `/api/adopt/cats/filter` | POST | 否 | 筛选流浪猫 |
| 4.7 | `/api/adopt/applications` | POST | 是 | 提交领养申请 |
| 4.8 | `/api/adopt/applications` | GET | 是 | 获取我的领养申请 |
| 4.9 | `/api/adopt/applications/{id}` | GET | 是 | 获取领养申请详情 |
| 4.10 | `/api/adopt/cats/{catId}/applications` | GET | 是 | 获取某只猫的领养申请 |
| 4.11 | `/api/adopt/applications/{id}/review` | PUT | 是 | 初审领养申请 |
| 4.12 | `/api/adopt/applications/{id}/feeder-review` | PUT | 是 | 复审领养申请 |
| 4.13 | `/api/adopt/applications/{id}/tracking` | POST | 是 | 添加跟踪记录 |
| 4.14 | `/api/adopt/applications/{id}/tracking` | GET | 是 | 获取跟踪记录 |
| 4.15 | `/api/adopt/blacklist` | GET | 是 | 查询黑名单 |
| 4.16 | `/api/adopt/blacklist` | POST | 是 | 添加黑名单 |
| 4.17 | `/api/adopt/blacklist/{userId}` | DELETE | 是 | 移除黑名单 |

### 领养状态流转
```
待审核(0) → 待领养(1) → 已领养(2)
         ↘ 审核未通过(3)
```

### 4.1 发布流浪猫信息

- **URL**：`POST /api/adopt/cats`

**请求体**：
```json
{
  "name": "流浪小花",
  "breed": "三花猫",
  "age": "1岁",
  "gender": 0,
  "healthStatus": "健康",
  "sterilized": 0,
  "vaccinated": 0,
  "location": "北京市朝阳区",
  "description": "很亲人的小猫",
  "photos": ["https://example.com/cat1.jpg"]
}
```

### 4.2 获取可领养猫咪列表

- **URL**：`GET /api/adopt/cats`
- **认证**：不需要

> 返回审核通过（status=1）的流浪猫列表

### 4.3 获取流浪猫详情

- **URL**：`GET /api/adopt/cats/{id}`
- **认证**：不需要

### 4.4 获取我发布的流浪猫

- **URL**：`GET /api/adopt/cats/feeder`

### 4.5 审核流浪猫信息

- **URL**：`PUT /api/adopt/cats/{id}/review`

**请求体**：
```json
{
  "status": 1
}
```

| status值 | 说明 |
|----------|------|
| 0 | 待审核 |
| 1 | 待领养 |
| 2 | 已领养 |
| 3 | 审核未通过 |

### 4.6 筛选流浪猫

- **URL**：`POST /api/adopt/cats/filter`
- **认证**：不需要

**请求体**：
```json
{
  "breed": "橘猫",
  "age": "1岁",
  "gender": 1,
  "sterilized": 1,
  "vaccinated": 1,
  "location": "北京"
}
```

### 4.7 提交领养申请

- **URL**：`POST /api/adopt/applications`

**请求体**：
```json
{
  "catId": 1,
  "livingAddress": "北京市海淀区",
  "housingType": 1,
  "familyAgree": 1,
  "petExperience": "养过猫",
  "hasAbandoned": 0
}
```

### 4.8 获取我的领养申请

- **URL**：`GET /api/adopt/applications`

### 4.9 获取领养申请详情

- **URL**：`GET /api/adopt/applications/{id}`

### 4.10 获取某只猫的领养申请列表

- **URL**：`GET /api/adopt/cats/{catId}/applications`

### 4.11 初审领养申请（平台审核）

- **URL**：`PUT /api/adopt/applications/{id}/review`

**请求体**：
```json
{
  "note": "初审通过"
}
```

### 4.12 复审领养申请（喂猫人审核）

- **URL**：`PUT /api/adopt/applications/{id}/feeder-review`

**请求体**：
```json
{
  "status": 1,
  "note": "喂猫人审核通过"
}
```

| status值 | 说明 |
|----------|------|
| 0 | 待初审 |
| 1 | 待复审 |
| 2 | 已通过 |
| 3 | 已拒绝 |

### 4.13 添加跟踪记录

- **URL**：`POST /api/adopt/applications/{id}/tracking`

**请求体**：
```json
{
  "trackingDate": "2026-06-20",
  "content": "猫咪适应良好，食欲正常",
  "photos": "https://example.com/cat1.jpg"
}
```

### 4.14 获取跟踪记录

- **URL**：`GET /api/adopt/applications/{id}/tracking`

### 4.15 查询黑名单

- **URL**：`GET /api/adopt/blacklist`

### 4.16 添加黑名单

- **URL**：`POST /api/adopt/blacklist`

**请求体**：
```json
{
  "userId": 5,
  "reason": "多次弃养记录"
}
```

### 4.17 移除黑名单

- **URL**：`DELETE /api/adopt/blacklist/{userId}`

---

## 五、社区模块 (CommunityController)

### 模块说明
社区互动功能，包括帖子发布、评论、点赞和任务系统。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 5.1 | `/api/community/posts` | GET | 是 | 获取帖子列表 |
| 5.2 | `/api/community/posts/{id}` | GET | 是 | 获取帖子详情 |
| 5.3 | `/api/community/posts` | POST | 是 | 发布帖子 |
| 5.4 | `/api/community/posts/{id}` | PUT | 是 | 更新帖子 |
| 5.5 | `/api/community/posts/{id}` | DELETE | 是 | 删除帖子 |
| 5.6 | `/api/community/posts/{id}/comments` | GET | 否 | 获取帖子评论 |
| 5.7 | `/api/community/posts/{id}/comments` | POST | 是 | 发表评论 |
| 5.8 | `/api/community/posts/{id}/like` | POST | 是 | 点赞帖子 |
| 5.9 | `/api/community/tasks` | GET | 否 | 获取任务列表 |
| 5.10 | `/api/community/tasks` | POST | 是 | 发布任务 |
| 5.11 | `/api/community/tasks/{id}/claim` | POST | 是 | 认领任务 |

### 5.1 获取帖子列表

- **URL**：`GET /api/community/posts`

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | Integer | 否 | 帖子类型筛选 |

**响应数据**：
```json
[
  {
    "id": 1,
    "userId": 4,
    "userName": "测试用户",
    "userAvatar": "https://example.com/avatar.jpg",
    "title": "test post",
    "content": "hello world",
    "images": null,
    "type": 0,
    "viewCount": 1,
    "likeCount": 0,
    "commentCount": 0,
    "status": 1,
    "isLiked": false,
    "createdAt": "2026-06-16T14:32:34"
  }
]
```

### 5.2 获取帖子详情

- **URL**：`GET /api/community/posts/{id}`

### 5.3 发布帖子

- **URL**：`POST /api/community/posts`

**请求体**：
```json
{
  "title": "帖子标题",
  "content": "帖子内容",
  "images": "图片URL，多个用逗号分隔",
  "type": 0
}
```

### 5.4 更新帖子

- **URL**：`PUT /api/community/posts/{id}`
- **请求体**：同 5.3

### 5.5 删除帖子

- **URL**：`DELETE /api/community/posts/{id}`

### 5.6 获取帖子评论

- **URL**：`GET /api/community/posts/{id}/comments`
- **认证**：不需要

### 5.7 发表评论

- **URL**：`POST /api/community/posts/{id}/comments`

**请求体**：
```json
{
  "content": "评论内容",
  "parentId": null
}
```

### 5.8 点赞帖子

- **URL**：`POST /api/community/posts/{id}/like`

### 5.9 获取任务列表

- **URL**：`GET /api/community/tasks`
- **认证**：不需要

### 5.10 发布任务

- **URL**：`POST /api/community/tasks`

**请求体**：
```json
{
  "title": "任务标题",
  "content": "任务内容",
  "type": 0,
  "location": "北京市",
  "rewardPoints": 10
}
```

### 5.11 认领任务

- **URL**：`POST /api/community/tasks/{id}/claim`

---

## 六、关注模块 (FollowController)

### 模块说明
用户间的关注关系管理，支持关注、取消关注和查看关注列表。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 6.1 | `/api/follow/{userId}` | POST | 是 | 关注用户 |
| 6.2 | `/api/follow/{userId}` | DELETE | 是 | 取消关注 |
| 6.3 | `/api/follow/{userId}/status` | GET | 是 | 检查关注状态 |
| 6.4 | `/api/follow/following` | GET | 是 | 获取关注列表 |
| 6.5 | `/api/follow/followers` | GET | 是 | 获取粉丝列表 |

### 6.1 关注用户

- **URL**：`POST /api/follow/{userId}`

### 6.2 取消关注

- **URL**：`DELETE /api/follow/{userId}`

### 6.3 检查关注状态

- **URL**：`GET /api/follow/{userId}/status`
- **响应数据**：`true` / `false`

### 6.4 获取关注列表

- **URL**：`GET /api/follow/following`

### 6.5 获取粉丝列表

- **URL**：`GET /api/follow/followers`

---

## 七、私信模块 (MessageController)

### 模块说明
用户间的即时通信功能，支持发送消息、查看对话和标记已读。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 7.1 | `/api/messages/send` | POST | 是 | 发送私信 |
| 7.2 | `/api/messages/conversation/{otherUserId}` | GET | 是 | 获取对话记录 |
| 7.3 | `/api/messages/contacts` | GET | 是 | 获取最近联系人 |
| 7.4 | `/api/messages/read/{senderId}` | POST | 是 | 标记消息已读 |

### 7.1 发送私信

- **URL**：`POST /api/messages/send`

**请求体**：
```json
{
  "receiverId": 5,
  "content": "你好，想咨询一下领养流程"
}
```

**响应数据**：
```json
{
  "id": 1,
  "senderId": 4,
  "receiverId": 5,
  "content": "你好，想咨询一下领养流程",
  "type": "text",
  "readStatus": 0,
  "createdAt": "2026-06-16T15:00:00"
}
```

### 7.2 获取对话记录

- **URL**：`GET /api/messages/conversation/{otherUserId}`

> 调用此接口会自动将对方发来的消息标记为已读

### 7.3 获取最近联系人

- **URL**：`GET /api/messages/contacts`

### 7.4 标记消息已读

- **URL**：`POST /api/messages/read/{senderId}`

---

## 八、医院模块 (HospitalController)

### 模块说明
宠物医院信息展示，包括医院列表、详情和优惠活动。所有接口均无需认证。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 8.1 | `/api/hospitals` | GET | 否 | 获取医院列表 |
| 8.2 | `/api/hospitals/{id}` | GET | 否 | 获取医院详情 |
| 8.3 | `/api/hospitals/{id}/discounts` | GET | 否 | 获取医院优惠 |

### 8.1 获取医院列表

- **URL**：`GET /api/hospitals`

### 8.2 获取医院详情

- **URL**：`GET /api/hospitals/{id}`

### 8.3 获取医院优惠

- **URL**：`GET /api/hospitals/{id}/discounts`

---

## 九、商城模块 (MallController)

### 模块说明
商城商品浏览功能。所有接口均无需认证。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 9.1 | `/api/mall/products` | GET | 否 | 获取商品列表 |
| 9.2 | `/api/mall/products/{id}` | GET | 否 | 获取商品详情 |

### 9.1 获取商品列表

- **URL**：`GET /api/mall/products`

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | Integer | 否 | 商品分类筛选 |

### 9.2 获取商品详情

- **URL**：`GET /api/mall/products/{id}`

---

## 十、订单模块 (OrderController)

### 模块说明
上门喂养服务订单管理，包含订单生命周期和服务记录。

### 订单响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单ID |
| orderNo | String | 订单编号 |
| clientId | Long | 委托方ID |
| clientName | String | 委托方姓名 |
| serviceId | Long | 服务方ID |
| serviceName | String | 服务方姓名 |
| status | Integer | 订单状态 |
| statusDescription | String | 状态描述 |
| startTime | String | 服务开始时间 |
| endTime | String | 服务结束时间 |
| visitFrequency | Integer | 上门频次 |
| feedingRequirements | String | 喂养要求 |
| litterCleanStandard | String | 猫砂清理标准 |
| specialCare | String | 特殊照料需求 |
| entryMethod | Integer | 入户方式 |
| keyStorageInfo | String | 钥匙寄存信息 |
| emergencyContact | String | 紧急联系人 |
| address | String | 服务地址 |
| totalAmount | BigDecimal | 订单总额 |
| actualPayment | BigDecimal | 实付金额 |
| refundAmount | BigDecimal | 退款金额 |
| commissionRate | BigDecimal | 佣金比例 |
| createdAt | String | 创建时间 |
| updatedAt | String | 更新时间 |

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 10.1 | `/api/orders` | POST | 是 | 创建订单 |
| 10.2 | `/api/orders/{id}` | GET | 是 | 获取订单详情（按ID） |
| 10.3 | `/api/orders/order-no/{orderNo}` | GET | 是 | 获取订单详情（按订单号） |
| 10.4 | `/api/orders/client` | GET | 是 | 获取客户订单列表 |
| 10.5 | `/api/orders/provider` | GET | 是 | 获取服务方订单列表 |
| 10.6 | `/api/orders/pending` | GET | 是 | 获取待接单列表 |
| 10.7 | `/api/orders/{id}/accept` | POST | 是 | 接单 |
| 10.8 | `/api/orders/{id}/start` | POST | 是 | 开始服务 |
| 10.9 | `/api/orders/{id}` | DELETE | 是 | 取消订单 |
| 10.10 | `/api/orders/{id}/complete` | POST | 是 | 完成订单 |
| 10.11 | `/api/orders/{id}/records` | GET | 是 | 获取服务记录列表 |
| 10.12 | `/api/orders/{id}/records` | POST | 是 | 创建服务记录 |
| 10.13 | `/api/orders/{id}/evaluate` | POST | 是 | 评价订单 |

### 订单状态流转

```
待接单(0) → 已接单(1) → 服务中(2) → 已完成(3)
           ↘ 已取消(4) ↗
```

### 退款规则

| 取消时机 | 退款比例 |
|----------|----------|
| 待接单取消 | 全额退款 |
| 已接单取消 | 扣除5% |
| 服务当天取消 | 扣除10% |

### 10.1 创建订单

- **URL**：`POST /api/orders`

**请求体**：
```json
{
  "startTime": "2026-06-17T09:00:00",
  "endTime": "2026-06-17T18:00:00",
  "visitFrequency": 2,
  "feedingRequirements": "每天喂2次",
  "litterCleanStandard": "每天清理",
  "specialCare": "需要喂药",
  "entryMethod": 1,
  "keyStorageInfo": "门口密码锁",
  "emergencyContact": "13800138000",
  "address": "北京市海淀区",
  "totalAmount": 100.00,
  "catIds": [1, 2]
}
```

### 10.2 获取订单详情（按ID）

- **URL**：`GET /api/orders/{id}`

### 10.3 获取订单详情（按订单号）

- **URL**：`GET /api/orders/order-no/{orderNo}`

### 10.4 获取客户订单列表

- **URL**：`GET /api/orders/client`

### 10.5 获取服务方订单列表

- **URL**：`GET /api/orders/provider`

### 10.6 获取待接单列表

- **URL**：`GET /api/orders/pending`

### 10.7 接单

- **URL**：`POST /api/orders/{id}/accept`

### 10.8 开始服务

- **URL**：`POST /api/orders/{id}/start`

### 10.9 取消订单

- **URL**：`DELETE /api/orders/{id}`

### 10.10 完成订单

- **URL**：`POST /api/orders/{id}/complete`

### 10.11 获取服务记录列表

- **URL**：`GET /api/orders/{id}/records`

### 10.12 创建服务记录

- **URL**：`POST /api/orders/{id}/records`

**请求体**：
```json
{
  "serviceTime": "2026-06-17T10:00:00",
  "videoUrl": "https://example.com/video.mp4",
  "lockPhotoUrl": "https://example.com/lock.jpg",
  "notes": "服务完成"
}
```

### 10.13 评价订单

- **URL**：`POST /api/orders/{id}/evaluate`

**请求体**：
```json
{
  "rating": 5,
  "comment": "服务很好！"
}
```

---

## 十一、积分模块 (PointsController)

### 模块说明
积分余额管理和签到功能。每日签到获得3积分。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 11.1 | `/api/points/balance` | GET | 是 | 获取积分余额 |
| 11.2 | `/api/points/records` | GET | 是 | 获取积分记录 |
| 11.3 | `/api/points/checkin` | POST | 是 | 签到 |
| 11.4 | `/api/points/ai-usage` | GET | 是 | 获取今日AI使用次数 |

### 11.1 获取积分余额

- **URL**：`GET /api/points/balance`

**响应数据**：
```json
{
  "userId": 4,
  "balance": 3,
  "totalEarned": 3,
  "totalSpent": 0
}
```

### 11.2 获取积分记录

- **URL**：`GET /api/points/records`

### 11.3 签到

- **URL**：`POST /api/points/checkin`

> 每日签到获得3积分，每天只能签到一次

### 11.4 获取今日AI使用次数

- **URL**：`GET /api/points/ai-usage`

---

## 十二、积分兑换模块 (ExchangeController)

### 模块说明
积分兑换商品功能，包括商品浏览和兑换操作。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 12.1 | `/api/exchange/items` | GET | 否 | 获取兑换商品列表 |
| 12.2 | `/api/exchange/items/{id}` | GET | 否 | 获取商品详情 |
| 12.3 | `/api/exchange/exchange` | POST | 是 | 积分兑换 |
| 12.4 | `/api/exchange/records` | GET | 是 | 获取兑换记录 |

### 12.1 获取兑换商品列表

- **URL**：`GET /api/exchange/items`

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 否 | 商品类型筛选（ai_service, coupon, gift） |

### 12.2 获取商品详情

- **URL**：`GET /api/exchange/items/{id}`

### 12.3 积分兑换

- **URL**：`POST /api/exchange/exchange`

**请求体**：
```json
{
  "itemId": 1
}
```

### 12.4 获取兑换记录

- **URL**：`GET /api/exchange/records`

---

## 十三、服务方资质模块 (ServiceProviderController)

### 模块说明
服务方认证审核流程，需先完成实名认证才能申请。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 13.1 | `/api/service-provider/apply` | POST | 是 | 申请服务方资质 |
| 13.2 | `/api/service-provider/credentials` | GET | 是 | 获取我的资质信息 |
| 13.3 | `/api/service-provider/applications` | GET | 是 | 获取待审核申请列表 |
| 13.4 | `/api/service-provider/applications/{id}/approve` | POST | 是 | 审核通过 |
| 13.5 | `/api/service-provider/applications/{id}/reject` | POST | 是 | 审核拒绝 |

### 13.1 申请服务方资质

- **URL**：`POST /api/service-provider/apply`

**请求体**：
```json
{
  "idCardFront": "https://example.com/front.jpg",
  "idCardBack": "https://example.com/back.jpg",
  "criminalRecord": "https://example.com/criminal.pdf",
  "trainingCertificate": "https://example.com/cert.jpg",
  "hasSignedAgreement": 1
}
```

### 13.2 获取我的资质信息

- **URL**：`GET /api/service-provider/credentials`

### 13.3 获取待审核申请列表

- **URL**：`GET /api/service-provider/applications`

### 13.4 审核通过

- **URL**：`POST /api/service-provider/applications/{id}/approve`

### 13.5 审核拒绝

- **URL**：`POST /api/service-provider/applications/{id}/reject`

**请求体**：
```json
{
  "reason": "拒绝原因"
}
```

---

## 十四、TNR模块 (TnrController)

### 模块说明
流浪猫绝育（TNR）申请管理，支持医院选择和手术照片上传。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 14.1 | `/api/tnr/applications` | POST | 是 | 创建TNR申请 |
| 14.2 | `/api/tnr/applications` | GET | 是 | 获取TNR申请列表 |
| 14.3 | `/api/tnr/applications/{id}` | GET | 是 | 获取TNR申请详情 |
| 14.4 | `/api/tnr/applications/{id}/approve` | POST | 是 | 审核通过 |
| 14.5 | `/api/tnr/applications/{id}/reject` | POST | 是 | 审核拒绝 |
| 14.6 | `/api/tnr/applications/{id}/photos` | POST | 是 | 上传手术照片 |

### 14.1 创建TNR申请

- **URL**：`POST /api/tnr/applications`

**请求体**：
```json
{
  "catName": "小花",
  "location": "北京市朝阳区望京",
  "description": "小区内发现一只流浪猫，需要TNR",
  "photos": "https://example.com/cat1.jpg",
  "hospitalId": 1,
  "operationTime": "2026-06-20T10:00:00"
}
```

### 14.2 获取TNR申请列表

- **URL**：`GET /api/tnr/applications`

### 14.3 获取TNR申请详情

- **URL**：`GET /api/tnr/applications/{id}`

### 14.4 审核通过

- **URL**：`POST /api/tnr/applications/{id}/approve`

### 14.5 审核拒绝

- **URL**：`POST /api/tnr/applications/{id}/reject`

**请求体**：
```json
{
  "reason": "信息不完整"
}
```

### 14.6 上传手术照片

- **URL**：`POST /api/tnr/applications/{id}/photos`

**请求体**：
```json
{
  "photos": "https://example.com/operation1.jpg,https://example.com/operation2.jpg"
}
```

> 上传手术照片后，申请状态自动变为 3（已完成手术）

---

## 十五、AI模块 (AiController)

### 模块说明
AI辅助功能，包括猫咪咨询、头像生成和语录生成。当前为Mock模式，API Key留空。

### 接口列表

| 序号 | 接口 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| 15.1 | `/api/ai/advice` | POST | 是 | 获取AI养猫建议 |
| 15.2 | `/api/ai/avatar` | POST | 是 | 生成AI猫咪头像 |
| 15.3 | `/api/ai/quote` | POST | 是 | 生成AI猫咪语录 |
| 15.4 | `/api/ai/usage` | GET | 是 | 获取今日AI使用次数 |

### 15.1 获取AI养猫建议

- **URL**：`POST /api/ai/advice`

**请求体**：
```json
{
  "question": "猫咪不吃东西怎么办？",
  "catBreed": "橘猫",
  "catAge": "2岁"
}
```

**响应数据**：
```json
{
  "topic": "environment",
  "advice": "建议保持环境安静，尝试更换食物..."
}
```

### 15.2 生成AI猫咪头像

- **URL**：`POST /api/ai/avatar`

**请求体**：
```json
{
  "prompt": "一只可爱的橘猫头像，卡通风格",
  "catBreed": "橘猫"
}
```

### 15.3 生成AI猫咪语录

- **URL**：`POST /api/ai/quote`

**响应数据**：
```json
{
  "quote": "猫咪是人类最好的朋友，它们用咕噜声治愈我们的心灵"
}
```

### 15.4 获取今日AI使用次数

- **URL**：`GET /api/ai/usage`

**响应数据**：今日使用次数（Integer）

---

## 十六、文件上传模块 (UploadController)

### 模块说明
通用文件上传功能，支持图片和视频上传，用于支撑发帖、猫咪建档等业务场景。

### 接口列表

| 序号 | 接口路径 | 请求方法 | 是否需要认证 | 接口说明 |
|------|----------|----------|-------------|----------|
| 16.1 | `/api/upload/file` | POST | 否 | 通用文件上传（自动识别类型） |
| 16.2 | `/api/upload/image` | POST | 否 | 图片上传 |
| 16.3 | `/api/upload/video` | POST | 否 | 视频上传 |
| 16.4 | `/api/upload/file` | DELETE | 是 | 删除文件 |

### 16.1 通用文件上传

- **URL**：`POST /api/upload/file`

**请求参数**：
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | MultipartFile | 是 | 要上传的文件 |

**支持的文件类型**：
- 图片：jpg, jpeg, png, gif, webp, bmp
- 视频：mp4, avi, mov, flv, wmv

**文件大小限制**：
- 图片：10MB
- 视频：50MB

**响应数据**：
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "/uploads/images/2024/01/15/abc123.jpg"
}
```

### 16.2 图片上传

- **URL**：`POST /api/upload/image`

**请求参数**：
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | MultipartFile | 是 | 要上传的图片文件 |

**响应数据**：图片URL字符串

### 16.3 视频上传

- **URL**：`POST /api/upload/video`

**请求参数**：
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | MultipartFile | 是 | 要上传的视频文件 |

**响应数据**：视频URL字符串

### 16.4 删除文件

- **URL**：`DELETE /api/upload/file?path=xxx`

**请求参数**：
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| path | String | 是 | 文件路径（上传时返回的URL） |

---

## ✅ 测试结果汇总

| 模块 | 接口数 | 测试通过 | 状态 |
|------|--------|---------|------|
| 认证模块 | 5 | 5 | ✅ |
| 用户模块 | 4 | 4 | ✅ |
| 猫咪档案模块 | 5 | 5 | ✅ |
| 领养模块 | 17 | 17 | ✅ |
| 社区模块 | 11 | 11 | ✅ |
| 关注模块 | 5 | 5 | ✅ |
| 私信模块 | 4 | 4 | ✅ |
| 医院模块 | 3 | 3 | ✅ |
| 商城模块 | 2 | 2 | ✅ |
| 订单模块 | 13 | 13 | ✅ |
| 积分模块 | 4 | 4 | ✅ |
| 积分兑换模块 | 4 | 4 | ✅ |
| 服务方资质模块 | 5 | 5 | ✅ |
| TNR模块 | 6 | 6 | ✅ |
| AI模块 | 4 | 4 | ✅ |
| 文件上传模块 | 4 | 4 | ✅ |

**总计**：96个接口，全部测试通过 ✅

### 📝 测试说明

#### 文件上传模块测试结果
- ✅ 图片上传：返回路径 `/uploads/images/2026/06/17/166fc9b7d4da4f72bbd4a18b331c7aa2.jpg`
- ✅ 通用文件上传：自动识别文件类型，返回路径 `/uploads/images/2026/06/17/4193597df4814cd0b2575e3fa18b5e91.jpg`
- ✅ 文件删除：成功删除指定路径的文件

#### 订单实付和退款金额测试结果
- ✅ 创建订单：`actualPayment=0`, `refundAmount=0`
- ✅ 取消订单（待接单状态）：`actualPayment=100.00`, `refundAmount=100.00`（全额退款）