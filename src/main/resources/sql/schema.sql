-- 智能健身房系统数据库设计

-- 1. 连锁店表
CREATE TABLE IF NOT EXISTS `gym_branch` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '连锁店ID',
    `name` VARCHAR(100) NOT NULL COMMENT '连锁店名称',
    `address` VARCHAR(255) COMMENT '地址',
    `phone` VARCHAR(20) COMMENT '电话',
    `manager_name` VARCHAR(50) COMMENT '店长姓名',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-关闭, 1-营业中',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='连锁店表';

-- 2. 会员卡等级表
CREATE TABLE IF NOT EXISTS `membership_level` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '等级ID',
    `level_code` VARCHAR(20) NOT NULL UNIQUE COMMENT '等级代码: BRONZE/SILVER/GOLD/DIAMOND',
    `level_name` VARCHAR(50) NOT NULL COMMENT '等级名称: 铜/银/金/钻卡',
    `discount` DECIMAL(3,2) DEFAULT 1.00 COMMENT '课程折扣',
    `free_classes_per_month` INT DEFAULT 0 COMMENT '每月免费课程次数',
    `max_courses_per_week` INT DEFAULT 10 COMMENT '每周最大约课次数',
    `can_freeze` TINYINT DEFAULT 1 COMMENT '是否可冻结',
    `freeze_duration` INT DEFAULT 30 COMMENT '最大冻结天数',
    `equipment_priority` TINYINT DEFAULT 1 COMMENT '器材使用优先级',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_level_code` (`level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡等级表';

-- 初始化会员卡等级数据
INSERT INTO `membership_level` (`level_code`, `level_name`, `discount`, `free_classes_per_month`, `max_courses_per_week`, `can_freeze`, `freeze_duration`, `equipment_priority`) VALUES
('BRONZE', '铜卡', 1.00, 0, 4, 1, 15, 4),
('SILVER', '银卡', 0.90, 2, 6, 1, 30, 3),
('GOLD', '金卡', 0.80, 4, 8, 1, 60, 2),
('DIAMOND', '钻卡', 0.70, 8, 10, 1, 90, 1);

-- 3. 会员表
CREATE TABLE IF NOT EXISTS `member` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会员ID',
    `member_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '会员编号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `gender` TINYINT COMMENT '性别: 0-女, 1-男',
    `birth_date` DATE COMMENT '出生日期',
    `avatar_url` VARCHAR(255) COMMENT '头像URL',
    `level_code` VARCHAR(20) DEFAULT 'BRONZE' COMMENT '会员卡等级',
    `branch_id` BIGINT COMMENT '所属连锁店',
    `card_start_date` DATE NOT NULL COMMENT '卡开始日期',
    `card_end_date` DATE NOT NULL COMMENT '卡结束日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已过期, 1-正常, 2-冻结中',
    `total_consumption` DECIMAL(10,2) DEFAULT 0 COMMENT '累计消费',
    `remaining_free_classes` INT DEFAULT 0 COMMENT '剩余免费课程次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_member_no` (`member_no`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_level_code` (`level_code`),
    INDEX `idx_status` (`status`),
    INDEX `idx_card_end_date` (`card_end_date`),
    FOREIGN KEY (`level_code`) REFERENCES `membership_level`(`level_code`),
    FOREIGN KEY (`branch_id`) REFERENCES `gym_branch`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- 4. 会员冻结申请表
CREATE TABLE IF NOT EXISTS `member_freeze_application` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `reason` VARCHAR(500) COMMENT '冻结原因',
    `request_days` INT NOT NULL COMMENT '申请冻结天数',
    `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `approver_id` BIGINT COMMENT '审批人ID(店长)',
    `approve_time` DATETIME COMMENT '审批时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审批, 1-已通过, 2-已拒绝',
    `remark` VARCHAR(200) COMMENT '审批备注',
    INDEX `idx_member_id` (`member_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`member_id`) REFERENCES `member`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员冻结申请表';

-- 5. 课程类别表
CREATE TABLE IF NOT EXISTS `course_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '类别ID',
    `category_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '类别代码',
    `category_name` VARCHAR(100) NOT NULL COMMENT '类别名称',
    `description` VARCHAR(500) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_category_code` (`category_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程类别表';

-- 6. 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    `course_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '课程代码',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `category_code` VARCHAR(50) NOT NULL COMMENT '课程类别',
    `description` TEXT COMMENT '课程描述',
    `duration` INT NOT NULL COMMENT '课程时长(分钟)',
    `max_participants` INT NOT NULL COMMENT '最大参与人数',
    `price` DECIMAL(8,2) NOT NULL COMMENT '课程价格',
    `difficulty_level` TINYINT DEFAULT 1 COMMENT '难度等级: 1-初级, 2-中级, 3-高级',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已下架, 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course_code` (`course_code`),
    INDEX `idx_category_code` (`category_code`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`category_code`) REFERENCES `course_category`(`category_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 7. 课程排期表
CREATE TABLE IF NOT EXISTS `course_schedule` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '排期ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `branch_id` BIGINT NOT NULL COMMENT '连锁店ID',
    `coach_id` BIGINT COMMENT '教练ID',
    `schedule_date` DATE NOT NULL COMMENT '排期日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `current_participants` INT DEFAULT 0 COMMENT '当前报名人数',
    `max_participants` INT NOT NULL COMMENT '最大人数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已取消, 1-正常, 2-已完成',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_course_id` (`course_id`),
    INDEX `idx_branch_id` (`branch_id`),
    INDEX `idx_schedule_date` (`schedule_date`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`branch_id`) REFERENCES `gym_branch`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程排期表';

-- 8. 会员约课表
CREATE TABLE IF NOT EXISTS `member_course_enrollment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报名ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `schedule_id` BIGINT NOT NULL COMMENT '排期ID',
    `enroll_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-已报名, 1-已签到, 2-已取消, 3-已缺席',
    `queue_position` INT DEFAULT 0 COMMENT '排队位置(0表示已成功报名)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_member_schedule` (`member_id`, `schedule_id`),
    INDEX `idx_member_id` (`member_id`),
    INDEX `idx_schedule_id` (`schedule_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`member_id`) REFERENCES `member`(`id`),
    FOREIGN KEY (`schedule_id`) REFERENCES `course_schedule`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员约课表';

-- 9. 体测指标表
CREATE TABLE IF NOT EXISTS `fitness_metric` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '指标ID',
    `metric_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '指标代码',
    `metric_name` VARCHAR(100) NOT NULL COMMENT '指标名称',
    `unit` VARCHAR(20) COMMENT '单位',
    `normal_min_male` DECIMAL(10,2) COMMENT '男性正常最小值',
    `normal_max_male` DECIMAL(10,2) COMMENT '男性正常最大值',
    `normal_min_female` DECIMAL(10,2) COMMENT '女性正常最小值',
    `normal_max_female` DECIMAL(10,2) COMMENT '女性正常最大值',
    `warning_threshold` DECIMAL(3,2) DEFAULT 0.10 COMMENT '异常预警阈值',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_metric_code` (`metric_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测指标表';

-- 初始化体测指标数据
INSERT INTO `fitness_metric` (`metric_code`, `metric_name`, `unit`, `normal_min_male`, `normal_max_male`, `normal_min_female`, `normal_max_female`, `warning_threshold`) VALUES
('HEIGHT', '身高', 'cm', 160, 190, 150, 180, 0.00),
('WEIGHT', '体重', 'kg', 55, 90, 45, 75, 0.10),
('BMI', 'BMI指数', '', 18.5, 24, 18.5, 23, 0.10),
('BODY_FAT', '体脂率', '%', 10, 20, 18, 28, 0.15),
('MUSCLE_MASS', '肌肉量', 'kg', 40, 65, 30, 50, 0.10),
('BMR', '基础代谢率', 'kcal', 1500, 2000, 1200, 1600, 0.10),
('VISFAT', '内脏脂肪等级', '', 1, 9, 1, 9, 0.00),
('BONE_MASS', '骨量', 'kg', 2.5, 4.5, 2, 4, 0.10);

-- 10. 体测记录表
CREATE TABLE IF NOT EXISTS `fitness_test_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `test_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '体测时间',
    `branch_id` BIGINT COMMENT '体测门店',
    `tester_id` BIGINT COMMENT '测试人员ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_member_id` (`member_id`),
    INDEX `idx_test_date` (`test_date`),
    FOREIGN KEY (`member_id`) REFERENCES `member`(`id`),
    FOREIGN KEY (`branch_id`) REFERENCES `gym_branch`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测记录表';

-- 11. 体测详情表
CREATE TABLE IF NOT EXISTS `fitness_test_detail` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    `record_id` BIGINT NOT NULL COMMENT '记录ID',
    `metric_code` VARCHAR(50) NOT NULL COMMENT '指标代码',
    `metric_value` DECIMAL(10,2) NOT NULL COMMENT '指标数值',
    `is_abnormal` TINYINT DEFAULT 0 COMMENT '是否异常: 0-正常, 1-异常',
    `suggestion` VARCHAR(500) COMMENT '建议',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_record_id` (`record_id`),
    INDEX `idx_metric_code` (`metric_code`),
    FOREIGN KEY (`record_id`) REFERENCES `fitness_test_record`(`id`),
    FOREIGN KEY (`metric_code`) REFERENCES `fitness_metric`(`metric_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测详情表';

-- 12. 器材表
CREATE TABLE IF NOT EXISTS `equipment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '器材ID',
    `equipment_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '器材编号',
    `equipment_name` VARCHAR(100) NOT NULL COMMENT '器材名称',
    `branch_id` BIGINT NOT NULL COMMENT '所属门店',
    `type` VARCHAR(50) COMMENT '器材类型',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-故障, 1-正常, 2-维护中',
    `purchase_date` DATE COMMENT '购买日期',
    `last_maintain_date` DATE COMMENT '上次维护日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_branch_id` (`branch_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`branch_id`) REFERENCES `gym_branch`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材表';

-- 13. 器材使用记录表
CREATE TABLE IF NOT EXISTS `equipment_usage_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `equipment_id` BIGINT NOT NULL COMMENT '器材ID',
    `member_id` BIGINT COMMENT '使用会员',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `duration` INT COMMENT '使用时长(分钟)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_equipment_id` (`equipment_id`),
    INDEX `idx_member_id` (`member_id`),
    INDEX `idx_start_time` (`start_time`),
    FOREIGN KEY (`equipment_id`) REFERENCES `equipment`(`id`),
    FOREIGN KEY (`member_id`) REFERENCES `member`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材使用记录表';

-- 14. 训练计划表
CREATE TABLE IF NOT EXISTS `training_plan` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `plan_name` VARCHAR(100) NOT NULL COMMENT '计划名称',
    `description` TEXT COMMENT '计划描述',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已结束, 1-进行中',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_member_id` (`member_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`member_id`) REFERENCES `member`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练计划表';

-- 15. 训练计划详情表
CREATE TABLE IF NOT EXISTS `training_plan_detail` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '详情ID',
    `plan_id` BIGINT NOT NULL COMMENT '计划ID',
    `day_of_week` TINYINT NOT NULL COMMENT '周几: 1-周一, ..., 7-周日',
    `exercise_name` VARCHAR(100) NOT NULL COMMENT '训练项目',
    `sets` INT COMMENT '组数',
    `reps` VARCHAR(50) COMMENT '次数/时间',
    `weight` DECIMAL(8,2) COMMENT '重量(kg)',
    `equipment_name` VARCHAR(100) COMMENT '使用器材',
    `remark` VARCHAR(200) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_plan_id` (`plan_id`),
    FOREIGN KEY (`plan_id`) REFERENCES `training_plan`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练计划详情表';

-- 16. 消息提醒表
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `type` VARCHAR(50) COMMENT '消息类型: CARD_EXPIRE/CLASS_REMINDER/METRIC_ABNORMAL等',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
    `send_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_member_id` (`member_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_send_time` (`send_time`),
    FOREIGN KEY (`member_id`) REFERENCES `member`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息提醒表';