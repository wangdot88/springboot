-- 校园智能体育管理系统数据库初始化脚本

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    age INT COMMENT '年龄',
    class_name VARCHAR(100) COMMENT '班级',
    department VARCHAR(100) COMMENT '院系',
    phone VARCHAR(20) COMMENT '手机号',
    is_weak BOOLEAN DEFAULT FALSE COMMENT '是否为体弱学生',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_no (student_no),
    INDEX idx_class (class_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 体测项目表
CREATE TABLE IF NOT EXISTS test_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    unit VARCHAR(20) COMMENT '单位',
    gender VARCHAR(10) COMMENT '适用性别',
    min_score DECIMAL(5,2) COMMENT '最低分',
    max_score DECIMAL(5,2) COMMENT '最高分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_item_name (item_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测项目表';

-- 体测成绩表
CREATE TABLE IF NOT EXISTS test_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    test_year INT NOT NULL COMMENT '体测年份',
    test_date DATE COMMENT '测试日期',
    height DECIMAL(5,2) COMMENT '身高(cm)',
    weight DECIMAL(5,2) COMMENT '体重(kg)',
    bmi DECIMAL(5,2) COMMENT 'BMI指数',
    vital_capacity DECIMAL(5,2) COMMENT '肺活量(ml)',
    sit_and_reach DECIMAL(5,2) COMMENT '坐位体前屈(cm)',
    standing_long_jump DECIMAL(5,2) COMMENT '立定跳远(m)',
    50m_run DECIMAL(5,2) COMMENT '50米跑(s)',
    1000m_run DECIMAL(5,2) COMMENT '1000米跑(s)',
    800m_run DECIMAL(5,2) COMMENT '800米跑(s)',
    pull_up INT COMMENT '引体向上(个)',
    sit_up INT COMMENT '仰卧起坐(个)',
    total_score DECIMAL(5,2) COMMENT '总分',
    level VARCHAR(10) COMMENT '等级(A/B/C/D)',
    is_pass BOOLEAN DEFAULT FALSE COMMENT '是否合格',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student (student_id),
    INDEX idx_year (test_year),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测成绩表';

-- 跑步打卡记录表
CREATE TABLE IF NOT EXISTS running_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    record_date DATE NOT NULL COMMENT '打卡日期',
    distance DECIMAL(5,2) COMMENT '跑步距离(km)',
    duration INT COMMENT '跑步时长(分钟)',
    steps INT COMMENT '步数',
    speed DECIMAL(5,2) COMMENT '平均速度(km/h)',
    location VARCHAR(200) COMMENT '跑步地点',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_date (student_id, record_date),
    INDEX idx_date (record_date),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑步打卡记录表';

-- 运动提醒记录表
CREATE TABLE IF NOT EXISTS exercise_reminder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    reminder_date DATE NOT NULL COMMENT '提醒日期',
    content TEXT COMMENT '提醒内容',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student (student_id),
    INDEX idx_date (reminder_date),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动提醒记录表';

-- 体育器材表
CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '器材编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '器材名称',
    category VARCHAR(50) COMMENT '器材类别',
    brand VARCHAR(100) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    total_count INT DEFAULT 0 COMMENT '总数量',
    available_count INT DEFAULT 0 COMMENT '可用数量',
    borrowed_count INT DEFAULT 0 COMMENT '已借出数量',
    broken_count INT DEFAULT 0 COMMENT '损坏数量',
    alert_threshold INT DEFAULT 5 COMMENT '预警阈值',
    purchase_date DATE COMMENT '采购日期',
    warranty_period INT COMMENT '质保期(月)',
    status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态(NORMAL/LOW_STOCK/DAMAGED)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equipment_no (equipment_no),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体育器材表';

-- 器材借出记录表
CREATE TABLE IF NOT EXISTS equipment_borrow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrow_no VARCHAR(50) NOT NULL UNIQUE COMMENT '借据编号',
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    borrow_count INT NOT NULL COMMENT '借出数量',
    borrow_date DATE NOT NULL COMMENT '借出日期',
    expected_return_date DATE NOT NULL COMMENT '预计归还日期',
    actual_return_date DATE COMMENT '实际归还日期',
    status VARCHAR(20) DEFAULT 'BORROWED' COMMENT '状态(BORROWED/RETURNED/OVERDUE)',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equipment (equipment_id),
    INDEX idx_student (student_id),
    INDEX idx_status (status),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材借出记录表';

-- 器材报修表
CREATE TABLE IF NOT EXISTS equipment_repair (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    repair_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报修编号',
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    reporter_id BIGINT COMMENT '报修人ID(学生)',
    reporter_name VARCHAR(100) COMMENT '报修人姓名',
    repair_count INT NOT NULL COMMENT '报修数量',
    problem_description TEXT COMMENT '问题描述',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态(PENDING/REPAIRING/COMPLETED/CANCELLED)',
    repair_result TEXT COMMENT '维修结果',
    repair_cost DECIMAL(10,2) COMMENT '维修费用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equipment (equipment_id),
    INDEX idx_status (status),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材报修表';

-- 初始化体测项目数据
INSERT INTO test_item (item_name, unit, gender, min_score, max_score) VALUES
('身高', 'cm', 'ALL', 140, 200),
('体重', 'kg', 'ALL', 30, 150),
('肺活量', 'ml', 'MALE', 2000, 7000),
('肺活量', 'ml', 'FEMALE', 1500, 5000),
('坐位体前屈', 'cm', 'ALL', -20, 30),
('立定跳远', 'm', 'MALE', 1.5, 3.0),
('立定跳远', 'm', 'FEMALE', 1.2, 2.5),
('50米跑', 's', 'MALE', 6, 15),
('50米跑', 's', 'FEMALE', 7, 18),
('1000米跑', 's', 'MALE', 180, 480),
('800米跑', 's', 'FEMALE', 180, 540),
('引体向上', '个', 'MALE', 0, 30),
('仰卧起坐', '个', 'FEMALE', 0, 60);

-- 初始化器材数据
INSERT INTO equipment (equipment_no, equipment_name, category, brand, total_count, available_count, alert_threshold) VALUES
('EQ001', '篮球', '球类', '斯伯丁', 50, 50, 10),
('EQ002', '足球', '球类', '耐克', 40, 40, 8),
('EQ003', '羽毛球拍', '球拍类', '尤尼克斯', 100, 100, 20),
('EQ004', '乒乓球拍', '球拍类', '红双喜', 80, 80, 15),
('EQ005', '排球', '球类', '李宁', 30, 30, 5),
('EQ006', '跳绳', '健身器材', '得力', 200, 200, 30),
('EQ007', '哑铃', '力量器材', '康强', 100, 100, 20),
('EQ008', '跑步机', '大型器材', '舒华', 10, 10, 2);
