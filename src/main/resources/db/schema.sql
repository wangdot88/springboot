CREATE DATABASE IF NOT EXISTS gym_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gym_management;

-- 门店表
CREATE TABLE IF NOT EXISTS `t_gym` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `gym_name` VARCHAR(100) NOT NULL COMMENT '门店名称',
  `gym_code` VARCHAR(20) NOT NULL UNIQUE COMMENT '门店编码',
  `address` VARCHAR(255) COMMENT '地址',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `manager` VARCHAR(50) COMMENT '店长',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-关闭 1-营业中',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- 会员卡等级表
CREATE TABLE IF NOT EXISTS `t_membership_level` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `level_name` VARCHAR(20) NOT NULL COMMENT '等级名称：铜卡/银卡/金卡/钻石卡',
  `level_code` VARCHAR(20) NOT NULL UNIQUE COMMENT '等级编码',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `duration_days` INT NOT NULL COMMENT '有效期天数',
  `max_courses_per_week` INT DEFAULT 0 COMMENT '每周最大约课数，0不限',
  `can_book_advance_days` INT DEFAULT 7 COMMENT '可提前预约天数',
  `discount_rate` DECIMAL(3,2) DEFAULT 1.00 COMMENT '折扣率',
  `privileges` TEXT COMMENT '权益描述JSON',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡等级表';

-- 会员表
CREATE TABLE IF NOT EXISTS `t_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `member_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '会员编号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT COMMENT '性别：0-女 1-男',
  `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
  `id_card` VARCHAR(18) COMMENT '身份证号',
  `birthday` DATE COMMENT '生日',
  `avatar` VARCHAR(255) COMMENT '头像',
  `email` VARCHAR(100) COMMENT '邮箱',
  `address` VARCHAR(255) COMMENT '住址',
  `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) COMMENT '紧急联系电话',
  `register_gym_id` BIGINT COMMENT '注册门店',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_phone (`phone`),
  INDEX idx_member_no (`member_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- 会员卡表
CREATE TABLE IF NOT EXISTS `t_member_card` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `card_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '卡号',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `level_id` BIGINT NOT NULL COMMENT '卡等级ID',
  `gym_id` BIGINT COMMENT '办卡门店',
  `active_time` DATETIME COMMENT '激活时间',
  `expire_time` DATETIME COMMENT '到期时间',
  `remain_courses` INT DEFAULT 0 COMMENT '剩余课程数',
  `total_courses` INT DEFAULT 0 COMMENT '总课程数',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-未激活 1-正常 2-冻结 3-已过期',
  `freeze_reason` VARCHAR(255) COMMENT '冻结原因',
  `freeze_time` DATETIME COMMENT '冻结时间',
  `unfreeze_time` DATETIME COMMENT '解冻时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_member_id (`member_id`),
  INDEX idx_expire_time (`expire_time`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡表';

-- 冻结审批表
CREATE TABLE IF NOT EXISTS `t_card_freeze_approval` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `card_id` BIGINT NOT NULL COMMENT '会员卡ID',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `apply_reason` VARCHAR(255) COMMENT '申请原因',
  `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `applicant` VARCHAR(50) COMMENT '申请人',
  `approver` VARCHAR(50) COMMENT '审批人',
  `approve_time` DATETIME COMMENT '审批时间',
  `approve_result` TINYINT DEFAULT 0 COMMENT '审批结果：0-待审批 1-通过 2-拒绝',
  `reject_reason` VARCHAR(255) COMMENT '拒绝原因',
  `freeze_days` INT COMMENT '冻结天数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_card_id (`card_id`),
  INDEX idx_approve_result (`approve_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冻结审批表';

-- 教练表
CREATE TABLE IF NOT EXISTS `t_coach` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `coach_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '教练编号',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT COMMENT '性别：0-女 1-男',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `specialty` VARCHAR(255) COMMENT '专长',
  `introduction` TEXT COMMENT '简介',
  `avatar` VARCHAR(255) COMMENT '头像',
  `gym_id` BIGINT COMMENT '所属门店',
  `level` VARCHAR(20) COMMENT '等级',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-离职 1-在职',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练表';

-- 课程类型表
CREATE TABLE IF NOT EXISTS `t_course_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `type_name` VARCHAR(50) NOT NULL COMMENT '类型名称',
  `type_code` VARCHAR(20) NOT NULL UNIQUE COMMENT '类型编码',
  `description` TEXT COMMENT '描述',
  `duration` INT DEFAULT 60 COMMENT '时长(分钟)',
  `calories` INT COMMENT '消耗卡路里',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度：1-初级 2-中级 3-高级',
  `suitable_for` VARCHAR(255) COMMENT '适合人群',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程类型表';

-- 课程表
CREATE TABLE IF NOT EXISTS `t_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `course_type_id` BIGINT NOT NULL COMMENT '课程类型ID',
  `coach_id` BIGINT NOT NULL COMMENT '教练ID',
  `gym_id` BIGINT NOT NULL COMMENT '门店ID',
  `classroom` VARCHAR(50) COMMENT '教室',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `max_capacity` INT DEFAULT 20 COMMENT '最大人数',
  `current_capacity` INT DEFAULT 0 COMMENT '当前人数',
  `min_level_id` BIGINT COMMENT '最低卡等级要求',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-取消 1-正常 2-已满 3-结束',
  `remark` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_gym_time (`gym_id`, `start_time`),
  INDEX idx_coach (`coach_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 预约表
CREATE TABLE IF NOT EXISTS `t_reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `reservation_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '预约编号',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `card_id` BIGINT NOT NULL COMMENT '会员卡ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `reservation_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已取消 1-已预约 2-已签到 3-已完成 4-缺勤',
  `checkin_time` DATETIME COMMENT '签到时间',
  `cancel_time` DATETIME COMMENT '取消时间',
  `cancel_reason` VARCHAR(255) COMMENT '取消原因',
  `from_waiting_list` TINYINT DEFAULT 0 COMMENT '是否来自排队：0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_member_course (`member_id`, `course_id`),
  INDEX idx_member (`member_id`),
  INDEX idx_course (`course_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 排队表
CREATE TABLE IF NOT EXISTS `t_waiting_list` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `queue_position` INT NOT NULL COMMENT '队列位置',
  `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已取消 1-排队中 2-已转预约',
  `convert_time` DATETIME COMMENT '转预约时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_course (`course_id`, `queue_position`),
  INDEX idx_member (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排队表';

-- 签到记录表
CREATE TABLE IF NOT EXISTS `t_checkin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `reservation_id` BIGINT NOT NULL COMMENT '预约ID',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `checkin_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `checkin_type` TINYINT DEFAULT 1 COMMENT '签到方式：1-扫码 2-人脸 3-手动',
  `operator` VARCHAR(50) COMMENT '操作人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_reservation (`reservation_id`),
  INDEX idx_member_time (`member_id`, `checkin_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';

-- 体测数据表
CREATE TABLE IF NOT EXISTS `t_physical_test` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `test_date` DATE NOT NULL COMMENT '测试日期',
  `height` DECIMAL(5,2) COMMENT '身高(cm)',
  `weight` DECIMAL(5,2) COMMENT '体重(kg)',
  `bmi` DECIMAL(4,2) COMMENT 'BMI',
  `body_fat_rate` DECIMAL(4,2) COMMENT '体脂率(%)',
  `muscle_mass` DECIMAL(5,2) COMMENT '肌肉量(kg)',
  `water_rate` DECIMAL(4,2) COMMENT '水分率(%)',
  `bone_density` DECIMAL(4,2) COMMENT '骨密度',
  `basal_metabolism` INT COMMENT '基础代谢',
  `heart_rate` INT COMMENT '心率',
  `blood_pressure_high` INT COMMENT '收缩压',
  `blood_pressure_low` INT COMMENT '舒张压',
  `vital_capacity` INT COMMENT '肺活量',
  `grip_strength` DECIMAL(5,2) COMMENT '握力',
  `sit_reach` DECIMAL(5,2) COMMENT '坐位体前屈(cm)',
  `push_ups` INT COMMENT '俯卧撑',
  `sit_ups` INT COMMENT '仰卧起坐',
  `run_1000m` INT COMMENT '1000米跑(秒)',
  `flexibility_score` INT COMMENT '柔韧性评分',
  `strength_score` INT COMMENT '力量评分',
  `endurance_score` INT COMMENT '耐力评分',
  `overall_score` INT COMMENT '综合评分',
  `tester` VARCHAR(50) COMMENT '测试人员',
  `gym_id` BIGINT COMMENT '测试门店',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_member_date (`member_id`, `test_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测数据表';

-- 体测标准表
CREATE TABLE IF NOT EXISTS `t_physical_standard` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `indicator_name` VARCHAR(50) NOT NULL COMMENT '指标名称',
  `indicator_code` VARCHAR(50) NOT NULL COMMENT '指标编码',
  `gender` TINYINT COMMENT '性别：0-女 1-男 null-通用',
  `age_min` INT COMMENT '最小年龄',
  `age_max` INT COMMENT '最大年龄',
  `value_min` DECIMAL(10,2) COMMENT '最小值',
  `value_max` DECIMAL(10,2) COMMENT '最大值',
  `normal_min` DECIMAL(10,2) COMMENT '正常最小值',
  `normal_max` DECIMAL(10,2) COMMENT '正常最大值',
  `unit` VARCHAR(20) COMMENT '单位',
  `level` VARCHAR(20) COMMENT '等级',
  `description` TEXT COMMENT '说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体测标准表';

-- 训练计划表
CREATE TABLE IF NOT EXISTS `t_training_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `plan_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '计划编号',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `coach_id` BIGINT COMMENT '教练ID',
  `test_id` BIGINT COMMENT '基于的体测ID',
  `plan_name` VARCHAR(100) COMMENT '计划名称',
  `start_date` DATE COMMENT '开始日期',
  `end_date` DATE COMMENT '结束日期',
  `goal` TEXT COMMENT '训练目标',
  `content` TEXT COMMENT '训练内容JSON',
  `frequency` VARCHAR(50) COMMENT '训练频率',
  `difficulty` TINYINT DEFAULT 1 COMMENT '难度：1-初级 2-中级 3-高级',
  `calories_goal` INT COMMENT '目标消耗卡路里',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-停用 1-进行中 2-已完成',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_member (`member_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练计划表';

-- 器材表
CREATE TABLE IF NOT EXISTS `t_equipment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `equipment_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '器材编号',
  `equipment_name` VARCHAR(100) NOT NULL COMMENT '器材名称',
  `category` VARCHAR(50) COMMENT '分类',
  `brand` VARCHAR(50) COMMENT '品牌',
  `model` VARCHAR(50) COMMENT '型号',
  `gym_id` BIGINT NOT NULL COMMENT '所属门店',
  `location` VARCHAR(100) COMMENT '位置',
  `purchase_date` DATE COMMENT '购入日期',
  `warranty_expire` DATE COMMENT '保修到期日',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-报废 1-正常 2-维修中',
  `maintenance_cycle` INT COMMENT '保养周期(天)',
  `last_maintenance` DATE COMMENT '上次保养日期',
  `next_maintenance` DATE COMMENT '下次保养日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_gym (`gym_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材表';

-- 器材使用记录表
CREATE TABLE IF NOT EXISTS `t_equipment_usage` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `equipment_id` BIGINT NOT NULL COMMENT '器材ID',
  `member_id` BIGINT COMMENT '会员ID',
  `gym_id` BIGINT NOT NULL COMMENT '门店ID',
  `start_time` DATETIME COMMENT '开始使用时间',
  `end_time` DATETIME COMMENT '结束使用时间',
  `duration_minutes` INT COMMENT '使用时长(分钟)',
  `calories_burned` INT COMMENT '消耗卡路里',
  `data_json` TEXT COMMENT '运动数据JSON',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_equipment (`equipment_id`, `start_time`),
  INDEX idx_member (`member_id`, `start_time`),
  INDEX idx_gym (`gym_id`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材使用记录表';

-- 通知表
CREATE TABLE IF NOT EXISTS `t_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `notification_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '通知编号',
  `member_id` BIGINT COMMENT '接收会员ID，null表示全员',
  `gym_id` BIGINT COMMENT '所属门店',
  `type` VARCHAR(50) NOT NULL COMMENT '通知类型：CARD_EXPIRE-卡到期提醒、COURSE_REMIND-课程提醒、ABNORMAL-异常提醒',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `send_time` DATETIME COMMENT '发送时间',
  `read_time` DATETIME COMMENT '阅读时间',
  `read_status` TINYINT DEFAULT 0 COMMENT '阅读状态：0-未读 1-已读',
  `push_status` TINYINT DEFAULT 0 COMMENT '推送状态：0-未推送 1-已推送 2-推送失败',
  `push_fail_reason` VARCHAR(255) COMMENT '推送失败原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_member (`member_id`, `read_status`),
  INDEX idx_type (`type`),
  INDEX idx_send_time (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 数据统计表
CREATE TABLE IF NOT EXISTS `t_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `gym_id` BIGINT COMMENT '门店ID，null表示全连锁',
  `type` VARCHAR(50) NOT NULL COMMENT '统计类型',
  `key1` VARCHAR(100) COMMENT '统计维度1',
  `key2` VARCHAR(100) COMMENT '统计维度2',
  `value1` DECIMAL(20,2) COMMENT '统计值1',
  `value2` DECIMAL(20,2) COMMENT '统计值2',
  `value3` DECIMAL(20,2) COMMENT '统计值3',
  `extra_data` TEXT COMMENT '额外数据JSON',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_stat (`stat_date`, `gym_id`, `type`, `key1`, `key2`),
  INDEX idx_type (`type`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据统计表';

-- 续费预测表
CREATE TABLE IF NOT EXISTS `t_renewal_prediction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `card_id` BIGINT NOT NULL COMMENT '会员卡ID',
  `probability` DECIMAL(5,4) COMMENT '续费概率',
  `prediction_model` VARCHAR(50) COMMENT '预测模型',
  `features` TEXT COMMENT '特征值JSON',
  `recommend_level_id` BIGINT COMMENT '推荐卡等级',
  `recommend_reason` VARCHAR(255) COMMENT '推荐原因',
  `prediction_date` DATE COMMENT '预测日期',
  `actual_renewal` TINYINT COMMENT '实际是否续费：0-否 1-是 null-未知',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_member (`member_id`),
  INDEX idx_probability (`probability`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='续费预测表';
