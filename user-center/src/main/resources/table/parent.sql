-- 设置字符集编码
-- set names utf8mb4;

-- 执行脚本文件命令
-- source D:\javaCode\parent\user-center\src\main\resources\table\user.sql

-- 删除数据库
DROP SCHEMA `parent`;

-- 创建数据库，如果不存在的话
CREATE DATABASE IF NOT EXISTS parent DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT ENCRYPTION='N';

-- 使用新建立的数据库
use parent;

CREATE TABLE IF NOT EXISTS user (
  `id` varchar(64) NOT NULL COMMENT '主键id',
  `nick_name` varchar(200) DEFAULT NULL COMMENT '用户昵称',
  `user_name` varchar(200) DEFAULT NULL COMMENT '用户名（真实姓名）',
  `account` varchar(64) DEFAULT NULL COMMENT '账号',
  `pass_word` varchar(64) DEFAULT NULL COMMENT '密码',
  `sex` tinyint DEFAULT '0' COMMENT '性别(0:男，1女)',
  `user_role` varchar(64) DEFAULT '0' COMMENT '用户角色（0正常用户）',
  `education` varchar(100) DEFAULT NULL COMMENT '学历',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号码',
  `identity_card` varchar(64) DEFAULT NULL COMMENT '身份证号码',
  `face` varchar(250) DEFAULT NULL COMMENT '头像',
  `face200` varchar(250) DEFAULT NULL COMMENT '头像 200x200x80',
  `status` tinyint DEFAULT '0' COMMENT '状态（0:在职，1：离职）',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `entry_date` date DEFAULT NULL COMMENT '登记时间',
  `create_time` date NOT NULL COMMENT '创建时间',
  `create_user_id` varchar(64) NOT NULL COMMENT '创建者用户',
  `update_time` date NOT NULL COMMENT '修改时间',
  `update_user_id` varchar(64) NOT NULL COMMENT '修改者用户',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0:删除，1:未删除)',
  `version` tinyint NOT NULL DEFAULT '0' COMMENT '版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO `user` VALUES
('1','上官林','王强','wangqiang','123',0,'0','大专','13267125689','360731199404155494',NULL,NULL,0,'1450988351','2021-05-04','2021-05-10','1','2021-05-10','1',0,0),
('2','金凤','王金凤','wangjinfeng','123',1,'0','大专','13267125689','360731199404155494',NULL,NULL,0,'1450988351','2021-05-04','2021-05-10','1','2021-05-10','1',0,0),
('3','顺兰','王顺兰','wangshunlan','123',0,'0','大专','13267125689','360731199404155494',NULL,NULL,0,'1450988351','2021-05-04','2021-05-10','1','2021-05-10','1',0,0);
