# 工单管理系统

## 功能概述

这是一个基于Spring Boot的工单管理系统，实现了以下功能：

### 1. 工单创建与管理
- 用户可以创建工单，支持工单分类和优先级
- 工单状态分为：待处理、处理中、已解决、已关闭
- 支持工单的创建、查看、更新和删除

### 2. 工单分配
- 自动分配工单
- 手动分配工单
- 工单转交功能

### 3. 工单回复
- 支持文字回复
- 支持图片回复
- 工单处理时间跟踪
- 关闭工单功能

### 4. 用户评价
- 工单解决后邀请用户评价打分（1-5分）
- 支持评价内容填写

### 5. 统计功能
- 统计工单总数
- 按状态统计工单数量
- 统计工单平均解决时间

## 技术架构

### 后端技术
- Spring Boot 1.5.10.RELEASE
- Spring Data JPA
- MySQL
- Maven

### 前端技术
- HTML/CSS/JavaScript
- Thymeleaf模板引擎

## 项目结构

```
src/main/java/dangod/springboot/
├── entity/ticket/          # 实体类
│   ├── Ticket.java        # 工单实体
│   ├── TicketReply.java   # 工单回复实体
│   ├── TicketEvaluation.java # 工单评价实体
│   └── User.java          # 用户实体
├── repository/ticket/     # 数据访问层
│   ├── TicketRepository.java
│   ├── TicketReplyRepository.java
│   ├── TicketEvaluationRepository.java
│   └── UserRepository.java
├── service/ticket/        # 服务层
│   ├── TicketService.java
│   └── impl/
│       └── TicketServiceImpl.java
└── controller/ticket/     # 控制层
    └── TicketController.java
```

## API接口

### 工单管理
- `POST /api/tickets` - 创建工单
- `GET /api/tickets/{id}` - 获取工单详情
- `GET /api/tickets` - 获取所有工单
- `GET /api/tickets/status/{status}` - 按状态获取工单
- `PUT /api/tickets/{id}` - 更新工单
- `PUT /api/tickets/{id}/assign` - 分配工单
- `PUT /api/tickets/{id}/transfer` - 转交工单
- `PUT /api/tickets/{id}/start` - 开始处理工单
- `PUT /api/tickets/{id}/resolve` - 标记工单为已解决
- `PUT /api/tickets/{id}/close` - 关闭工单

### 工单回复
- `POST /api/tickets/{id}/replies` - 添加回复
- `GET /api/tickets/{id}/replies` - 获取工单回复

### 工单评价
- `POST /api/tickets/{id}/evaluation` - 添加评价
- `GET /api/tickets/{id}/evaluation` - 获取工单评价

### 统计功能
- `GET /api/tickets/stats/total` - 统计总工单数
- `GET /api/tickets/stats/status/{status}` - 按状态统计工单
- `GET /api/tickets/stats/average-resolve-time` - 平均解决时间

## 数据库设计

### 工单表 (ticket)
- id: 主键
- title: 工单标题
- content: 工单内容
- category: 工单分类
- priority: 优先级（1-5）
- status: 工单状态
- creator_id: 创建者ID
- assignee_id: 处理者ID
- create_time: 创建时间
- update_time: 更新时间
- resolve_time: 解决时间
- close_time: 关闭时间

### 工单回复表 (ticket_reply)
- id: 主键
- ticket_id: 工单ID
- user_id: 用户ID
- content: 回复内容
- image_url: 图片URL
- create_time: 创建时间

### 工单评价表 (ticket_evaluation)
- id: 主键
- ticket_id: 工单ID
- score: 评分（1-5）
- comment: 评价内容
- evaluate_time: 评价时间

### 用户表 (user)
- id: 主键
- username: 用户名
- password: 密码
- name: 姓名
- email: 邮箱
- phone: 电话
- role: 角色
- create_time: 创建时间
- update_time: 更新时间

## 运行要求

- Java 8
- MySQL 5.7+
- Maven 3.3+

## 启动步骤

1. 配置数据库连接
2. 运行 `mvn clean install`
3. 运行 `mvn spring-boot:run`
4. 访问 http://localhost:8080/ticket 查看工单管理页面

## 测试

项目包含单元测试，可以通过以下命令运行：

```bash
mvn test
```

## 总结

这个工单管理系统实现了所有要求的功能，包括工单创建、分配、回复、评价和统计。系统采用了分层架构，代码结构清晰，易于维护和扩展。