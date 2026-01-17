-- 创建数据库
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo;

-- 班级表
CREATE TABLE IF NOT EXISTS tb_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL UNIQUE COMMENT '班级名称',
    grade VARCHAR(20) COMMENT '年级',
    major VARCHAR(10) COMMENT '专业',
    student_count INT COMMENT '学生人数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级信息表';

-- 学生表
CREATE TABLE IF NOT EXISTS tb_student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    age INT COMMENT '年龄',
    phone VARCHAR(20) COMMENT '电话',
    class_id BIGINT COMMENT '班级ID',
    is_weak BOOLEAN DEFAULT FALSE COMMENT '是否体弱',
    FOREIGN KEY (class_id) REFERENCES tb_class(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 体测表
CREATE TABLE IF NOT EXISTS tb_fitness_test (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    year INT NOT NULL COMMENT '年份',
    semester INT NOT NULL COMMENT '学期',
    run1000 DOUBLE COMMENT '1000米跑成绩(分钟)',
    run800 DOUBLE COMMENT '800米跑成绩(分钟)',
    run50 DOUBLE COMMENT '50米跑成绩(秒)',
    sit_and_reach INT COMMENT '坐位体前屈(厘米)',
    standing_long_jump INT COMMENT '立定跳远(厘米)',
    sit_up INT COMMENT '仰卧起坐(个)',
    pull_up INT COMMENT '引体向上(个)',
    bmi DOUBLE COMMENT 'BMI指数',
    total_score DOUBLE COMMENT '总分',
    level VARCHAR(10) COMMENT '等级(ABCD)',
    is_qualified BOOLEAN COMMENT '是否合格',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES tb_student(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测成绩表';

-- 运动记录表
CREATE TABLE IF NOT EXISTS tb_exercise_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    exercise_type VARCHAR(50) NOT NULL COMMENT '运动类型',
    distance DOUBLE COMMENT '距离(米)',
    duration INT COMMENT '时长(分钟)',
    calories DOUBLE COMMENT '卡路里',
    location VARCHAR(100) COMMENT '地点',
    create_time DATETIME COMMENT '创建时间',
    FOREIGN KEY (student_id) REFERENCES tb_student(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动记录表';

-- 器材表
CREATE TABLE IF NOT EXISTS tb_equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '器材编号',
    name VARCHAR(100) NOT NULL COMMENT '器材名称',
    category VARCHAR(50) COMMENT '类别',
    brand VARCHAR(50) COMMENT '品牌',
    model VARCHAR(50) COMMENT '型号',
    total_count INT COMMENT '总数',
    available_count INT COMMENT '可用数量',
    min_stock INT COMMENT '最小库存',
    price DOUBLE COMMENT '价格',
    purchase_date DATE COMMENT '购买日期',
    status VARCHAR(20) COMMENT '状态',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材表';

-- 器材借用表
CREATE TABLE IF NOT EXISTS tb_equipment_borrow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    borrow_count INT NOT NULL COMMENT '借用数量',
    borrow_date DATETIME NOT NULL COMMENT '借用日期',
    return_date DATETIME COMMENT '归还日期',
    expected_return_date DATETIME COMMENT '预计归还日期',
    status VARCHAR(20) COMMENT '状态',
    is_overdue BOOLEAN DEFAULT FALSE COMMENT '是否超时',
    remarks VARCHAR(200) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES tb_student(id),
    FOREIGN KEY (equipment_id) REFERENCES tb_equipment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材借用表';

-- 器材维修表
CREATE TABLE IF NOT EXISTS tb_equipment_repair (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    repair_reason VARCHAR(200) NOT NULL COMMENT '维修原因',
    repair_count INT COMMENT '维修数量',
    reporter VARCHAR(50) COMMENT '报修人',
    reporter_phone VARCHAR(20) COMMENT '报修人电话',
    status VARCHAR(20) COMMENT '状态',
    repair_result VARCHAR(200) COMMENT '维修结果',
    repair_cost DOUBLE COMMENT '维修费用',
    report_date DATETIME COMMENT '报修日期',
    repair_date DATETIME COMMENT '维修日期',
    complete_date DATETIME COMMENT '完成日期',
    remarks VARCHAR(200) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (equipment_id) REFERENCES tb_equipment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材维修表';

-- 插入测试数据
INSERT INTO tb_class (class_name, grade, major, student_count) VALUES
('计算机1班', '2023', '计算机科学', 30),
('计算机2班', '2023', '计算机科学', 28),
('软件工程1班', '2023', '软件工程', 32);

INSERT INTO tb_student (student_no, name, gender, age, phone, class_id) VALUES
('2023001', '张三', '男', 20, '13800138001', 1),
('2023002', '李四', '男', 20, '13800138002', 1),
('2023003', '王五', '女', 19, '13800138003', 1),
('2023004', '赵六', '男', 20, '13800138004', 2),
('2023005', '钱七', '女', 19, '13800138005', 2),
('2023006', '孙八', '男', 20, '13800138006', 3);

INSERT INTO tb_equipment (equipment_no, name, category, brand, model, total_count, available_count, min_stock, price, status) VALUES
('EQ001', '篮球', '球类', '斯伯丁', 'TF-1000', 20, 20, 5, 150.00, 'AVAILABLE'),
('EQ002', '足球', '球类', '耐克', 'Premier', 15, 15, 3, 200.00, 'AVAILABLE'),
('EQ003', '羽毛球拍', '球拍', '尤尼克斯', 'ArcSaber', 30, 30, 10, 300.00, 'AVAILABLE'),
('EQ004', '乒乓球拍', '球拍', '红双喜', '狂飙3', 25, 25, 8, 100.00, 'AVAILABLE'),
('EQ005', '跳绳', '健身器材', '李宁', 'LJ-001', 50, 50, 15, 30.00, 'AVAILABLE');
