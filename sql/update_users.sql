-- ================================================
-- 用户表更新脚本 - 添加实名认证相关字段
-- ================================================

USE weimao;

-- 检查并添加不存在的列
-- 使用存储过程来安全地添加列
DELIMITER $$

DROP PROCEDURE IF EXISTS add_column_if_not_exists$$

CREATE PROCEDURE add_column_if_not_exists(
    IN tableName VARCHAR(64),
    IN columnName VARCHAR(64),
    IN columnDefinition VARCHAR(255)
)
BEGIN
    DECLARE columnCount INT DEFAULT 0;
    
    SELECT COUNT(*) INTO columnCount
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE table_schema = DATABASE()
      AND table_name = tableName
      AND column_name = columnName;
    
    IF columnCount = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', tableName, ' ADD COLUMN ', columnName, ' ', columnDefinition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

-- 添加实名认证相关字段
CALL add_column_if_not_exists('users', 'real_name', 'VARCHAR(50) COMMENT ''真实姓名''');
CALL add_column_if_not_exists('users', 'face_image', 'VARCHAR(255) COMMENT ''人脸照片''');
CALL add_column_if_not_exists('users', 'auth_status', 'TINYINT DEFAULT 0 COMMENT ''认证状态：0-未认证, 1-已通过, 2-已拒绝''');
CALL add_column_if_not_exists('users', 'auth_reject_reason', 'TEXT COMMENT ''认证拒绝原因''');
CALL add_column_if_not_exists('users', 'auth_time', 'DATETIME COMMENT ''认证时间''');

-- 尝试移除id_card字段的唯一约束（如果存在）
-- 使用存储过程来安全地删除索引
DELIMITER $$

DROP PROCEDURE IF EXISTS drop_index_if_exists$$

CREATE PROCEDURE drop_index_if_exists(
    IN tableName VARCHAR(64),
    IN indexName VARCHAR(64)
)
BEGIN
    DECLARE indexCount INT DEFAULT 0;
    
    SELECT COUNT(*) INTO indexCount
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = tableName
      AND index_name = indexName;
    
    IF indexCount > 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', tableName, ' DROP INDEX ', indexName);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL drop_index_if_exists('users', 'uk_users_id_card');

-- 尝试创建索引（如果不存在）
DELIMITER $$

DROP PROCEDURE IF EXISTS create_index_if_not_exists$$

CREATE PROCEDURE create_index_if_not_exists(
    IN tableName VARCHAR(64),
    IN indexName VARCHAR(64),
    IN columnName VARCHAR(64)
)
BEGIN
    DECLARE indexCount INT DEFAULT 0;
    
    SELECT COUNT(*) INTO indexCount
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE table_schema = DATABASE()
      AND table_name = tableName
      AND index_name = indexName;
    
    IF indexCount = 0 THEN
        SET @sql = CONCAT('CREATE INDEX ', indexName, ' ON ', tableName, '(', columnName, ')');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL create_index_if_not_exists('users', 'idx_users_auth_status', 'auth_status');

DROP PROCEDURE IF EXISTS add_column_if_not_exists;
DROP PROCEDURE IF EXISTS drop_index_if_exists;
DROP PROCEDURE IF EXISTS create_index_if_not_exists;

SELECT '用户表更新完成' AS result;