# 校园智能体育管理后端系统

## 项目简介

这是一个基于Spring Boot开发的校园智能体育管理后端系统，提供体测管理、运动监督和体育器材管理三大核心功能。

## 功能模块

### 1. 体测管理
- Excel格式导入体测成绩
- 自动计算体测成绩等级（ABCD）
- 按班级生成合格率报告
- 自动标记体弱学生
- 导出班级体测合格率报告

### 2. 运动监督
- 每日跑步打卡
- 班级运动排行榜
- 未运动学生提醒
- 导出每日运动报告

### 3. 体育器材管理
- 器材借出登记
- 借用超时提醒
- 器材报修功能
- 库存不足预警
- 导出器材年度使用报告

## 技术栈

- Spring Boot 1.5.10
- Spring Data JPA
- MySQL
- Druid连接池
- Redis
- Apache POI（Excel处理）
- Quartz（定时任务）
- Swagger2（API文档）

## 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p demo < src/main/resources/sql/init.sql
```

3. 修改配置文件 `src/main/resources/config/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo?characterEncoding=utf-8
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

## API接口文档

### 基础数据管理

#### 班级管理
- `GET /api/base/classes` - 获取所有班级
- `GET /api/base/classes/{id}` - 根据ID获取班级
- `POST /api/base/classes` - 创建班级
- `DELETE /api/base/classes/{id}` - 删除班级

#### 学生管理
- `GET /api/base/students` - 获取所有学生
- `GET /api/base/students/{id}` - 根据ID获取学生
- `GET /api/base/students/class/{classId}` - 根据班级ID获取学生
- `POST /api/base/students` - 创建学生
- `DELETE /api/base/students/{id}` - 删除学生

### 体测管理

- `POST /api/fitness/import` - 导入体测成绩（Excel）
- `GET /api/fitness/list?year=2024&semester=1` - 获取体测成绩列表
- `GET /api/fitness/class/{classId}?year=2024&semester=1` - 获取班级体测成绩
- `GET /api/fitness/weak` - 获取体弱学生列表
- `GET /api/fitness/unqualified?year=2024&semester=1` - 获取不合格学生
- `GET /api/fitness/report/class?year=2024&semester=1` - 生成班级合格率报告
- `GET /api/fitness/report/class/export?year=2024&semester=1` - 导出班级合格率报告
- `POST /api/fitness/save` - 保存体测成绩
- `DELETE /api/fitness/{id}` - 删除体测成绩

### 运动监督

- `POST /api/exercise/record` - 记录运动
  - 参数：studentId, exerciseType, distance, duration, calories, location
- `GET /api/exercise/student/{studentId}` - 获取学生运动记录
- `GET /api/exercise/date/{date}` - 获取指定日期的运动记录
- `GET /api/exercise/class/{classId}/date/{date}` - 获取班级指定日期的运动记录
- `GET /api/exercise/ranking/daily?date=2024-01-01` - 获取每日排行榜
- `GET /api/exercise/ranking/class/{classId}?date=2024-01-01` - 获取班级每日排行榜
- `GET /api/exercise/ranking/period?startDate=2024-01-01&endDate=2024-01-31` - 获取时间段排行榜
- `GET /api/exercise/inactive/{classId}?date=2024-01-01` - 获取未运动学生
- `GET /api/exercise/report/daily/export?date=2024-01-01` - 导出每日运动报告

### 体育器材管理

- `GET /api/equipment/list` - 获取所有器材
- `GET /api/equipment/low-stock` - 获取库存不足的器材
- `POST /api/equipment/borrow` - 借用器材
  - 参数：studentId, equipmentId, borrowCount, expectedReturnDate, remarks
- `POST /api/equipment/return/{borrowId}` - 归还器材
- `GET /api/equipment/overdue` - 获取超时未归还的器材
- `GET /api/equipment/student/{studentId}/active` - 获取学生当前借用的器材
- `GET /api/equipment/equipment/{equipmentId}/borrows` - 获取器材借用记录
- `POST /api/equipment/repair` - 报修器材
  - 参数：equipmentId, repairReason, repairCount, reporter, reporterPhone, remarks
- `POST /api/equipment/repair/complete/{repairId}` - 完成维修
  - 参数：repairResult, repairCost
- `GET /api/equipment/equipment/{equipmentId}/repairs` - 获取器材维修记录
- `GET /api/equipment/repairs/pending` - 获取待处理的维修
- `GET /api/equipment/report/annual?year=2024` - 生成年度使用报告
- `GET /api/equipment/report/annual/export?year=2024` - 导出年度使用报告
- `POST /api/equipment/save` - 保存器材
- `DELETE /api/equipment/{id}` - 删除器材
- `DELETE /api/equipment/borrow/{id}` - 删除借用记录
- `DELETE /api/equipment/repair/{id}` - 删除维修记录

## 定时任务

系统包含以下定时任务：

1. **器材超时检查** - 每天20:00执行
   - 检查超时未归还的器材并标记

2. **未运动学生检查** - 每天21:00执行
   - 检查当日未运动的学生并记录

3. **每日运动报告生成** - 每天22:00执行
   - 生成当日运动排行榜

4. **每周运动报告生成** - 每周一9:00执行
   - 生成上周运动排行榜

## Excel导入格式

### 体测成绩导入格式

| 学号 | 姓名 | 年份 | 学期 | 1000米跑 | 800米跑 | 50米跑 | 坐位体前屈 | 立定跳远 | 仰卧起坐 | 引体向上 | BMI |
|------|------|------|------|----------|---------|--------|------------|----------|----------|----------|-----|
| 2023001 | 张三 | 2024 | 1 | 4.5 | 3.8 | 7.2 | 18 | 220 | 35 | 8 | 22.5 |

## 项目结构

```
src/main/java/dangod/springboot/
├── config/              # 配置类
│   ├── druid/          # Druid配置
│   ├── redis/          # Redis配置
│   └── SchedulingConfig.java  # 定时任务配置
├── controller/         # 控制器
│   ├── BaseController.java
│   ├── FitnessTestController.java
│   ├── ExerciseController.java
│   └── EquipmentController.java
├── core/              # 核心工具
│   ├── config/
│   ├── response/      # 统一响应
│   └── util/
├── dto/               # 数据传输对象
│   ├── FitnessTestImportDTO.java
│   ├── ClassReportDTO.java
│   ├── ExerciseRankingDTO.java
│   └── EquipmentReportDTO.java
├── entity/            # 实体类
│   ├── ClassInfo.java
│   ├── Student.java
│   ├── FitnessTest.java
│   ├── ExerciseRecord.java
│   ├── Equipment.java
│   ├── EquipmentBorrow.java
│   └── EquipmentRepair.java
├── repository/        # 数据访问层
│   ├── ClassInfoRepository.java
│   ├── StudentRepository.java
│   ├── FitnessTestRepository.java
│   ├── ExerciseRecordRepository.java
│   ├── EquipmentRepository.java
│   ├── EquipmentBorrowRepository.java
│   └── EquipmentRepairRepository.java
├── service/           # 业务逻辑层
│   ├── FitnessTestService.java
│   ├── ExerciseService.java
│   └── EquipmentService.java
└── task/              # 定时任务
    └── ScheduledTask.java
```

## 启动项目

1. 确保MySQL和Redis已启动
2. 配置数据库连接信息
3. 运行主类 `SpringbootApplication.java`
4. 访问 `http://localhost:8088` 查看系统

## 注意事项

1. Excel导入文件格式必须严格按照模板格式
2. 体测成绩等级划分标准：A(90-100)、B(80-89)、C(60-79)、D(0-59)
3. 器材借用时会自动检查库存是否充足
4. 定时任务默认开启，可根据需要调整执行时间

## 开发者

系统基于Spring Boot 1.5.10开发，使用JPA进行数据持久化，支持MySQL数据库。
