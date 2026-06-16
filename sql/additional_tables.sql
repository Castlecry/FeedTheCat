-- ================================================
-- 补充数据表脚本
-- ================================================

USE weimao;

-- ----------------------------
-- 猫咪档案表
-- ----------------------------
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
    sterilized TINYINT DEFAULT 0 COMMENT '是否已绝育',
    vaccinated TINYINT DEFAULT 0 COMMENT '是否已免疫',
    next_vaccine_date DATE COMMENT '下次免疫日期',
    avatar VARCHAR(255) COMMENT '猫咪照片',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_cat_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪档案表';

-- ----------------------------
-- 服务方资质表
-- ----------------------------
CREATE TABLE IF NOT EXISTS service_provider_credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资质ID',
    user_id BIGINT NOT NULL COMMENT '服务方ID',
    id_card_front VARCHAR(255) NOT NULL COMMENT '身份证正面',
    id_card_back VARCHAR(255) NOT NULL COMMENT '身份证反面',
    criminal_record VARCHAR(255) COMMENT '无犯罪记录证明',
    training_certificate VARCHAR(255) COMMENT '培训证书',
    has_signed_agreement TINYINT DEFAULT 0 COMMENT '是否已签署协议',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核, 1-已通过, 2-已拒绝',
    reject_reason TEXT COMMENT '拒绝原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_credential_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务方资质表';

-- ----------------------------
-- 服务记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS service_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    service_time DATETIME NOT NULL COMMENT '服务时间',
    video_url VARCHAR(255) COMMENT '录像URL',
    lock_photo_url VARCHAR(255) COMMENT '门锁照片URL',
    notes TEXT COMMENT '服务备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_record_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务记录表';

-- ----------------------------
-- 订单评价表
-- ----------------------------
CREATE TABLE IF NOT EXISTS order_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    client_id BIGINT NOT NULL COMMENT '委托方ID',
    service_id BIGINT NOT NULL COMMENT '服务方ID',
    rating TINYINT NOT NULL COMMENT '评分(1-5)',
    comment TEXT COMMENT '评价内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES users(id),
    UNIQUE KEY uk_evaluation_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- ----------------------------
-- 用户积分表
-- ----------------------------
CREATE TABLE IF NOT EXISTS user_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    balance INT NOT NULL DEFAULT 0 COMMENT '积分余额',
    total_earned INT NOT NULL DEFAULT 0 COMMENT '累计获取',
    total_spent INT NOT NULL DEFAULT 0 COMMENT '累计消费',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_points_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分表';

-- ----------------------------
-- 积分记录表
-- ----------------------------
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_points_user_id (user_id),
    INDEX idx_points_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- ----------------------------
-- 社区帖子表
-- ----------------------------
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_post_user_id (user_id),
    INDEX idx_post_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

-- ----------------------------
-- 评论表
-- ----------------------------
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论者ID',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT COMMENT '父评论ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_comment_post_id (post_id),
    INDEX idx_comment_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ----------------------------
-- 点赞表
-- ----------------------------
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '点赞者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_like_post_user (post_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ----------------------------
-- 跟踪回访表
-- ----------------------------
CREATE TABLE IF NOT EXISTS adoption_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '跟踪ID',
    application_id BIGINT NOT NULL COMMENT '领养申请ID',
    tracking_time DATETIME NOT NULL COMMENT '跟踪时间',
    status TINYINT NOT NULL COMMENT '猫咪状态：0-正常, 1-需关注, 2-异常',
    notes TEXT COMMENT '跟踪备注',
    photos TEXT COMMENT '照片URL（JSON数组）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (application_id) REFERENCES adoption_applications(id) ON DELETE CASCADE,
    INDEX idx_tracking_application (application_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跟踪回访表';

-- ----------------------------
-- TNR申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS tnr_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    cat_name VARCHAR(50) COMMENT '猫咪名字',
    location VARCHAR(200) NOT NULL COMMENT '发现地点',
    description TEXT COMMENT '描述',
    photos TEXT COMMENT '照片URL（JSON数组）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核, 1-已通过, 2-已拒绝',
    reject_reason TEXT COMMENT '拒绝原因',
    operation_time DATETIME COMMENT '实施时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_tnr_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TNR申请表';

-- ----------------------------
-- 领养黑名单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS adoption_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    reason TEXT NOT NULL COMMENT '拉黑原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_blacklist_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养黑名单表';

-- ----------------------------
-- AI使用记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS ai_usage_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    ai_type VARCHAR(50) NOT NULL COMMENT 'AI类型：avatar, quote, advice',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ai_user_date (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI使用记录表';

-- ----------------------------
-- 合作医院表
-- ----------------------------
CREATE TABLE IF NOT EXISTS hospitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '医院ID',
    name VARCHAR(100) NOT NULL COMMENT '医院名称',
    address VARCHAR(500) NOT NULL COMMENT '地址',
    phone VARCHAR(20) COMMENT '联系电话',
    business_hours VARCHAR(100) COMMENT '营业时间',
    description TEXT COMMENT '医院介绍',
    photos TEXT COMMENT '照片URL（JSON数组）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用, 1-正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hospital_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合作医院表';

-- ----------------------------
-- 医院优惠政策表
-- ----------------------------
CREATE TABLE IF NOT EXISTS hospital_discounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '优惠ID',
    hospital_id BIGINT NOT NULL COMMENT '医院ID',
    title VARCHAR(100) NOT NULL COMMENT '优惠标题',
    description TEXT COMMENT '优惠描述',
    discount_type TINYINT NOT NULL COMMENT '优惠类型：0-折扣, 1-满减, 2-赠品',
    discount_value DECIMAL(10,2) COMMENT '优惠值',
    min_amount DECIMAL(10,2) COMMENT '最低消费金额',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用, 1-正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE,
    INDEX idx_discount_hospital (hospital_id),
    INDEX idx_discount_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医院优惠政策表';

-- ----------------------------
-- 商品表
-- ----------------------------
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category TINYINT NOT NULL COMMENT '分类：0-医疗服务, 1-宠物用品, 2-食品',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    description TEXT COMMENT '商品描述',
    images TEXT COMMENT '图片URL（JSON数组）',
    stock INT DEFAULT 0 COMMENT '库存',
    sales INT DEFAULT 0 COMMENT '销量',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架, 1-上架',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_category (category),
    INDEX idx_product_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 商城订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS mall_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    points_deduct INT DEFAULT 0 COMMENT '积分抵扣',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待支付, 1-已支付, 2-已发货, 3-已完成, 4-已取消',
    pay_time DATETIME COMMENT '支付时间',
    shipping_address VARCHAR(500) COMMENT '收货地址',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_mall_order_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单表';

-- ----------------------------
-- 商城订单明细表
-- ----------------------------
CREATE TABLE IF NOT EXISTS mall_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    FOREIGN KEY (order_id) REFERENCES mall_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_order_item_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单明细表';

-- ----------------------------
-- 协助任务表
-- ----------------------------
CREATE TABLE IF NOT EXISTS community_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    content TEXT COMMENT '任务描述',
    type TINYINT DEFAULT 0 COMMENT '任务类型：0-临时照顾, 1-寻猫启示, 2-物资求助',
    location VARCHAR(200) COMMENT '地点',
    reward_points INT DEFAULT 0 COMMENT '奖励积分',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待认领, 1-进行中, 2-已完成',
    assignee_id BIGINT COMMENT '承接人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assignee_id) REFERENCES users(id),
    INDEX idx_task_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='协助任务表';

-- ----------------------------
-- 任务认领记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS task_claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    user_id BIGINT NOT NULL COMMENT '认领人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '认领时间',
    FOREIGN KEY (task_id) REFERENCES community_tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_claim_task_user (task_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务认领记录表';

-- ----------------------------
-- 用户行为日志表
-- ----------------------------
CREATE TABLE IF NOT EXISTS user_action_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    action_type VARCHAR(50) NOT NULL COMMENT '行为类型',
    action_detail TEXT COMMENT '行为详情',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_action_user (user_id),
    INDEX idx_action_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为日志表';

-- ----------------------------
-- 签到记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS check_in_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    check_in_date DATE NOT NULL COMMENT '签到日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_checkin_user_date (user_id, check_in_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';

COMMIT;

SELECT '补充数据表创建完成' AS result;