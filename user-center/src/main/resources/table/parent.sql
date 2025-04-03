-- 设置字符集编码
-- set names utf8mb4;

-- 执行脚本文件命令
-- source D:\code\20241225\parent\user-center\src\main\resources\table\parent.sql

-- 删除数据库
DROP SCHEMA `parent`;

-- 创建数据库，如果不存在的话
CREATE DATABASE IF NOT EXISTS parent DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT ENCRYPTION='N';

-- 使用新建立的数据库
use parent;

DROP TABLE IF EXISTS t_user;
CREATE TABLE IF NOT EXISTS t_user(
  `id` varchar(64) NOT NULL COMMENT '主键id',
  `nick_name` varchar(200) DEFAULT NULL COMMENT '用户昵称',
  `user_name` varchar(200) DEFAULT NULL COMMENT '用户名（真实姓名）',
  `account` varchar(64) NOT NULL COMMENT '账号',
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

  `create_time` varchar(30) DEFAULT NULL COMMENT '创建时间',
  `create_user_id` varchar(64) DEFAULT NULL COMMENT '创建者用户',
  `update_time` varchar(30) DEFAULT NULL COMMENT '修改时间',
  `update_user_id` varchar(64) DEFAULT NULL COMMENT '修改者用户',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0:删除，1:未删除)',
  `version` tinyint NOT NULL DEFAULT '0' COMMENT '版本号',
   PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO `t_user` VALUES
('1','上官林','王强','wangqiang','123',0,'0','大专','13267125689','360731199404155494',NULL,NULL,0,'1450988351','2021-05-04','2021-05-10','1','2021-05-10','1',0,0),
('2','金凤','王金凤','wangjinfeng','123',1,'0','大专','13267125689','360731199404155494',NULL,NULL,0,'1450988351','2021-05-04','2021-05-10','1','2021-05-10','1',0,0),
('3','顺兰','王顺兰','wangshunlan','123',0,'0','大专','13267125689','360731199404155494',NULL,NULL,0,'1450988351','2021-05-04','2021-05-10','1','2021-05-10','1',0,0);

DROP TABLE IF EXISTS t_system_parameter;
CREATE TABLE IF NOT EXISTS t_system_parameter (
  `id` varchar(64) NOT NULL COMMENT '主键id',
  `main_key` varchar(32) NOT NULL COMMENT '主key',
  `sub_key` varchar(32) NOT NULL COMMENT '子key',
  `parm_desc` varchar(500) NOT NULL COMMENT '参数描述',
  `parm_value` varchar(500) NOT NULL COMMENT '参数值',
  `parm_remark` varchar(500) DEFAULT NULL COMMENT '参数备注',
  `module` varchar(16) NOT NULL COMMENT '模块',

  `create_time` varchar(30) DEFAULT NULL COMMENT '创建时间',
  `create_user_id` varchar(64) DEFAULT NULL COMMENT '创建者用户',
  `update_time` varchar(30) DEFAULT NULL COMMENT '修改时间',
  `update_user_id` varchar(64) DEFAULT NULL COMMENT '修改者用户',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0:删除，1:未删除)',
  `version` tinyint NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`main_key`,`sub_key`,`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
INSERT INTO parent.t_system_parameter
(id, main_key, sub_key, parm_desc, parm_value, parm_remark, module, create_time, create_user_id, update_time, update_user_id, deleted, version)
VALUES('3', 'limit_count', '*', 'limit 数量', '2', 'limit 数量', 'common-biz', '-1', '-1', NULL, NULL, 0, 0);
INSERT INTO parent.t_system_parameter
(id, main_key, sub_key, parm_desc, parm_value, parm_remark, module, create_time, create_user_id, update_time, update_user_id, deleted, version)
VALUES('1', 'SEND_SMS_COUNT', '*', '限制短信发送次数', '5', '限制短信发送次数', '001', '-1', '-1', NULL, NULL, 0, 0);
INSERT INTO parent.t_system_parameter
(id, main_key, sub_key, parm_desc, parm_value, parm_remark, module, create_time, create_user_id, update_time, update_user_id, deleted, version)
VALUES('2', 'VERIFICATION_CODE_EXPIRE_DATE', '*', '短信验证码过期时间(单位：秒)', '60', '短信验证码过期时间(单位：秒)', '001', '-1', '-1', NULL, NULL, 0, 0);
