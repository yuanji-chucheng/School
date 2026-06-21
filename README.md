# 校园二手物品交易与互助服务平台

基于 **Java + Spring Boot + MyBatis + MySQL 8.0** 的前后端分离项目，前端使用原生 HTML/CSS/JavaScript，通过 Ajax 调用 RESTful API，认证采用 JWT Access Token。

## 功能模块

| 模块 | 说明 |
|------|------|
| 用户注册登录 | 学号注册，管理员审核后可登录；角色：学生/管理员 |
| 二手物品 | 发布（分类、新旧、图片URL）、管理员审核、搜索浏览 |
| 交易订单 | 下单→待付款→待发货→待收货→已完成；可取消待付款订单 |
| 交易评价 | 完成后双方互评，影响信用分 |
| 互助服务 | 发布需求→接单→进行中→完成；完成后互评 |
| 站内私信 | 买卖双方私信沟通 |
| 系统通知 | 审核结果、订单变化等通知 |
| 举报 | 用户举报违规内容，管理员处理 |

## 项目结构

```
demo1/
├── sql/schema.sql              # 数据库建表脚本
├── frontend/                   # 前端页面（独立部署或用 Live Server 打开）
│   ├── css/common.css
│   ├── js/api.js               # 所有 API 封装
│   └── *.html                  # 各功能页面
└── src/main/java/com/example/demo/
    ├── controller/             # REST 接口层
    ├── service/                # 业务逻辑层
    ├── mapper/                 # MyBatis 接口
    ├── entity/                 # 实体类
    ├── dto/                    # 数据传输对象
    ├── common/                 # 统一响应、异常处理
    ├── config/                 # 跨域、JWT 拦截器
    └── util/                   # JWT、密码工具
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

编辑 `src/main/resources/application.yml`，修改用户名和密码：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trade?...
    username: root
    password: 你的密码
```

### 3. 启动后端

```bash
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`，API 前缀为 `/api`。

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

## API 说明

- 统一响应：`{ "code": 200, "message": "success", "data": {...} }`
- 分页响应：`{ "rows": [...], "total": 100 }`
- 认证方式：请求头 `Authorization: Bearer <token>`

主要接口：

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 注册 |
| POST | /api/auth/login | 登录 |
| GET | /api/items | 搜索物品 |
| POST | /api/items | 发布物品 |
| POST | /api/orders?itemId= | 下单 |
| POST | /api/orders/{id}/status?status= | 订单流转 |
| GET/POST | /api/help | 互助需求 |
| GET/POST | /api/messages | 私信 |
| GET | /api/notifications | 通知列表 |

## 技术说明

- 后端采用 Spring Boot 整合 Spring MVC + MyBatis，分层清晰
- JWT 拦截器校验登录态，公开浏览接口（物品/互助列表）无需 Token
- 密码使用 SHA-256 加密存储
- 图片以 URL 字符串存储，多张用逗号分隔
