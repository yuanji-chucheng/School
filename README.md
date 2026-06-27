# 校园二手物品交易与互助服务平台

基于 **Java 17 + Spring Boot 3.3 + MyBatis + MySQL 8.0** 的前后端分离项目，前端使用原生 HTML/CSS/JavaScript，通过 Ajax 调用 RESTful API，认证采用 JWT Access Token。

## 功能模块

| 模块 | 说明 |
|------|------|
| 用户注册登录 | 学号注册，管理员审核后可登录；角色：学生/管理员 |
| 二手物品 | 发布（分类、新旧、图片）、管理员审核、搜索浏览 |
| 交易订单 | 下单→待付款→待发货→待收货→已完成；可取消待付款订单 |
| 交易评价 | 完成后双方互评，影响信用分 |
| 互助服务 | 发布需求→接单→进行中→完成；完成后互评 |
| 站内私信 | 买卖双方私信沟通 |
| 系统通知 | 审核结果、订单变化等通知 |
| 举报 | 用户举报违规内容，管理员处理 |
| 学生管理 | 管理员可单个/批量删除学生，级联删除其发布的物品和互助帖 |
| API 文档 | Swagger UI 在线查看所有接口 |

## 项目结构

```
demo1/
├── sql/
│   ├── schema.sql              # 数据库建表脚本
│   └── cleanup_database.sql    # 数据库清理脚本
├── frontend/                   # 前端页面（独立部署或用 Live Server 打开）
│   ├── css/common.css          # 公共样式
│   ├── js/api.js               # 所有 API 封装
│   ├── index.html              # 首页
│   ├── login.html              # 登录页
│   ├── register.html           # 注册页
│   ├── items.html              # 物品列表
│   ├── item-detail.html        # 物品详情
│   ├── item-publish.html       # 发布物品
│   ├── orders.html             # 我的订单
│   ├── help.html               # 互助列表
│   ├── help-detail.html        # 互助详情
│   ├── help-publish.html       # 发布互助
│   ├── messages.html           # 私信
│   ├── profile.html            # 个人中心
│   ├── admin.html              # 管理员入口
│   └── admin-panel.html        # 管理后台
├── uploads/                    # 本地上传文件存储目录
└── src/main/java/com/example/demo/
    ├── controller/             # REST 接口层
    ├── service/                # 业务逻辑层
    ├── mapper/                 # MyBatis 接口
    ├── entity/                 # 实体类
    ├── dto/                    # 数据传输对象
    ├── common/                 # 统一响应、异常处理
    ├── config/                 # 跨域、JWT 拦截器、Swagger 配置
    └── util/                   # JWT、密码、OSS 工具
```

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 快速启动

### 1. 初始化数据库

```bash
mysql -u root -p < sql/schema.sql
```

默认创建数据库 `campus_trade`，并初始化管理员账号：

- **学号**：`admin`
- **密码**：`admin123`

### 2. 修改数据库连接

编辑 `src/main/resources/application.yml`，修改数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trade?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 启动后端

```bash
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`，API 前缀为 `/api`。

启动成功后自动打开浏览器访问首页。

### 4. 启动前端

用 VS Code Live Server 或任意静态服务器打开 `frontend` 目录，例如：

```bash
cd frontend
npx serve .
```

浏览器访问 `http://localhost:3000`（端口以实际为准）。

> 前端 API 地址配置在 `frontend/js/api.js` 的 `API_BASE`，默认 `http://localhost:8080/api`。

## 使用流程示例

1. 学生注册 → 管理员登录后台审核用户
2. 学生登录 → 发布二手物品 → 管理员审核物品
3. 其他学生浏览购买 → 订单状态依次操作（付款/发货/收货）
4. 交易完成后双方互评
5. 发布/接取互助需求，完成后互评
6. 可通过私信联系，举报违规由管理员处理

## API 文档

启动后端后，访问 Swagger UI 查看完整 API 文档：

- **Swagger UI**：`http://localhost:8080/swagger-ui.html`
- **API Docs**：`http://localhost:8080/api-docs`

## API 说明

- 统一响应：`{ "code": 200, "message": "success", "data": {...} }`
- 分页响应：`{ "rows": [...], "total": 100 }`
- 认证方式：请求头 `Authorization: Bearer <token>`

主要接口：

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/login | 用户登录 |
| GET | /api/users/{id} | 获取用户信息 |
| GET | /api/items | 搜索物品列表 |
| GET | /api/items/{id} | 物品详情 |
| POST | /api/items | 发布物品 |
| PUT | /api/items/{id} | 更新物品 |
| DELETE | /api/items/{id} | 删除物品 |
| POST | /api/orders | 下单购买 |
| GET | /api/orders | 我的订单列表 |
| POST | /api/orders/{id}/status | 订单状态流转 |
| GET | /api/help | 互助需求列表 |
| GET | /api/help/{id} | 互助详情 |
| POST | /api/help | 发布互助需求 |
| POST | /api/help/{id}/accept | 接单 |
| POST | /api/help/{id}/complete | 完成互助 |
| GET | /api/messages | 私信列表 |
| POST | /api/messages | 发送私信 |
| GET | /api/notifications | 通知列表 |
| POST | /api/reports | 举报 |
| GET | /api/users/admin/students | 管理员：学生列表 |
| DELETE | /api/users/admin/{id} | 管理员：删除学生 |
| DELETE | /api/users/admin/batch | 管理员：批量删除学生 |
| PUT | /api/items/admin/{id}/status | 管理员：审核物品 |
| PUT | /api/help/admin/{id}/status | 管理员：审核互助 |
| GET | /api/reports/admin | 管理员：举报列表 |
| PUT | /api/reports/admin/{id} | 管理员：处理举报 |

## 技术说明

- **后端框架**：Spring Boot 3.3.5 + Spring MVC + MyBatis
- **认证方式**：JWT（jjwt 0.12.6），拦截器校验登录态
- **密码加密**：SHA-256 存储
- **API 文档**：SpringDoc OpenAPI 2.5.0（Swagger UI）
- **文件存储**：本地存储或阿里云 OSS（可配置）
- **分层架构**：Controller → Service → Mapper，职责清晰
- **异常处理**：全局异常处理器统一返回错误信息
- **公开接口**：物品列表、互助列表等浏览接口无需 Token

## 管理员功能

管理员登录后可进入后台管理面板，功能包括：

- **用户管理**：审核新注册学生、查看学生列表、单个/批量删除学生
- **物品管理**：审核待上架物品、下架违规物品
- **互助管理**：审核互助需求、下架违规互助
- **举报处理**：查看举报列表、处理举报（通过/驳回）