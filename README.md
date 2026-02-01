# 校园智能体育管理系统 API 文档

## 项目简介

校园智能体育管理系统是一个综合性的体育管理平台，包含体测管理、运动监督和体育器材管理三大核心模块。

## 技术栈

- Spring Boot 1.5.10
- Spring Data JPA
- MySQL
- Redis
- Apache POI (Excel处理)
- Swagger2 (API文档)

## 系统功能

### 1. 体测管理
- 体测成绩Excel格式导入
- 自动计算体测成绩等级（分为ABCD）
- 按班级生成合格率报告
- 自动标记体弱学生

### 2. 运动监督
- 每天的跑步打卡
- 班级运动排行榜
- 未运动的学生提醒
- 导出每日运动报告

### 3. 体育器材管理
- 器材借出登记
- 借用超时提醒
- 器材报修功能
- 库存不足预警
- 导出器材年度使用报告

## API接口

### 系统接口

#### 健康检查
```
GET /api/health
```

#### 系统信息
```
GET /api/info
```

### 学生管理

#### 添加学生
```
POST /api/students/add
Content-Type: application/json

{
  "studentId": "2021001",
  "name": "张三",
  "gender": "男",
  "age": 20,
  "className": "计算机1班",
  "major": "计算机科学与技术",
  "phone": "13800138000",
  "email": "zhangsan@example.com"
}
```

#### 更新学生信息
```
PUT /api/students/update/{studentId}
Content-Type: application/json

{
  "name": "张三",
  "gender": "男",
  "age": 21,
  "className": "计算机1班",
  "major": "计算机科学与技术",
  "phone": "13800138001",
  "email": "zhangsan@example.com"
}
```

#### 删除学生
```
DELETE /api/students/delete/{studentId}
```

#### 获取学生信息
```
GET /api/students/{studentId}
```

#### 获取所有学生
```
GET /api/students/all
```

#### 按班级获取学生
```
GET /api/students/class/{className}
```

#### 获取体弱学生
```
GET /api/students/weak
```

### 体测管理

#### 导入体测成绩
```
POST /api/physical-test/import
Content-Type: multipart/form-data

file: Excel文件
```

Excel格式示例：
| 学号 | 测试类型 | 50米跑 | 坐位体前屈 | 立定跳远 | 引体向上 | 仰卧起坐 | 800米跑 | 1000米跑 |
|------|----------|--------|------------|----------|----------|----------|---------|----------|
| 2021001 | 期中测试 | 7.2 | 18 | 230 | 10 |  |  | 240 |

#### 生成班级体测报告
```
GET /api/physical-test/class-report/{className}?testType=期中测试
```

#### 获取体弱学生
```
GET /api/physical-test/weak-students
```

#### 获取班级体弱学生
```
GET /api/physical-test/weak-students/{className}
```

### 运动监督

#### 运动打卡
```
POST /api/exercise/check-in
Content-Type: application/x-www-form-urlencoded

studentId=2021001&exerciseType=跑步&distance=3000&duration=30&location=操场
```

#### 获取班级排行榜
```
GET /api/exercise/class-ranking?date=2023-05-20
```

#### 获取学生排行榜
```
GET /api/exercise/student-ranking?date=2023-05-20
```

#### 获取未运动学生
```
GET /api/exercise/non-exercised-students?date=2023-05-20
```

#### 获取一周未运动学生
```
GET /api/exercise/non-exercised-week?startDate=2023-05-15
```

#### 生成每日运动报告
```
GET /api/exercise/daily-report?date=2023-05-20
```

#### 获取学生运动历史
```
GET /api/exercise/student-history/{studentId}?startDate=2023-05-01&endDate=2023-05-31
```

### 体育器材管理

#### 添加器材
```
POST /api/equipment/add
Content-Type: application/x-www-form-urlencoded

equipmentCode=E001&name=篮球&category=球类&brand=斯伯丁&model=7号&totalQuantity=20&minStockAlert=5&description=标准比赛用球
```

#### 借用器材
```
POST /api/equipment/borrow
Content-Type: application/x-www-form-urlencoded

studentId=2021001&equipmentCode=E001&quantity=2&expectedReturnDate=2023-05-25 10:00:00&remarks=体育课使用
```

#### 归还器材
```
POST /api/equipment/return
Content-Type: application/x-www-form-urlencoded

borrowId=1&remarks=完好无损
```

#### 获取逾期借用记录
```
GET /api/equipment/overdue
```

#### 获取库存不足器材
```
GET /api/equipment/low-stock
```

#### 报修器材
```
POST /api/equipment/report-repair
Content-Type: application/x-www-form-urlencoded

equipmentCode=E001&studentId=2021001&quantity=1&repairReason=漏气&repairDescription=篮球表面出现漏气现象
```

#### 开始维修
```
POST /api/equipment/start-repair
Content-Type: application/x-www-form-urlencoded

repairId=1
```

#### 完成维修
```
POST /api/equipment/complete-repair
Content-Type: application/x-www-form-urlencoded

repairId=1&repairCost=50.0
```

#### 获取学生当前借用记录
```
GET /api/equipment/current-borrows/{studentId}
```

#### 获取学生借用历史
```
GET /api/equipment/borrow-history/{studentId}
```

#### 获取待维修记录
```
GET /api/equipment/pending-repairs
```

#### 获取所有器材
```
GET /api/equipment/all
```

### 报告导出

#### 导出体测报告
```
GET /api/reports/physical-test?className=计算机1班&testType=期中测试
```

#### 导出每日运动报告
```
GET /api/reports/daily-exercise?date=2023-05-20
```

#### 导出器材年度报告
```
GET /api/reports/equipment-annual?year=2023
```

#### 导出体弱学生报告
```
GET /api/reports/weak-students
```

## 定时任务

系统包含以下定时任务：

1. **每天9:00** - 检查逾期器材借用记录
2. **每天10:00** - 检查库存不足器材
3. **每天20:00** - 检查今日未运动学生
4. **每周一9:00** - 检查上周运动情况
5. **每月1号8:00** - 生成月度报告

## 数据库表结构

### 学生表 (student)
- id: 主键
- student_id: 学号
- name: 姓名
- gender: 性别
- age: 年龄
- class_name: 班级
- major: 专业
- phone: 电话
- email: 邮箱
- is_weak_physical: 是否体弱
- created_at: 创建时间
- updated_at: 更新时间

### 体测表 (physical_test)
- id: 主键
- student_id: 学生ID
- test_type: 测试类型
- score: 总分
- grade: 等级
- run_50m: 50米跑
- sit_and_reach: 坐位体前屈
- long_jump: 立定跳远
- pull_up: 引体向上
- sit_up: 仰卧起坐
- run_800m: 800米跑
- run_1000m: 1000米跑
- test_date: 测试日期
- created_at: 创建时间
- updated_at: 更新时间

### 运动记录表 (exercise_record)
- id: 主键
- student_id: 学生ID
- exercise_type: 运动类型
- distance: 距离
- duration: 时长
- calories: 卡路里
- location: 运动地点
- exercise_date: 运动日期
- created_at: 创建时间
- updated_at: 更新时间

### 器材表 (equipment)
- id: 主键
- equipment_code: 器材编码
- name: 器材名称
- category: 类别
- brand: 品牌
- model: 型号
- total_quantity: 总数量
- available_quantity: 可用数量
- borrowed_quantity: 借用数量
- repair_quantity: 维修数量
- min_stock_alert: 最低库存预警
- description: 描述
- created_at: 创建时间
- updated_at: 更新时间

### 器材借用表 (equipment_borrow)
- id: 主键
- student_id: 学生ID
- equipment_id: 器材ID
- quantity: 数量
- borrow_date: 借用日期
- expected_return_date: 预计归还日期
- actual_return_date: 实际归还日期
- status: 状态
- remarks: 备注
- created_at: 创建时间
- updated_at: 更新时间

### 器材维修表 (equipment_repair)
- id: 主键
- equipment_id: 器材ID
- student_id: 学生ID
- quantity: 数量
- repair_reason: 维修原因
- status: 状态
- repair_description: 维修描述
- repair_cost: 维修费用
- report_date: 报修日期
- repair_date: 维修日期
- completed_date: 完成日期
- created_at: 创建时间
- updated_at: 更新时间

## 部署说明

1. 配置数据库连接信息（application.yml）
2. 配置Redis连接信息
3. 配置邮件服务器信息（用于发送提醒）
4. 运行 `mvn clean package` 打包
5. 运行 `java -jar target/springboot-0.0.1-SNAPSHOT.jar` 启动应用

## API文档

启动应用后，访问 http://localhost:8088/swagger-ui.html 查看完整的API文档。