CREATE DATABASE IF NOT EXISTS campus_sports DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_sports;

CREATE TABLE t_clazz (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    head_teacher VARCHAR(50) COMMENT '班主任',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    create_time DATETIME,
    update_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

CREATE TABLE t_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    age INT COMMENT '年龄',
    class_id BIGINT COMMENT '班级ID',
    is_weak TINYINT(1) DEFAULT 0 COMMENT '是否体弱学生',
    height DOUBLE COMMENT '身高(cm)',
    weight DOUBLE COMMENT '体重(kg)',
    phone VARCHAR(20) COMMENT '手机号',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_student_no (student_no),
    INDEX idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

CREATE TABLE t_fitness_test (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    test_date DATE COMMENT '测试日期',
    semester VARCHAR(50) COMMENT '学期',
    height DOUBLE COMMENT '身高(cm)',
    weight DOUBLE COMMENT '体重(kg)',
    bmi DOUBLE COMMENT 'BMI指数',
    vital_capacity INT COMMENT '肺活量',
    vital_capacity_grade VARCHAR(5) COMMENT '肺活量等级',
    fifty_meter DOUBLE COMMENT '50米跑(秒)',
    fifty_meter_grade VARCHAR(5) COMMENT '50米跑等级',
    jump INT COMMENT '立定跳远(cm)',
    jump_grade VARCHAR(5) COMMENT '立定跳远等级',
    sit_reach DOUBLE COMMENT '坐位体前屈(cm)',
    sit_reach_grade VARCHAR(5) COMMENT '坐位体前屈等级',
    endurance_run INT COMMENT '耐力跑(秒)',
    endurance_run_grade VARCHAR(5) COMMENT '耐力跑等级',
    pull_up INT COMMENT '引体向上(个)',
    pull_up_grade VARCHAR(5) COMMENT '引体向上等级',
    sit_up INT COMMENT '仰卧起坐(个)',
    sit_up_grade VARCHAR(5) COMMENT '仰卧起坐等级',
    total_score INT COMMENT '总分',
    total_grade VARCHAR(5) COMMENT '总等级(A/B/C/D)',
    is_passed TINYINT(1) DEFAULT 0 COMMENT '是否及格',
    is_weak_student TINYINT(1) DEFAULT 0 COMMENT '是否体弱学生',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_student_id (student_id),
    INDEX idx_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测成绩表';

CREATE TABLE t_fitness_test_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT COMMENT '班级ID',
    semester VARCHAR(50) COMMENT '学期',
    total_students INT COMMENT '总人数',
    tested_students INT COMMENT '已测试人数',
    passed_students INT COMMENT '及格人数',
    failed_students INT COMMENT '不及格人数',
    pass_rate DOUBLE COMMENT '及格率',
    grade_a_count INT COMMENT 'A级人数',
    grade_b_count INT COMMENT 'B级人数',
    grade_c_count INT COMMENT 'C级人数',
    grade_d_count INT COMMENT 'D级人数',
    weak_students_count INT COMMENT '体弱学生人数',
    avg_total_score DOUBLE COMMENT '平均总分',
    report_data TEXT COMMENT '报告数据JSON',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_class_id (class_id),
    INDEX idx_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测报告表';

CREATE TABLE t_running_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    record_date DATE NOT NULL COMMENT '打卡日期',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    distance DOUBLE COMMENT '距离(km)',
    duration INT COMMENT '时长(秒)',
    avg_pace DOUBLE COMMENT '平均配速',
    calories INT COMMENT '消耗卡路里',
    location VARCHAR(200) COMMENT '运动地点',
    status VARCHAR(20) COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_student_id (student_id),
    INDEX idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑步记录表';

CREATE TABLE t_sports_daily_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_date DATE NOT NULL COMMENT '报告日期',
    class_id BIGINT COMMENT '班级ID',
    total_students INT COMMENT '总人数',
    checked_in_count INT COMMENT '已打卡人数',
    not_checked_in_count INT COMMENT '未打卡人数',
    total_distance DOUBLE COMMENT '总里程',
    avg_distance DOUBLE COMMENT '平均里程',
    check_in_rate DOUBLE COMMENT '打卡率',
    report_data TEXT COMMENT '报告数据JSON',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_report_date (report_date),
    INDEX idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动日报表';

CREATE TABLE t_equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '器材名称',
    code VARCHAR(50) UNIQUE COMMENT '器材编号',
    category VARCHAR(50) COMMENT '类别',
    brand VARCHAR(50) COMMENT '品牌',
    model VARCHAR(50) COMMENT '型号',
    total_quantity INT COMMENT '总数量',
    available_quantity INT COMMENT '可用数量',
    borrowed_quantity INT COMMENT '借出数量',
    repair_quantity INT COMMENT '维修中数量',
    min_stock INT COMMENT '最低库存',
    status VARCHAR(20) COMMENT '状态',
    location VARCHAR(100) COMMENT '存放位置',
    purchase_date DATE COMMENT '采购日期',
    price DOUBLE COMMENT '单价',
    description VARCHAR(500) COMMENT '描述',
    image_url VARCHAR(500) COMMENT '图片URL',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_code (code),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材表';

CREATE TABLE t_equipment_borrow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    borrow_date DATE COMMENT '借出日期',
    expected_return_date DATE COMMENT '预计归还日期',
    actual_return_date DATE COMMENT '实际归还日期',
    quantity INT COMMENT '数量',
    status VARCHAR(20) COMMENT '状态',
    is_overdue TINYINT(1) DEFAULT 0 COMMENT '是否超期',
    borrow_purpose VARCHAR(500) COMMENT '借用用途',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材借用表';

CREATE TABLE t_equipment_repair (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    report_date DATE COMMENT '报修日期',
    repair_date DATE COMMENT '维修日期',
    complete_date DATE COMMENT '完成日期',
    quantity INT COMMENT '数量',
    description VARCHAR(500) COMMENT '故障描述',
    repair_cost DOUBLE COMMENT '维修费用',
    status VARCHAR(20) COMMENT '状态',
    repair_man VARCHAR(50) COMMENT '维修人',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材维修表';

CREATE TABLE t_equipment_annual_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_id BIGINT COMMENT '器材ID',
    year INT COMMENT '年度',
    total_borrow_count INT COMMENT '总借用次数',
    total_borrow_days INT COMMENT '总借用天数',
    repair_count INT COMMENT '维修次数',
    repair_cost DOUBLE COMMENT '维修总费用',
    utilization_rate DOUBLE COMMENT '利用率',
    report_data TEXT COMMENT '报告数据JSON',
    create_time DATETIME,
    update_time DATETIME,
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_year (year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材年度报告表';

INSERT INTO t_clazz (class_name, grade, head_teacher, student_count) VALUES
('计算机1班', '大一', '张老师', 45),
('计算机2班', '大一', '李老师', 42),
('电子信息1班', '大一', '王老师', 40),
('计算机1班', '大二', '刘老师', 38),
('计算机2班', '大二', '陈老师', 36);

INSERT INTO t_student (student_no, name, gender, age, class_id, is_weak, height, weight) VALUES
('2024001', '张三', '男', 18, 1, 0, 175, 65),
('2024002', '李四', '男', 18, 1, 0, 180, 70),
('2024003', '王五', '女', 18, 1, 0, 165, 55),
('2024004', '赵六', '男', 18, 1, 1, 170, 50),
('2024005', '钱七', '女', 18, 1, 0, 160, 52),
('2024006', '孙八', '男', 18, 2, 0, 178, 72),
('2024007', '周九', '女', 18, 2, 0, 162, 50),
('2024008', '吴十', '男', 18, 2, 0, 172, 68);

INSERT INTO t_equipment (name, code, category, brand, model, total_quantity, available_quantity, borrowed_quantity, repair_quantity, min_stock, status, location) VALUES
('篮球', 'EQ001', '球类', '斯伯丁', '经典款', 50, 45, 5, 0, 10, '正常', '体育馆1楼'),
('足球', 'EQ002', '球类', '阿迪达斯', '训练用球', 30, 28, 2, 0, 5, '正常', '体育馆1楼'),
('羽毛球拍', 'EQ003', '球拍类', '尤尼克斯', '入门款', 40, 35, 5, 0, 8, '正常', '体育馆2楼'),
('乒乓球拍', 'EQ004', '球拍类', '红双喜', '四星', 50, 48, 2, 0, 10, '正常', '体育馆2楼'),
('跑步机', 'EQ005', '健身器材', '舒华', '家用款', 10, 8, 1, 1, 2, '库存不足', '健身房');
