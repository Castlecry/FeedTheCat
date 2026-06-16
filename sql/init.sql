-- 创建数据库
CREATE DATABASE IF NOT EXISTS weimao 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE weimao;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    id_card VARCHAR(18) UNIQUE COMMENT '身份证号(加密存储)',
    avatar VARCHAR(255) COMMENT '头像URL',
    address VARCHAR(500) COMMENT '常用地址',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '用户角色：0-普通用户, 1-委托方, 2-服务方, 3-送养人, 4-领养人',
    credit_score INT DEFAULT 100 COMMENT '信用分',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 猫咪档案表
CREATE TABLE IF NOT EXISTS cat_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '猫咪ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    name VARCHAR(50) NOT NULL COMMENT '猫咪名字',
    breed VARCHAR(50) COMMENT '品种',
    age VARCHAR(20) COMMENT '年龄',
    gender TINYINT COMMENT '性别：0-公, 1-母',
    health_status TEXT COMMENT '健康状况',
    dietary_habits TEXT COMMENT '饮食习惯',
    taboos TEXT COMMENT '禁忌事项',
    sterilized TINYINT DEFAULT 0 COMMENT '是否绝育：0-否, 1-是',
    vaccinated TINYINT DEFAULT 0 COMMENT '是否免疫：0-否, 1-是',
    avatar VARCHAR(255) COMMENT '猫咪照片',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_cat_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪档案表';

-- 委托订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单编号',
    client_id BIGINT NOT NULL COMMENT '委托方ID',
    service_id BIGINT COMMENT '服务方ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待接单, 1-已接单, 2-服务中, 3-已完成, 4-已取消',
    start_time DATETIME NOT NULL COMMENT '服务开始时间',
    end_time DATETIME NOT NULL COMMENT '服务结束时间',
    visit_frequency TINYINT NOT NULL COMMENT '上门频次（次/天）',
    feeding_requirements TEXT COMMENT '喂养要求',
    litter_clean_standard VARCHAR(200) COMMENT '猫砂清理标准',
    special_care TEXT COMMENT '特殊照料需求',
    entry_method TINYINT NOT NULL COMMENT '入户方式：0-密码, 1-钥匙寄存',
    key_storage_info VARCHAR(500) COMMENT '钥匙寄存信息',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    address VARCHAR(500) NOT NULL COMMENT '服务地址',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    commission_rate DECIMAL(5,2) DEFAULT 0.1 COMMENT '平台佣金比例',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_client_id (client_id),
    INDEX idx_service_id (service_id),
    INDEX idx_status (status),
    CONSTRAINT fk_order_client FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_service FOREIGN KEY (service_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托订单表';

-- 服务记录表
CREATE TABLE IF NOT EXISTS service_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    service_time DATETIME NOT NULL COMMENT '服务时间',
    video_url VARCHAR(255) NOT NULL COMMENT '录像URL',
    lock_photo_url VARCHAR(255) COMMENT '门锁照片URL',
    notes TEXT COMMENT '服务备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    CONSTRAINT fk_record_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务记录表';

-- 流浪猫信息表
CREATE TABLE IF NOT EXISTS stray_cats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '猫咪ID',
    feeder_id BIGINT NOT NULL COMMENT '送养人ID',
    name VARCHAR(50) COMMENT '猫咪名字',
    breed VARCHAR(50) COMMENT '品种',
    age VARCHAR(20) COMMENT '年龄',
    gender TINYINT COMMENT '性别：0-公, 1-母',
    health_status TEXT NOT NULL COMMENT '健康状况',
    sterilized TINYINT DEFAULT 0 COMMENT '是否绝育：0-否, 1-是',
    vaccinated TINYINT DEFAULT 0 COMMENT '是否免疫：0-否, 1-是',
    location VARCHAR(200) COMMENT '发现地点',
    description TEXT COMMENT '描述',
    photos TEXT COMMENT '照片URL（JSON数组）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核, 1-待领养, 2-已领养',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_feeder_id (feeder_id),
    INDEX idx_status (status),
    CONSTRAINT fk_stray_feeder FOREIGN KEY (feeder_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流浪猫信息表';

-- 领养申请表
CREATE TABLE IF NOT EXISTS adoption_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    cat_id BIGINT NOT NULL COMMENT '流浪猫ID',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    living_address VARCHAR(500) NOT NULL COMMENT '居住地址',
    housing_type TINYINT NOT NULL COMMENT '住房类型：0-合租, 1-独立住房',
    family_agree TINYINT NOT NULL COMMENT '家人是否同意：0-否, 1-是',
    pet_experience TEXT COMMENT '养宠经验',
    has_abandoned TINYINT DEFAULT 0 COMMENT '是否有弃养经历：0-否, 1-是',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待初审, 1-待复审, 2-已通过, 3-已拒绝',
    platform_note TEXT COMMENT '平台审核备注',
    feeder_note TEXT COMMENT '送养人审核备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_cat_id (cat_id),
    INDEX idx_applicant_id (applicant_id),
    INDEX idx_status (status),
    CONSTRAINT fk_adopt_cat FOREIGN KEY (cat_id) REFERENCES stray_cats(id) ON DELETE CASCADE,
    CONSTRAINT fk_adopt_applicant FOREIGN KEY (applicant_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS points_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type TINYINT NOT NULL COMMENT '类型：0-获取, 1-消费',
    source VARCHAR(50) NOT NULL COMMENT '来源：order_publish, service_complete等',
    amount INT NOT NULL COMMENT '积分数量',
    balance_before INT NOT NULL COMMENT '变动前余额',
    balance_after INT NOT NULL COMMENT '变动后余额',
    related_id BIGINT COMMENT '关联业务ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    CONSTRAINT fk_points_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 用户积分余额表
CREATE TABLE IF NOT EXISTS user_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT UNIQUE NOT NULL COMMENT '用户ID',
    balance INT DEFAULT 0 COMMENT '积分余额',
    total_earned INT NOT NULL DEFAULT 0 COMMENT '累计获取',
    total_spent INT NOT NULL DEFAULT 0 COMMENT '累计消费',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_user_points FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分余额表';

-- 社区帖子表
CREATE TABLE IF NOT EXISTS community_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    images TEXT COMMENT '图片URL（JSON数组）',
    type TINYINT DEFAULT 0 COMMENT '类型：0-日常分享, 1-求助, 2-闲置转让',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核, 1-已发布, 2-已删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论者ID',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT COMMENT '父评论ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-删除, 1-正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 点赞表
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '点赞者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 服务方资质表
CREATE TABLE IF NOT EXISTS service_provider_credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资质ID',
    user_id BIGINT UNIQUE NOT NULL COMMENT '服务方ID',
    id_card_front VARCHAR(255) NOT NULL COMMENT '身份证正面',
    id_card_back VARCHAR(255) NOT NULL COMMENT '身份证反面',
    criminal_record VARCHAR(255) COMMENT '无犯罪记录证明',
    training_certificate VARCHAR(255) COMMENT '培训证书',
    has_signed_agreement TINYINT DEFAULT 0 COMMENT '是否签署协议：0-否, 1-是',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核, 1-已通过, 2-已拒绝',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_credentials_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务方资质表';

-- 领养黑名单表
CREATE TABLE IF NOT EXISTS adoption_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT UNIQUE NOT NULL COMMENT '用户ID',
    reason TEXT COMMENT '列入原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_blacklist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养黑名单表';

-- TNR申请表
CREATE TABLE IF NOT EXISTS tnr_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    hospital_id BIGINT COMMENT '合作医院ID',
    plan_capture_time DATETIME NOT NULL COMMENT '计划抓捕时间',
    cat_count INT DEFAULT 1 COMMENT '猫咪数量',
    location VARCHAR(200) COMMENT '抓捕地点',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核, 1-已通过, 2-已完成, 3-已拒绝',
    surgery_photos TEXT COMMENT '手术照片（JSON数组）',
    release_photos TEXT COMMENT '放归照片（JSON数组）',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    CONSTRAINT fk_tnr_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TNR申请表';

-- 合作医院表
CREATE TABLE IF NOT EXISTS partner_hospitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '医院ID',
    name VARCHAR(100) NOT NULL COMMENT '医院名称',
    address VARCHAR(500) NOT NULL COMMENT '地址',
    phone VARCHAR(20) COMMENT '联系电话',
    description TEXT COMMENT '医院介绍',
    discount_policy TEXT COMMENT '优惠政策',
    photos TEXT COMMENT '医院照片（JSON数组）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用, 1-正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合作医院表';

-- 商城商品表
CREATE TABLE IF NOT EXISTS mall_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    category TINYINT DEFAULT 0 COMMENT '分类：0-医疗服务, 1-宠物用品, 2-食品',
    images TEXT COMMENT '商品图片（JSON数组）',
    stock INT DEFAULT 0 COMMENT '库存',
    sales INT DEFAULT 0 COMMENT '销量',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架, 1-上架',
    hospital_id BIGINT COMMENT '关联医院ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status),
    CONSTRAINT fk_product_hospital FOREIGN KEY (hospital_id) REFERENCES partner_hospitals(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城商品表';

-- 商城订单表
CREATE TABLE IF NOT EXISTS mall_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待支付, 1-已支付, 2-已完成, 3-已取消',
    points_deducted INT DEFAULT 0 COMMENT '积分抵扣',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    CONSTRAINT fk_mall_order_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_mall_order_product FOREIGN KEY (product_id) REFERENCES mall_products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单表';

-- 领养跟踪记录表
CREATE TABLE IF NOT EXISTS adoption_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    application_id BIGINT NOT NULL COMMENT '领养申请ID',
    tracking_type TINYINT NOT NULL COMMENT '跟踪类型：0-线上打卡, 1-线下回访',
    content TEXT COMMENT '跟踪内容',
    photos TEXT COMMENT '照片（JSON数组）',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_application_id (application_id),
    CONSTRAINT fk_tracking_application FOREIGN KEY (application_id) REFERENCES adoption_applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养跟踪记录表';

-- 插入初始数据
INSERT INTO partner_hospitals (name, address, phone, description, discount_policy) VALUES
('阳光宠物医院', '北京市朝阳区建国路88号', '010-12345678', '专业宠物医疗服务机构，提供疫苗接种、绝育手术、疾病治疗等服务', '领养猫咪享受疫苗8折优惠'),
('爱心动物诊所', '北京市海淀区中关村大街1号', '010-87654321', '24小时急诊服务，专业医疗团队', '流浪猫绝育免费，医疗费用9折');

-- 创建存储过程：生成订单编号
DELIMITER $$
CREATE PROCEDURE GenerateOrderNo(OUT orderNo VARCHAR(32))
BEGIN
    SET orderNo = CONCAT('MO', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(FLOOR(RAND() * 10000), 4, '0'));
END$$
DELIMITER ;

-- 创建触发器：用户注册时自动创建积分余额记录
DELIMITER $$
CREATE TRIGGER AfterUserInsert AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO user_points (user_id, balance) VALUES (NEW.id, 0);
END$$
DELIMITER ;

-- 创建视图：用户完整信息
CREATE VIEW user_full_info AS
SELECT 
    u.id, u.phone, u.name, u.avatar, u.address, u.role, u.credit_score, u.status,
    up.balance AS points_balance
FROM users u
LEFT JOIN user_points up ON u.id = up.user_id;

-- 创建索引：订单状态+创建时间
CREATE INDEX idx_order_status_created ON orders(status, created_at);

-- 创建索引：流浪猫状态+创建时间
CREATE INDEX idx_stray_status_created ON stray_cats(status, created_at);

-- 创建索引：领养申请状态+创建时间
CREATE INDEX idx_adopt_status_created ON adoption_applications(status, created_at);

COMMIT;

-- 输出建表完成信息
SELECT '数据库初始化完成' AS result;
