/*
 Navicat Premium Data Transfer

 Source Server         : 172.16.60.98-测试LOT
 Source Server Type    : PostgreSQL
 Source Server Version : 120008
 Source Host           : 172.16.60.98:5432
 Source Catalog        : zeus-iot
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 120008
 File Encoding         : 65001

 Date: 23/09/2021 11:43:08
*/


-- ----------------------------
-- Sequence structure for device_online_report_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."device_online_report_id_seq";
CREATE SEQUENCE "public"."device_online_report_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for devices_groups_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."devices_groups_id_seq";
CREATE SEQUENCE "public"."devices_groups_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for media_type_setting_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."media_type_setting_id_seq";
CREATE SEQUENCE "public"."media_type_setting_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for messages_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."messages_id_seq";
CREATE SEQUENCE "public"."messages_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_event_relation_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."product_event_relation_id_seq";
CREATE SEQUENCE "public"."product_event_relation_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_event_tags_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."product_event_tags_id_seq";
CREATE SEQUENCE "public"."product_event_tags_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_service_relation_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."product_service_relation_id_seq";
CREATE SEQUENCE "public"."product_service_relation_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_status_function_relation_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."product_status_function_relation_id_seq";
CREATE SEQUENCE "public"."product_status_function_relation_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_type_product_type_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."product_type_product_type_id_seq";
CREATE SEQUENCE "public"."product_type_product_type_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for service_execute_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."service_execute_record_id_seq";
CREATE SEQUENCE "public"."service_execute_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for service_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."service_id_seq";
CREATE SEQUENCE "public"."service_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for service_param_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."service_param_id_seq";
CREATE SEQUENCE "public"."service_param_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_user_group_user_group_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_user_group_user_group_id_seq";
CREATE SEQUENCE "public"."sys_user_group_user_group_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_user_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_user_user_id_seq";
CREATE SEQUENCE "public"."sys_user_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tag_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tag_id_seq";
CREATE SEQUENCE "public"."tag_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS "public"."device";
CREATE TABLE "public"."device" (
  "device_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "product_id" int8,
  "status" varchar(16) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "create_user" int8,
  "update_time" timestamp(6),
  "update_user" int8,
  "type" varchar(16) COLLATE "pg_catalog"."default",
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default",
  "addr" varchar(255) COLLATE "pg_catalog"."default",
  "position" varchar(64) COLLATE "pg_catalog"."default",
  "online" int2 DEFAULT 1
)
;
COMMENT ON COLUMN "public"."device"."device_id" IS '设备ID';
COMMENT ON COLUMN "public"."device"."name" IS '设备名称';
COMMENT ON COLUMN "public"."device"."product_id" IS '关联产品';
COMMENT ON COLUMN "public"."device"."status" IS '状态';
COMMENT ON COLUMN "public"."device"."remark" IS '备注';
COMMENT ON COLUMN "public"."device"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."device"."create_user" IS '创建者';
COMMENT ON COLUMN "public"."device"."update_time" IS '系统时间';
COMMENT ON COLUMN "public"."device"."update_user" IS '修改者';
COMMENT ON COLUMN "public"."device"."type" IS '设备类型';
COMMENT ON COLUMN "public"."device"."zbx_id" IS 'zbx主机ID';
COMMENT ON COLUMN "public"."device"."addr" IS '设备地址';
COMMENT ON COLUMN "public"."device"."position" IS '地址坐标';
COMMENT ON COLUMN "public"."device"."online" IS '是否在线 1是 0否';

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO "public"."device" VALUES ('101', '温度传感器', 1440880212866494464, 'ENABLE', '', '2021-09-23 11:27:40.882', 1, '2021-09-23 11:27:41.038', 1, '1', '10590', NULL, NULL, 1);

-- ----------------------------
-- Table structure for device_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."device_group";
CREATE TABLE "public"."device_group" (
  "device_group_id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "create_user" int8,
  "create_time" timestamp(6),
  "update_user" int8,
  "update_time" timestamp(6)
)
;
COMMENT ON COLUMN "public"."device_group"."device_group_id" IS '主机组ID';
COMMENT ON COLUMN "public"."device_group"."name" IS '主机组名称';
COMMENT ON COLUMN "public"."device_group"."remark" IS '备注';
COMMENT ON COLUMN "public"."device_group"."zbx_id" IS 'zabbix 主机组ID';
COMMENT ON COLUMN "public"."device_group"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."device_group"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."device_group"."update_user" IS '更新人';
COMMENT ON COLUMN "public"."device_group"."update_time" IS '更新时间';

-- ----------------------------
-- Records of device_group
-- ----------------------------
INSERT INTO "public"."device_group" VALUES (1440880403610857472, '我的设备组', '', '55', 1, '2021-09-23 11:27:13.22', 1, '2021-09-23 11:27:13.22');

-- ----------------------------
-- Table structure for device_online_report
-- ----------------------------
DROP TABLE IF EXISTS "public"."device_online_report";
CREATE TABLE "public"."device_online_report" (
  "id" int8 NOT NULL DEFAULT nextval('device_online_report_id_seq'::regclass),
  "create_time" varchar(10) COLLATE "pg_catalog"."default",
  "online" int4,
  "offline" int4,
  "type" int2
)
;
COMMENT ON COLUMN "public"."device_online_report"."online" IS '在线数';
COMMENT ON COLUMN "public"."device_online_report"."offline" IS '离线数';
COMMENT ON COLUMN "public"."device_online_report"."type" IS '设备类型';

-- ----------------------------
-- Records of device_online_report
-- ----------------------------

-- ----------------------------
-- Table structure for devices_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."devices_groups";
CREATE TABLE "public"."devices_groups" (
  "id" int8 NOT NULL DEFAULT nextval('devices_groups_id_seq'::regclass),
  "device_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "device_group_id" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."devices_groups"."id" IS 'ID';
COMMENT ON COLUMN "public"."devices_groups"."device_id" IS '设备ID';
COMMENT ON COLUMN "public"."devices_groups"."device_group_id" IS '设备组ID';

-- ----------------------------
-- Records of devices_groups
-- ----------------------------
INSERT INTO "public"."devices_groups" VALUES (170, '101', 1440880403610857472);

-- ----------------------------
-- Table structure for media_type_setting
-- ----------------------------
DROP TABLE IF EXISTS "public"."media_type_setting";
CREATE TABLE "public"."media_type_setting" (
  "id" int4 NOT NULL DEFAULT nextval('media_type_setting_id_seq'::regclass),
  "type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "template" text COLLATE "pg_catalog"."default",
  "webhooks" text COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."media_type_setting"."type" IS '类型';
COMMENT ON COLUMN "public"."media_type_setting"."template" IS '消息模板';
COMMENT ON COLUMN "public"."media_type_setting"."webhooks" IS '推送配置';

-- ----------------------------
-- Records of media_type_setting
-- ----------------------------
INSERT INTO "public"."media_type_setting" VALUES (3, 'feishu', '{
    "msg_type":"text",
    "content": {
        "text":"%s"
    }
}', '{"url":"https://open.feishu.cn/open-apis/bot/v2/hook/c43e56f3-bdc9-4abb-ba4c-17afc6147f73","secret":"xoaP9dPKLeQIeDk5jZnEdg"}');
INSERT INTO "public"."media_type_setting" VALUES (1, 'wechat', '
{
        "content": "广州今日天气：29度，大部分多云，降雨概率：80%",
        "mentioned_list":["wangqing","@all"],
        "mentioned_mobile_list":["13800001111","@all"]
 }
', '{"url":"https://oapi.dingtalk.com/robot/send?access_token=dd0f8a3340c15ec0f1d54b91980584c1127abe6dd143a964d44385647bc15124"}');
INSERT INTO "public"."media_type_setting" VALUES (2, 'dingtalk', '{
    "msgtype":"text",
    "text": {
        "content":"%s"
    }
}', '{"url":"https://oapi.dingtalk.com/robot/send?access_token=dd0f8a3340c15ec0f1d54b91980584c1127abe6dd143a964d44385647bc15124","secret":"SEC9b9e759c78a216ddcbeb2612694dbd6e7420b75fe78ffe0de9fac0701e3d4d45"}');

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS "public"."messages";
CREATE TABLE "public"."messages" (
  "classify" int4 NOT NULL,
  "title" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "content" text COLLATE "pg_catalog"."default",
  "clock" int4 NOT NULL,
  "module" varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "readed" int2 DEFAULT 0,
  "user_id" int8,
  "id" int8 NOT NULL DEFAULT nextval('messages_id_seq'::regclass)
)
;
COMMENT ON COLUMN "public"."messages"."classify" IS '消息的类型';
COMMENT ON COLUMN "public"."messages"."module" IS '消息跳转的模块';
COMMENT ON COLUMN "public"."messages"."readed" IS '是否已读 1-是 0-否';

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630303245, NULL, NULL, 1, 55);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630303288, NULL, NULL, 1, 56);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630303362, NULL, NULL, 1, 57);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630303458, NULL, NULL, 1, 58);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630304019, NULL, NULL, 1, 59);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630304038, NULL, NULL, 1, 60);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630304155, NULL, NULL, 1, 61);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630304242, NULL, NULL, 1, 62);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919518, NULL, NULL, 1, 63);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919518, NULL, NULL, 1, 64);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919518, NULL, NULL, 1, 65);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919518, NULL, NULL, 1, 66);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919518, NULL, NULL, 1, 67);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919564, NULL, NULL, 1, 68);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919564, NULL, NULL, 1, 69);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919564, NULL, NULL, 1, 70);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919564, NULL, NULL, 1, 71);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630919564, NULL, NULL, 1, 72);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630921533, NULL, NULL, 1, 73);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630921533, NULL, NULL, 1, 74);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630921533, NULL, NULL, 1, 75);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630921533, NULL, NULL, 1, 76);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432185862590431232","triggername":"test","deviceStatus":"{EVENT.TAGS.OFFLINE}"}', 1630921533, NULL, NULL, 1, 77);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"{TRIGGER.DESCRIPTION","hostname":"1432636352419790848"}', 1631763023, NULL, NULL, 80, 78);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"{TRIGGER.DESCRIPTION","hostname":"1432636352419790848"}', 1631763023, NULL, NULL, 1, 79);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"{TRIGGER.DESCRIPTION","hostname":"1432636352419790848"}', 1631763023, NULL, NULL, 115, 80);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"{TRIGGER.DESCRIPTION","hostname":"1432636352419790848"}', 1631763023, NULL, NULL, 79, 81);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"","hostname":"1432636352419790848"}', 1631763230, NULL, NULL, 80, 82);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"","hostname":"1432636352419790848"}', 1631763230, NULL, NULL, 1, 83);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"","hostname":"1432636352419790848"}', 1631763230, NULL, NULL, 115, 84);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"","hostname":"1432636352419790848"}', 1631763230, NULL, NULL, 79, 85);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763232, NULL, NULL, 80, 86);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763232, NULL, NULL, 1, 87);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763232, NULL, NULL, 115, 88);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763232, NULL, NULL, 79, 89);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763259, NULL, NULL, 80, 90);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763259, NULL, NULL, 1, 91);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763259, NULL, NULL, 115, 92);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763259, NULL, NULL, 79, 93);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763260, NULL, NULL, 80, 94);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763260, NULL, NULL, 1, 95);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763260, NULL, NULL, 115, 96);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"0​"}', 1631763260, NULL, NULL, 79, 97);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763533, NULL, NULL, 80, 98);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763533, NULL, NULL, 1, 99);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763533, NULL, NULL, 115, 100);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763533, NULL, NULL, 79, 101);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763535, NULL, NULL, 80, 102);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763535, NULL, NULL, 1, 103);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763535, NULL, NULL, 115, 104);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"{EVENT.OBJECTID}​"}', 1631763535, NULL, NULL, 79, 105);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763972, NULL, NULL, 80, 106);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763972, NULL, NULL, 1, 107);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763972, NULL, NULL, 115, 108);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763972, NULL, NULL, 79, 109);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763973, NULL, NULL, 80, 110);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763973, NULL, NULL, 1, 111);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763973, NULL, NULL, 115, 112);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"​"}', 1631763973, NULL, NULL, 79, 113);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764088, NULL, NULL, 80, 114);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764088, NULL, NULL, 1, 115);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764088, NULL, NULL, 115, 116);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764088, NULL, NULL, 79, 117);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764090, NULL, NULL, 80, 118);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764090, NULL, NULL, 1, 119);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764090, NULL, NULL, 115, 120);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631764090, NULL, NULL, 79, 121);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774313, NULL, NULL, 80, 122);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774313, NULL, NULL, 1, 123);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774313, NULL, NULL, 115, 124);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774313, NULL, NULL, 79, 125);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774326, NULL, NULL, 80, 126);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774327, NULL, NULL, 1, 127);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774327, NULL, NULL, 115, 128);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774327, NULL, NULL, 79, 129);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774425, NULL, NULL, 80, 130);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774425, NULL, NULL, 115, 131);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774425, NULL, NULL, 1, 132);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774425, NULL, NULL, 79, 133);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774451, NULL, NULL, 80, 134);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774451, NULL, NULL, 115, 135);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774451, NULL, NULL, 1, 136);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774451, NULL, NULL, 79, 137);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774484, NULL, NULL, 80, 138);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774484, NULL, NULL, 115, 139);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774484, NULL, NULL, 1, 140);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774484, NULL, NULL, 79, 141);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774505, NULL, NULL, 80, 142);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774505, NULL, NULL, 115, 143);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774505, NULL, NULL, 1, 144);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774505, NULL, NULL, 79, 145);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774729, NULL, NULL, 80, 146);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774729, NULL, NULL, 115, 147);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774729, NULL, NULL, 1, 148);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774729, NULL, NULL, 79, 149);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774749, NULL, NULL, 80, 150);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774749, NULL, NULL, 115, 151);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774749, NULL, NULL, 1, 152);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggerid":"1436570370054893568​"}', 1631774749, NULL, NULL, 79, 153);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775655, NULL, NULL, 80, 154);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775655, NULL, NULL, 115, 155);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775655, NULL, NULL, 1, 156);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775655, NULL, NULL, 79, 157);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775672, NULL, NULL, 80, 158);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775672, NULL, NULL, 115, 159);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775673, NULL, NULL, 1, 160);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"1432636352419790848","triggername":"1436570370054893568​","triggerid":"20077​"}', 1631775673, NULL, NULL, 79, 161);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438757419495247872​","triggerid":"20106​"}', 1631861533, NULL, NULL, 1, 162);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438757419495247872​","triggerid":"20106​"}', 1631861533, NULL, NULL, 116, 163);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438757419495247872​","triggerid":"20106​"}', 1631861533, NULL, NULL, 79, 164);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438757739495477248​","triggerid":"20107​"}', 1631861635, NULL, NULL, 1, 165);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438757739495477248​","triggerid":"20107​"}', 1631861635, NULL, NULL, 116, 166);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438757739495477248​","triggerid":"20107​"}', 1631861635, NULL, NULL, 79, 167);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438758964660064256​","triggerid":"20108​"}', 1631861892, NULL, NULL, 1, 168);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438758964660064256​","triggerid":"20108​"}', 1631861892, NULL, NULL, 116, 169);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438758964660064256​","triggerid":"20108​"}', 1631861892, NULL, NULL, 79, 170);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759211675209728​","triggerid":"20109​"}', 1631861959, NULL, NULL, 1, 171);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759211675209728​","triggerid":"20109​"}', 1631861959, NULL, NULL, 116, 172);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759211675209728​","triggerid":"20109​"}', 1631861959, NULL, NULL, 79, 173);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759729210335232​","triggerid":"20110​"}', 1631862201, NULL, NULL, 1, 174);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759729210335232​","triggerid":"20110​"}', 1631862201, NULL, NULL, 116, 175);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759729210335232​","triggerid":"20110​"}', 1631862201, NULL, NULL, 79, 176);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759844151042048​","triggerid":"20111​"}', 1631862205, NULL, NULL, 1, 177);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759844151042048​","triggerid":"20111​"}', 1631862205, NULL, NULL, 116, 178);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438759844151042048​","triggerid":"20111​"}', 1631862205, NULL, NULL, 79, 179);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438762285726703616​","triggerid":"20112​"}', 1631862674, NULL, NULL, 1, 180);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438762285726703616​","triggerid":"20112​"}', 1631862674, NULL, NULL, 116, 181);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438762285726703616​","triggerid":"20112​"}', 1631862675, NULL, NULL, 79, 182);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438763312085536768​","triggerid":"20113​"}', 1631862918, NULL, NULL, 1, 183);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438763312085536768​","triggerid":"20113​"}', 1631862918, NULL, NULL, 116, 184);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438763312085536768​","triggerid":"20113​"}', 1631862918, NULL, NULL, 79, 185);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631865382, NULL, NULL, 1, 186);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631865382, NULL, NULL, 116, 187);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631865382, NULL, NULL, 79, 188);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631868823, NULL, NULL, 1, 189);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631868823, NULL, NULL, 116, 190);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631868823, NULL, NULL, 79, 191);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631868829, NULL, NULL, 1, 192);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631868829, NULL, NULL, 116, 193);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"hostname":"dfd","triggername":"1438773665242583040​","triggerid":"20115​"}', 1631868829, NULL, NULL, 79, 194);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299476, NULL, NULL, 1, 195);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299476, NULL, NULL, 116, 196);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299476, NULL, NULL, 79, 197);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299697, NULL, NULL, 1, 198);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299697, NULL, NULL, 116, 199);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299697, NULL, NULL, 79, 200);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299723, NULL, NULL, 1, 201);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299723, NULL, NULL, 116, 202);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299723, NULL, NULL, 79, 203);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299759, NULL, NULL, 1, 204);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299759, NULL, NULL, 116, 205);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299759, NULL, NULL, 79, 206);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299788, NULL, NULL, 1, 207);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299788, NULL, NULL, 116, 208);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299788, NULL, NULL, 79, 209);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299845, NULL, NULL, 1, 210);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299845, NULL, NULL, 116, 211);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299845, NULL, NULL, 79, 212);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299870, NULL, NULL, 1, 213);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299870, NULL, NULL, 116, 214);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632299870, NULL, NULL, 79, 215);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632316171, NULL, NULL, 1, 216);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632316171, NULL, NULL, 116, 217);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632316171, NULL, NULL, 79, 218);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632316187, NULL, NULL, 1, 219);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632316187, NULL, NULL, 116, 220);
INSERT INTO "public"."messages" VALUES (1, '告警消息', '{"triggerName":"1438773665242583040","hostname":"dfd"}', 1632316187, NULL, NULL, 79, 221);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS "public"."product";
CREATE TABLE "public"."product" (
  "product_id" int8 NOT NULL,
  "group_id" int8,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "type" varchar(64) COLLATE "pg_catalog"."default",
  "manufacturer" varchar(255) COLLATE "pg_catalog"."default",
  "model" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "create_user" int8,
  "update_time" timestamp(6),
  "update_user" int8,
  "product_code" varchar(255) COLLATE "pg_catalog"."default",
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product"."product_id" IS '产品ID';
COMMENT ON COLUMN "public"."product"."group_id" IS '产品组ID';
COMMENT ON COLUMN "public"."product"."name" IS '产品名称';
COMMENT ON COLUMN "public"."product"."type" IS '设备类型';
COMMENT ON COLUMN "public"."product"."manufacturer" IS '厂商';
COMMENT ON COLUMN "public"."product"."model" IS '型号';
COMMENT ON COLUMN "public"."product"."remark" IS '备注描述';
COMMENT ON COLUMN "public"."product"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."product"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."product"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."product"."update_user" IS '更新人';
COMMENT ON COLUMN "public"."product"."product_code" IS '产品编号';
COMMENT ON COLUMN "public"."product"."zbx_id" IS 'Zabbix对应模板ID';

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO "public"."product" VALUES (1440880212866494464, 214, '温度计', '1', '', '', '', '2021-09-23 11:26:27.74', 1, '2021-09-23 11:26:27.74', 1, 'wd0001', '10589');

-- ----------------------------
-- Table structure for product_attribute
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_attribute";
CREATE TABLE "public"."product_attribute" (
  "attr_id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "units" varchar(16) COLLATE "pg_catalog"."default",
  "source" varchar(16) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "product_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "create_user" int8,
  "create_time" timestamp(6),
  "update_user" int8,
  "update_time" timestamp(6),
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default",
  "value_type" varchar(16) COLLATE "pg_catalog"."default",
  "dep_attr_id" int8,
  "template_id" int8
)
;
COMMENT ON COLUMN "public"."product_attribute"."attr_id" IS '属性ID';
COMMENT ON COLUMN "public"."product_attribute"."name" IS '属性名称';
COMMENT ON COLUMN "public"."product_attribute"."key" IS '属性标识Key';
COMMENT ON COLUMN "public"."product_attribute"."units" IS '数字类型单位';
COMMENT ON COLUMN "public"."product_attribute"."source" IS '数据来源';
COMMENT ON COLUMN "public"."product_attribute"."remark" IS '备注描述';
COMMENT ON COLUMN "public"."product_attribute"."product_id" IS '产品ID';
COMMENT ON COLUMN "public"."product_attribute"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."product_attribute"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."product_attribute"."update_user" IS '更新人';
COMMENT ON COLUMN "public"."product_attribute"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."product_attribute"."zbx_id" IS 'itemid';
COMMENT ON COLUMN "public"."product_attribute"."value_type" IS '值类型';
COMMENT ON COLUMN "public"."product_attribute"."dep_attr_id" IS '依赖属性ID';
COMMENT ON COLUMN "public"."product_attribute"."template_id" IS '继承的属性ID';

-- ----------------------------
-- Records of product_attribute
-- ----------------------------
INSERT INTO "public"."product_attribute" VALUES (1440880744175759360, '温度', 'wd', NULL, '2', NULL, '1440880212866494464', 1, '2021-09-23 11:28:34.672', 1, '2021-09-23 11:28:34.672', '37912', '0', NULL, NULL);
INSERT INTO "public"."product_attribute" VALUES (1440880745664737280, '温度', 'wd', NULL, '2', NULL, '101', 1, '2021-09-23 11:28:34.693', 1, '2021-09-23 11:28:34.778', '37913', '0', NULL, 1440880744175759360);

-- ----------------------------
-- Table structure for product_attribute_event
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_attribute_event";
CREATE TABLE "public"."product_attribute_event" (
  "attr_id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "units" varchar(16) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "product_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "create_user" int8,
  "create_time" timestamp(6),
  "update_user" int8,
  "update_time" timestamp(6),
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default",
  "value_type" varchar(16) COLLATE "pg_catalog"."default",
  "template_id" int8,
  "event_level" char(1) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_attribute_event"."attr_id" IS '属性ID';
COMMENT ON COLUMN "public"."product_attribute_event"."name" IS '属性名称';
COMMENT ON COLUMN "public"."product_attribute_event"."key" IS '属性标识Key';
COMMENT ON COLUMN "public"."product_attribute_event"."units" IS '数字类型单位';
COMMENT ON COLUMN "public"."product_attribute_event"."remark" IS '备注描述';
COMMENT ON COLUMN "public"."product_attribute_event"."product_id" IS '产品ID';
COMMENT ON COLUMN "public"."product_attribute_event"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."product_attribute_event"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."product_attribute_event"."update_user" IS '更新人';
COMMENT ON COLUMN "public"."product_attribute_event"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."product_attribute_event"."zbx_id" IS 'itemid';
COMMENT ON COLUMN "public"."product_attribute_event"."value_type" IS '值类型';
COMMENT ON COLUMN "public"."product_attribute_event"."template_id" IS '继承的属性ID';
COMMENT ON COLUMN "public"."product_attribute_event"."event_level" IS '事件级别';

-- ----------------------------
-- Records of product_attribute_event
-- ----------------------------

-- ----------------------------
-- Table structure for product_event
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_event";
CREATE TABLE "public"."product_event" (
  "event_rule_id" int8 NOT NULL,
  "event_rule_name" varchar(255) COLLATE "pg_catalog"."default",
  "event_level" varchar(1) COLLATE "pg_catalog"."default",
  "exp_logic" varchar(10) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "create_user" int8,
  "update_time" timestamp(6),
  "update_user" int8,
  "event_notify" varchar(1) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_event"."event_rule_id" IS '告警规则ID';
COMMENT ON COLUMN "public"."product_event"."event_rule_name" IS '告警规则名称';
COMMENT ON COLUMN "public"."product_event"."event_level" IS '告警等级';
COMMENT ON COLUMN "public"."product_event"."exp_logic" IS 'and 或者 or';
COMMENT ON COLUMN "public"."product_event"."event_notify" IS '0 否 1 是';

-- ----------------------------
-- Records of product_event
-- ----------------------------

-- ----------------------------
-- Table structure for product_event_expression
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_event_expression";
CREATE TABLE "public"."product_event_expression" (
  "event_exp_id" int8 NOT NULL,
  "event_rule_id" int8,
  "function" varchar(16) COLLATE "pg_catalog"."default",
  "scope" varchar(10) COLLATE "pg_catalog"."default",
  "condition" varchar(10) COLLATE "pg_catalog"."default",
  "value" varchar(255) COLLATE "pg_catalog"."default",
  "device_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "product_attr_key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "unit" varchar(16) COLLATE "pg_catalog"."default",
  "product_attr_id" int8,
  "product_attr_type" varchar(4) COLLATE "pg_catalog"."default",
  "period" varchar(8) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_event_expression"."event_exp_id" IS '告警函数ID';
COMMENT ON COLUMN "public"."product_event_expression"."event_rule_id" IS '告警规则ID';
COMMENT ON COLUMN "public"."product_event_expression"."device_id" IS '设备ID';
COMMENT ON COLUMN "public"."product_event_expression"."product_attr_key" IS 'item key';
COMMENT ON COLUMN "public"."product_event_expression"."product_attr_id" IS '属性ID';
COMMENT ON COLUMN "public"."product_event_expression"."product_attr_type" IS '属性类型 属性 事件';
COMMENT ON COLUMN "public"."product_event_expression"."period" IS '取值周期 时间 周期';

-- ----------------------------
-- Records of product_event_expression
-- ----------------------------

-- ----------------------------
-- Table structure for product_event_relation
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_event_relation";
CREATE TABLE "public"."product_event_relation" (
  "id" int8 NOT NULL DEFAULT nextval('product_event_relation_id_seq'::regclass),
  "event_rule_id" int8 NOT NULL,
  "relation_id" varchar(64) COLLATE "pg_catalog"."default",
  "zbx_id" int4,
  "inherit" varchar(4) COLLATE "pg_catalog"."default" DEFAULT '0'::character varying,
  "status" varchar(8) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_event_relation"."event_rule_id" IS '规则ID';
COMMENT ON COLUMN "public"."product_event_relation"."relation_id" IS '关联产品或设备ID';
COMMENT ON COLUMN "public"."product_event_relation"."zbx_id" IS 'trigger id';
COMMENT ON COLUMN "public"."product_event_relation"."inherit" IS '是否来自产品';
COMMENT ON COLUMN "public"."product_event_relation"."status" IS '状态';
COMMENT ON COLUMN "public"."product_event_relation"."remark" IS '备注';

-- ----------------------------
-- Records of product_event_relation
-- ----------------------------

-- ----------------------------
-- Table structure for product_event_service
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_event_service";
CREATE TABLE "public"."product_event_service" (
  "event_rule_id" int8,
  "service_id" int8,
  "device_id" varchar(64) COLLATE "pg_catalog"."default",
  "execute_device_id" varchar(64) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_event_service"."event_rule_id" IS '告警规则ID';
COMMENT ON COLUMN "public"."product_event_service"."service_id" IS '产品服务ID';
COMMENT ON COLUMN "public"."product_event_service"."device_id" IS '关联产品或设备ID';
COMMENT ON COLUMN "public"."product_event_service"."execute_device_id" IS '执行目标设备ID';

-- ----------------------------
-- Records of product_event_service
-- ----------------------------

-- ----------------------------
-- Table structure for product_event_tags
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_event_tags";
CREATE TABLE "public"."product_event_tags" (
  "id" int4 NOT NULL DEFAULT nextval('product_event_tags_id_seq'::regclass),
  "event_rule_id" int8 NOT NULL,
  "tag_zbx_id" int4 NOT NULL
)
;
COMMENT ON COLUMN "public"."product_event_tags"."id" IS '自增ID';
COMMENT ON COLUMN "public"."product_event_tags"."event_rule_id" IS '告警规则ID';
COMMENT ON COLUMN "public"."product_event_tags"."tag_zbx_id" IS 'zabbix tag id';

-- ----------------------------
-- Records of product_event_tags
-- ----------------------------

-- ----------------------------
-- Table structure for product_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_group";
CREATE TABLE "public"."product_group" (
  "product_group_id" int8 NOT NULL,
  "pid" int8,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6),
  "create_user" int8,
  "update_time" timestamp(6),
  "update_user" int8
)
;
COMMENT ON COLUMN "public"."product_group"."product_group_id" IS '产品分组ID';
COMMENT ON COLUMN "public"."product_group"."pid" IS '父ID';
COMMENT ON COLUMN "public"."product_group"."name" IS '分组名称';
COMMENT ON COLUMN "public"."product_group"."remark" IS '备注';
COMMENT ON COLUMN "public"."product_group"."key" IS '分组标识';
COMMENT ON COLUMN "public"."product_group"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."product_group"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."product_group"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."product_group"."update_user" IS '更新人';

-- ----------------------------
-- Records of product_group
-- ----------------------------

-- ----------------------------
-- Table structure for product_service
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_service";
CREATE TABLE "public"."product_service" (
  "id" int8 NOT NULL DEFAULT nextval('service_id_seq'::regclass),
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "mark" varchar(64) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "async" varchar(16) COLLATE "pg_catalog"."default" DEFAULT 1
)
;
COMMENT ON COLUMN "public"."product_service"."name" IS '服务名称';
COMMENT ON COLUMN "public"."product_service"."mark" IS '服务标识';
COMMENT ON COLUMN "public"."product_service"."remark" IS '备注';
COMMENT ON COLUMN "public"."product_service"."async" IS '执行方式 0-同步 1-异步';

-- ----------------------------
-- Records of product_service
-- ----------------------------

-- ----------------------------
-- Table structure for product_service_param
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_service_param";
CREATE TABLE "public"."product_service_param" (
  "id" int8 NOT NULL DEFAULT nextval('service_param_id_seq'::regclass),
  "service_id" int8,
  "key" varchar(32) COLLATE "pg_catalog"."default",
  "name" varchar(32) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_service_param"."service_id" IS '服务ID';
COMMENT ON COLUMN "public"."product_service_param"."key" IS '参数标识';
COMMENT ON COLUMN "public"."product_service_param"."name" IS '参数名称';

-- ----------------------------
-- Records of product_service_param
-- ----------------------------

-- ----------------------------
-- Table structure for product_service_relation
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_service_relation";
CREATE TABLE "public"."product_service_relation" (
  "service_id" int8 NOT NULL,
  "relation_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "id" int8 NOT NULL DEFAULT nextval('product_service_relation_id_seq'::regclass),
  "inherit" varchar(4) COLLATE "pg_catalog"."default" DEFAULT 0
)
;
COMMENT ON COLUMN "public"."product_service_relation"."inherit" IS '是否继承';

-- ----------------------------
-- Records of product_service_relation
-- ----------------------------

-- ----------------------------
-- Table structure for product_status_function
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_status_function";
CREATE TABLE "public"."product_status_function" (
  "rule_id" int8 NOT NULL,
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default",
  "rule_function" varchar(10) COLLATE "pg_catalog"."default",
  "rule_condition" varchar(255) COLLATE "pg_catalog"."default",
  "rule_status" int2 DEFAULT 1,
  "create_user" int8,
  "update_user" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "rule_function_recovery" varchar(10) COLLATE "pg_catalog"."default",
  "rule_condition_recovery" varchar(255) COLLATE "pg_catalog"."default",
  "attr_id" int8,
  "attr_id_recovery" int8,
  "unit" varchar(8) COLLATE "pg_catalog"."default",
  "unit_recovery" varchar(8) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_status_function"."rule_id" IS '触发器ID，离线 上线';
COMMENT ON COLUMN "public"."product_status_function"."zbx_id" IS 'zabbix trigger id';
COMMENT ON COLUMN "public"."product_status_function"."rule_function" IS '''nodata'' or ''last''';
COMMENT ON COLUMN "public"."product_status_function"."rule_condition" IS '''5m'' or ''4''';
COMMENT ON COLUMN "public"."product_status_function"."rule_status" IS '0 or 1';
COMMENT ON COLUMN "public"."product_status_function"."rule_function_recovery" IS '''nodata'' or ''last''';
COMMENT ON COLUMN "public"."product_status_function"."rule_condition_recovery" IS '''5m'' or ''4''';
COMMENT ON COLUMN "public"."product_status_function"."attr_id" IS 'trigger  attrId';
COMMENT ON COLUMN "public"."product_status_function"."attr_id_recovery" IS 'recovery  attrId';

-- ----------------------------
-- Records of product_status_function
-- ----------------------------

-- ----------------------------
-- Table structure for product_status_function_relation
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_status_function_relation";
CREATE TABLE "public"."product_status_function_relation" (
  "rule_id" int8 NOT NULL,
  "relation_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "id" int8 NOT NULL DEFAULT nextval('product_status_function_relation_id_seq'::regclass),
  "inherit" varchar(4) COLLATE "pg_catalog"."default" DEFAULT 0
)
;
COMMENT ON COLUMN "public"."product_status_function_relation"."inherit" IS '是否继承自产品 1是0否';

-- ----------------------------
-- Records of product_status_function_relation
-- ----------------------------

-- ----------------------------
-- Table structure for product_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."product_type";
CREATE TABLE "public"."product_type" (
  "id" int4 NOT NULL DEFAULT nextval('product_type_product_type_id_seq'::regclass),
  "pid" int4,
  "name" varchar(32) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "pids" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_type"."name" IS '分类名称';
COMMENT ON COLUMN "public"."product_type"."remark" IS '备注';
COMMENT ON COLUMN "public"."product_type"."pids" IS '所有父ID';

-- ----------------------------
-- Records of product_type
-- ----------------------------
INSERT INTO "public"."product_type" VALUES (214, 0, '温控产品', NULL, '[0],');

-- ----------------------------
-- Table structure for service_execute_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."service_execute_record";
CREATE TABLE "public"."service_execute_record" (
  "id" int8 NOT NULL DEFAULT nextval('service_execute_record_id_seq'::regclass),
  "create_time" timestamp(6),
  "service_name" varchar(64) COLLATE "pg_catalog"."default",
  "param" varchar(255) COLLATE "pg_catalog"."default",
  "device_id" varchar(64) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."service_execute_record"."service_name" IS '服务名称';
COMMENT ON COLUMN "public"."service_execute_record"."param" IS '执行参数';
COMMENT ON COLUMN "public"."service_execute_record"."device_id" IS '设备ID';

-- ----------------------------
-- Records of service_execute_record
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_config";
CREATE TABLE "public"."sys_config" (
  "id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_flag" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_type_id" int8,
  "value" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "create_user" int8,
  "update_user" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "status" varchar(8) COLLATE "pg_catalog"."default" DEFAULT 'ENABLE'::character varying
)
;
COMMENT ON COLUMN "public"."sys_config"."id" IS '主键ID';
COMMENT ON COLUMN "public"."sys_config"."name" IS '名称';
COMMENT ON COLUMN "public"."sys_config"."code" IS '属性编码标识';
COMMENT ON COLUMN "public"."sys_config"."dict_flag" IS '是否是字典中的值';
COMMENT ON COLUMN "public"."sys_config"."dict_type_id" IS '字典类型的编码';
COMMENT ON COLUMN "public"."sys_config"."value" IS '属性值，如果是字典中的类型，则为dict的code';
COMMENT ON COLUMN "public"."sys_config"."remark" IS '备注';
COMMENT ON COLUMN "public"."sys_config"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sys_config"."update_user" IS '更新人';
COMMENT ON COLUMN "public"."sys_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_config"."update_time" IS '修改时间';
COMMENT ON COLUMN "public"."sys_config"."status" IS '是否可修改  ENABLE可以  DISABLE不可';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO "public"."sys_config" VALUES (1145915627211370496, '全局主机组Id', 'ZEUS_HOST_GROUP_ID', 'Y', NULL, '19', '全局主机和模板组ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1143468867767607298, '用户角色ID', 'ZEUS_ADMIN_ROLE_ID', 'N', NULL, '2', '用户角色ID', 1, 1, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370497, '离线回调ActionId', 'ZEUS_OFFLINE_ACTION_ID', 'Y', NULL, '20', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370503, '告警回调ActionId', 'ZEUS_ALARM_ACTION_ID', 'Y', NULL, '21', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370504, '动作回调ActionId', 'ZEUS_EXEC_ACTION_ID', 'Y', NULL, '22', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370505, '事件回调ActionId', 'ZEUS_EVENT_ACTION_ID', 'Y', NULL, '23', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370502, '获取token的header标识', 'ZEUS_TOKEN_HEADER_NAME', 'N', NULL, 'Authorization', '获取token的header标识', 1, 1, '2019-10-16 23:02:39', '2021-08-03 16:38:12.432', 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1143468689664876546, '管理系统名称', 'ZEUS_SYSTEM_NAME', 'N', NULL, 'ZEUS-IOT', '管理系统名称', 1, 1, '2019-06-25 18:39:15', '2021-08-09 17:17:30.189', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370501, '获取系统地密钥过期时间', 'ZEUS_JWT_SECRET_EXPIRE', 'N', NULL, '86400', '获取系统地密钥过期时间（单位：秒），默认1天', 1, 1, '2019-10-16 23:02:39', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143469008025133058, 'OAuth2登录用户的账号标识', 'ZEUS_OAUTH2_PREFIX', 'N', NULL, 'oauth2', 'OAuth2登录用户的账号标识', 1, 1, '2019-06-25 18:40:31', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370499, '文件上传路径', 'ZEUS_FILE_UPLOAD_PATH', 'N', NULL, '/tmp', '文件上传默认目录', 1, 1, '2019-08-30 09:10:40', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143324237579165697, '验证码开关', 'ZEUS_KAPTCHA_OPEN', 'Y', 1106120265689055233, 'DISABLE', '是否开启验证码', 1, 1, '2019-06-24 12:46:43', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370498, 'Zeus发布的编号', 'ZEUS_SYSTEM_RELEASE_VERSION', 'N', NULL, '10', '用于防止浏览器缓存相关的js和css', 1, 1, '2019-07-02 12:42:30', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143468867767607297, '默认系统密码', 'ZEUS_DEFAULT_PASSWORD', 'N', NULL, '111111', '默认系统密码', 1, 1, '2019-06-25 18:39:57', '2021-08-03 16:38:12.432', 'ENABLE');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict";
CREATE TABLE "public"."sys_dict" (
  "dict_id" int8 NOT NULL,
  "dict_type_id" int8 NOT NULL,
  "code" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "status" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4,
  "remark" varchar(1000) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "create_user" int8,
  "update_user" int8,
  "groups" varchar(64) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."sys_dict"."dict_id" IS '字典id';
COMMENT ON COLUMN "public"."sys_dict"."dict_type_id" IS '所属字典类型的id';
COMMENT ON COLUMN "public"."sys_dict"."code" IS '字典编码';
COMMENT ON COLUMN "public"."sys_dict"."name" IS '字典名称';
COMMENT ON COLUMN "public"."sys_dict"."status" IS '状态（字典）';
COMMENT ON COLUMN "public"."sys_dict"."sort" IS '排序';
COMMENT ON COLUMN "public"."sys_dict"."remark" IS '字典的描述';
COMMENT ON COLUMN "public"."sys_dict"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_dict"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_dict"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sys_dict"."update_user" IS '修改人';
COMMENT ON COLUMN "public"."sys_dict"."groups" IS '分组';
COMMENT ON TABLE "public"."sys_dict" IS '基础字典';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO "public"."sys_dict" VALUES (1106120532442595330, 1106120208097067009, 'M', '男', 'ENABLE', NULL, '', '2019-03-14 17:11:00', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120574163337218, 1106120208097067009, 'F', '女', 'ENABLE', NULL, '', '2019-03-14 17:11:10', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120645697191938, 1106120265689055233, 'ENABLE', '启用', 'ENABLE', NULL, '', '2019-03-14 17:11:27', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120699468169217, 1106120265689055233, 'DISABLE', '禁用', 'ENABLE', NULL, '', '2019-03-14 17:11:40', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120784318939137, 1106120322450571266, 'ENABLE', '启用', 'ENABLE', NULL, '', '2019-03-14 17:12:00', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120825993543682, 1106120322450571266, 'FREEZE', '冻结', 'ENABLE', 1, '', '2019-03-14 17:12:10', '2019-03-16 10:56:36', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120875872206849, 1106120322450571266, 'DELETED', '已删除', 'ENABLE', -1221, '', '2019-03-14 17:12:22', '2019-03-16 10:56:53', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120935070613505, 1106120388036902914, 'Y', '删除', 'ENABLE', 23333, '', '2019-03-14 17:12:36', '2019-03-16 10:58:53', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1106120968910258177, 1106120388036902914, 'N', '未删除', 'ENABLE', 1212211221, '', '2019-03-14 17:12:44', '2019-03-16 10:59:03', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1149218674746355713, 1149217131989069826, 'BASE_SYSTEM', '基础功能', 'ENABLE', 1, '系统管理平台', '2019-07-11 15:27:38', '2020-01-01 17:14:45', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533174626959361, 1160532704105742337, '00101', '办公审批', 'ENABLE', 10, '', '2019-08-11 20:47:25', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533264645111810, 1160532704105742337, '00102', '行政审批', 'ENABLE', 20, '', '2019-08-11 20:47:47', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533377727741954, 1160532775455047681, 'KEY_LEAVE', '请假流程标识', 'ENABLE', 10, '', '2019-08-11 20:48:14', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533455343337474, 1160532775455047681, 'KEY_FINANCE', '财务流程标识', 'ENABLE', 20, '', '2019-08-11 20:48:32', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533574843252737, 1160532886713155585, '00401', '事假', 'ENABLE', 10, '', '2019-08-11 20:49:01', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533625615302658, 1160532886713155585, '00402', '婚假', 'ENABLE', 20, '', '2019-08-11 20:49:13', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533707215486977, 1160532886713155585, '00403', '产假', 'ENABLE', 30, '', '2019-08-11 20:49:32', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533765403066370, 1160532886713155585, '00404', '病假', 'ENABLE', 40, '', '2019-08-11 20:49:46', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533863834992641, 1160532886713155585, '00405', '公假', 'ENABLE', 50, '', '2019-08-11 20:50:09', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160533945309347841, 1160532886713155585, '00406', '年假', 'ENABLE', 60, '', '2019-08-11 20:50:29', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1160534007389241346, 1160532886713155585, '00407', '其他', 'ENABLE', 70, '', '2019-08-11 20:50:44', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1212300736972668929, 1149217131989069826, 'ENT_FUNC', '企业功能', 'ENABLE', 20, '企业功能', '2020-01-01 17:13:14', NULL, 1, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423166477712343040, 1423160140530053120, 'DICTTYPE', '字典类型', 'ENABLE', 1, '测试', '2021-08-05 14:18:24.098', '2021-08-05 14:18:24.098', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423234510216499200, 1423160140530053120, 'DICTTYPE4', '字典类型4', 'ENABLE', 1, '测试', '2021-08-05 18:48:44.31', '2021-08-05 18:48:44.31', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423234906741895168, 1423160140530053120, 'DICTTYPE5', '字典类型5', 'ENABLE', 1, '测试', '2021-08-05 18:50:18.848', '2021-08-05 18:50:18.848', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423235174556626944, 1423160140530053120, 'DICTTYPE6', '字典类型6', 'ENABLE', 1, '测试', '2021-08-05 18:51:22.701', '2021-08-05 18:51:22.701', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423464964597334017, 1160532886713155586, '1', '直连设备', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423464964597334018, 1160532886713155586, '2', '网关子设备', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423464964597334019, 1160532886713155586, '3', '网关设备', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199424, 1423840415470653440, '凤飞飞', '凤飞飞', 'ENABLE', NULL, '凤飞飞', '2021-08-07 14:44:21.192', '2021-08-07 14:44:21.192', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199425, 1160532886713155587, 'wechat', '企业微信', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199426, 1160532886713155587, 'dingtalk', '钉钉', 'ENABLE', 2, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199427, 1160532886713155587, 'feishu', '飞书', 'ENABLE', 3, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199430, 1160532886713155588, '18', '根据单个属性预处理', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199428, 1160532886713155588, '2', '主动上报', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199435, 1160532886713155589, '%', '百分比', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '常用单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199436, 1160532886713155589, 'count', '次', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '常用单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199437, 1160532886713155589, 'r/min', '转每分钟', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '常用单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199431, 1160532886713155590, '3', '整数', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199432, 1160532886713155590, '0', '小数', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199433, 1160532886713155590, '1', '字符', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199434, 1160532886713155590, '4', '文本', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199438, 1160532886713155589, 'nm', '纳米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '长度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199439, 1160532886713155589, 'μm', '微米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '长度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199440, 1160532886713155589, 'mm', '毫米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '长度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199441, 1160532886713155589, 'cm', '厘米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '长度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199442, 1160532886713155589, 'm', '米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '长度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199443, 1160532886713155589, 'km', '千米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '长度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199444, 1160532886713155589, 'mm²', '平方毫米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '面积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199445, 1160532886713155589, 'cm²', '平方厘米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '面积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199446, 1160532886713155589, 'm²', '平方米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '面积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199447, 1160532886713155589, 'km²', '平方千米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '面积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199448, 1160532886713155589, 'hm²', '公顷', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '面积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199449, 1160532886713155589, 'd', '天', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '时间单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199450, 1160532886713155589, 'h', '小时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '时间单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199451, 1160532886713155589, 'min', '分钟', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '时间单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199452, 1160532886713155589, 's', '秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '时间单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199453, 1160532886713155589, 'ms', '毫秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '时间单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199454, 1160532886713155589, 'μs', '微秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '时间单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199455, 1160532886713155589, 'ns', '纳秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '时间单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199456, 1160532886713155589, 'mm³', '立方毫米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '体积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199457, 1160532886713155589, 'cm³', '立方厘米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '体积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199458, 1160532886713155589, 'm³', '立方米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '体积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199459, 1160532886713155589, 'km³', '立方千米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '体积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199460, 1160532886713155589, 'm³/s', '立方米每秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199461, 1160532886713155589, 'km³/s', '立方千米每秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199462, 1160532886713155589, 'cm³/s', '立方厘米每秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199463, 1160532886713155589, 'l/s', '升每秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199464, 1160532886713155589, 'm³/h', '立方米每小时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199465, 1160532886713155589, 'km³/h', '立方千米每小时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199466, 1160532886713155589, 'cm³/h', '立方厘米每小时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199467, 1160532886713155589, 'l/h', '升每小时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '流量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199468, 1160532886713155589, 'mL', '毫升', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '容积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199469, 1160532886713155589, 'L', '升', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '容积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199470, 1160532886713155589, 'mg', '毫克', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199471, 1160532886713155589, 'g', '克', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1437341935102316544, 1437341934464782336, 'SSGrdr26', '三石TestKKRl7u', 'DISABLE', 99, NULL, '2021-09-13 17:06:36.507', '2021-09-13 17:06:36.641', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1428599180229115912, 1428599180229115904, '1', '是', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1428599180229115911, 1428599180229115904, '0', '', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199478, 1160532886713155589, 'K', '开尔文', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '温度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199479, 1160532886713155589, '℃', '摄氏度', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '温度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199506, 1160532886713155591, '25', 'Replace', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199507, 1160532886713155591, '4', 'Trim', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199508, 1160532886713155591, '2', 'Right trim', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199509, 1160532886713155591, '3', 'Left trim', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199510, 1160532886713155591, '11', 'XML XPath', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '结构化数据');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199511, 1160532886713155591, '12', 'JSONPath', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '结构化数据');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199480, 1160532886713155589, '℉', '华氏度', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '温度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199481, 1160532886713155589, 'J', '焦耳', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '能量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199514, 1160532886713155591, '1', '自定义倍数', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '自定义计算');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199482, 1160532886713155589, 'cal', '卡', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '能量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199483, 1160532886713155589, 'W', '瓦特', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '功率单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199517, 1160532886713155591, '7', '八进制转十进制', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '数制转换');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199518, 1160532886713155591, '8', '十六进制转十进制', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '数制转换');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199519, 1160532886713155591, '21', 'JavaScript', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '自定义脚本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199504, 1160532886713155589, 'kW·h', '千瓦·时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199484, 1160532886713155589, 'kW', '千瓦特', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '功率单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199485, 1160532886713155589, 'rad', '弧度', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '角度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199486, 1160532886713155589, '°', '度', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '角度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199487, 1160532886713155589, '′', '[角]分', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '角度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199488, 1160532886713155589, '″', '[角]秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '角度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199489, 1160532886713155589, 'Hz', '赫兹', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '频率单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199490, 1160532886713155589, 'MHz', '兆赫兹', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '频率单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199491, 1160532886713155589, 'GHz', 'G赫兹', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '频率单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199492, 1160532886713155589, 'm/s', '米每秒', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '速度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199493, 1160532886713155589, 'km/h', '千米每小时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '速度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199494, 1160532886713155589, 'kn', '节', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '速度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199495, 1160532886713155589, 'V', '伏特', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199496, 1160532886713155589, 'kV', '千伏', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199497, 1160532886713155589, 'mV', '毫伏', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199498, 1160532886713155589, 'μV', '微伏', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199499, 1160532886713155589, 'A', '安培', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199505, 1160532886713155589, 'kgce', 'Kg标准煤', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '能源单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199500, 1160532886713155589, 'Ω', '欧姆', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199501, 1160532886713155589, 'KΩ', '千欧', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199502, 1160532886713155589, 'MΩ', '兆欧', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199503, 1160532886713155589, 'eV', '电子伏', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199512, 1160532886713155591, '13', 'In range', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '验证');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199579, 1428599180229115905, '1', '信息', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199574, 1428599180229115905, '2', '低级', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199577, 1428599180229115905, '3', '中级', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199576, 1428599180229115905, '4', '高级', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199575, 1428599180229115905, '5', '紧急', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199578, 1428599180229115905, '0', '未定义', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1429987619034984449, 1142859918022911591, '1', '异步', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1429987619034984450, 1142859918022911591, '0', '同步', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199472, 1160532886713155589, 'kg', '千克', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199473, 1160532886713155589, 't', '吨', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199474, 1160532886713155589, 'Pa', '帕斯卡', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '压力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199475, 1160532886713155589, 'kPa', '千帕斯卡', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '压力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199476, 1160532886713155589, 'N', '牛顿', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199477, 1160532886713155589, 'N.m', '牛·米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '力单位');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict_type";
CREATE TABLE "public"."sys_dict_type" (
  "dict_type_id" int8 NOT NULL,
  "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(1000) COLLATE "pg_catalog"."default",
  "system_flag" char(1) COLLATE "pg_catalog"."default" NOT NULL,
  "status" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4,
  "create_time" timestamp(6),
  "create_user" int8,
  "update_time" timestamp(6),
  "update_user" int8
)
;
COMMENT ON COLUMN "public"."sys_dict_type"."dict_type_id" IS '字典类型id';
COMMENT ON COLUMN "public"."sys_dict_type"."code" IS '字典类型编码';
COMMENT ON COLUMN "public"."sys_dict_type"."name" IS '字典类型名称';
COMMENT ON COLUMN "public"."sys_dict_type"."remark" IS '字典描述';
COMMENT ON COLUMN "public"."sys_dict_type"."system_flag" IS '是否是系统字典，Y-是，N-否';
COMMENT ON COLUMN "public"."sys_dict_type"."status" IS '状态(字典)';
COMMENT ON COLUMN "public"."sys_dict_type"."sort" IS '排序';
COMMENT ON COLUMN "public"."sys_dict_type"."create_time" IS '添加时间';
COMMENT ON COLUMN "public"."sys_dict_type"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sys_dict_type"."update_time" IS '修改时间';
COMMENT ON COLUMN "public"."sys_dict_type"."update_user" IS '修改人';
COMMENT ON TABLE "public"."sys_dict_type" IS '字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO "public"."sys_dict_type" VALUES (1106120265689055233, 'STATUS', '状态', '', 'Y', 'ENABLE', 3, '2019-03-14 17:09:57', 1, NULL, NULL);
INSERT INTO "public"."sys_dict_type" VALUES (1106120322450571266, 'ACCOUNT_STATUS', '账号状态', '', 'Y', 'ENABLE', 40, '2019-03-14 17:10:10', 1, '2019-08-11 20:46:38', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1106120388036902914, 'DEL_FLAG', '是否删除', '', 'Y', 'ENABLE', 2, '2019-03-14 17:10:26', 1, '2019-03-27 16:26:31', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1149217131989069826, 'SYSTEM_TYPE', '系统分类', '系统所有分类的维护', 'Y', 'ENABLE', 50, '2019-07-11 15:21:30', 1, '2019-08-11 20:46:47', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1160532704105742337, 'FLOW_CATEGARY', '工作流分类', '工作流分类', 'Y', 'ENABLE', 60, '2019-08-11 20:45:33', 1, NULL, NULL);
INSERT INTO "public"."sys_dict_type" VALUES (1160532775455047681, 'FLOW_KEY', '工作流标识', '工作流标识', 'Y', 'ENABLE', 70, '2019-08-11 20:45:50', 1, NULL, NULL);
INSERT INTO "public"."sys_dict_type" VALUES (1160532886713155585, 'LEAVE_TYPE', '请假类型', '请假类型', 'Y', 'ENABLE', 80, '2019-08-11 20:46:17', 1, '2019-08-11 20:46:23', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1106120208097067009, 'SEX', '性别', '', 'Y', 'ENABLE', 4, '2019-03-14 17:09:43', 1, NULL, NULL);
INSERT INTO "public"."sys_dict_type" VALUES (1423160140530053120, 'DICTTYPE', '字典类型', '测试', 'N', 'ENABLE', NULL, '2021-08-05 13:53:13.196', 1, '2021-08-05 13:53:13.196', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1160532886713155587, 'MEDIA_TYPE', '通知类型', '通知类型', 'N', 'ENABLE', 1, '2019-08-11 20:46:17', 1, '2019-08-11 20:46:23', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1160532886713155586, 'DEVICE_TYPE', '设备类型', '设备类型', 'Y', 'ENABLE', 1, '2019-08-11 20:46:17', 1, '2019-08-11 20:46:23', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1160532886713155588, 'ATTR_TYPE', '产品属性类型', '产品属性类型', 'Y', 'ENABLE', 1, '2019-08-11 20:46:17', 1, '2019-08-11 20:46:23', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1160532886713155589, 'UNITS', '产品属性单位', '产品属性单位', 'Y', 'ENABLE', 1, '2019-08-11 20:46:17', 1, '2019-08-11 20:46:23', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1160532886713155590, 'DATA_TYPE', '产品属性值类型', '产品属性值类型', 'Y', 'ENABLE', 1, '2019-08-11 20:46:17', 1, '2019-08-11 20:46:23', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1160532886713155591, 'DATA_PRE_TYPE', '数据预处理方法名称', '数据预处理方法名称', 'Y', 'ENABLE', 1, '2019-08-11 20:46:17', 1, '2019-08-11 20:46:23', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1142859918022911591, 'EXECUTE_TYPE', '服务执行方式', '服务执行方式', 'N', 'ENABLE', 1, NULL, NULL, '2021-09-10 15:56:14.127', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1437348629152780288, 'SShe06pqU', '三石TestcKA3qIU', '描述Test@123', 'N', 'ENABLE', NULL, '2021-09-13 17:33:12.493', 1, '2021-09-13 17:33:12.554', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1428599180229115904, 'WHETHER', '是否', '是否', 'N', 'ENABLE', NULL, '2021-08-20 14:06:01.311', 1, '2021-08-20 14:06:01.311', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1428599180229115905, 'EVENT_LEVEL', '告警级别', '告警级别', 'N', 'ENABLE', NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_login_log";
CREATE TABLE "public"."sys_login_log" (
  "log_name" varchar(255) COLLATE "pg_catalog"."default",
  "user_id" int8,
  "succeed" varchar(255) COLLATE "pg_catalog"."default",
  "message" text COLLATE "pg_catalog"."default",
  "ip_address" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "login_log_id" int8 NOT NULL
)
;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
INSERT INTO "public"."sys_login_log" VALUES ('登录日志', 1, '成功', NULL, '172.16.3.33', '2021-09-23 11:35:36.879', 1440882516655042560);
INSERT INTO "public"."sys_login_log" VALUES ('登录日志', 1, '成功', NULL, '172.16.3.25', '2021-09-23 11:36:56.394', 1440882849955409920);
INSERT INTO "public"."sys_login_log" VALUES ('退出日志', 1, '成功', NULL, '172.16.3.25', '2021-09-23 11:42:26.181', 1440884233178148864);
INSERT INTO "public"."sys_login_log" VALUES ('登录日志', 117, '成功', NULL, '172.16.3.25', '2021-09-23 11:42:32.517', 1440884259753259008);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_menu";
CREATE TABLE "public"."sys_menu" (
  "menu_id" int8 NOT NULL,
  "code" varchar(255) COLLATE "pg_catalog"."default",
  "pcode" varchar(255) COLLATE "pg_catalog"."default",
  "pcodes" varchar(255) COLLATE "pg_catalog"."default",
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "icon" varchar(255) COLLATE "pg_catalog"."default",
  "url" varchar(255) COLLATE "pg_catalog"."default",
  "sort" int2,
  "levels" int2,
  "menu_flag" varchar(32) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "status" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "create_user" int8,
  "update_user" int8
)
;
COMMENT ON COLUMN "public"."sys_menu"."menu_id" IS '菜单ID';
COMMENT ON COLUMN "public"."sys_menu"."code" IS '菜单编号';
COMMENT ON COLUMN "public"."sys_menu"."pcode" IS '菜单父编号';
COMMENT ON COLUMN "public"."sys_menu"."pcodes" IS '当前菜单的所有父菜单编号';
COMMENT ON COLUMN "public"."sys_menu"."name" IS '菜单名称';
COMMENT ON COLUMN "public"."sys_menu"."icon" IS '菜单图标';
COMMENT ON COLUMN "public"."sys_menu"."url" IS 'URL地址';
COMMENT ON COLUMN "public"."sys_menu"."sort" IS '菜单排序号';
COMMENT ON COLUMN "public"."sys_menu"."levels" IS '菜单层级';
COMMENT ON COLUMN "public"."sys_menu"."menu_flag" IS '是否是菜单(字典)';
COMMENT ON COLUMN "public"."sys_menu"."remark" IS '备注';
COMMENT ON COLUMN "public"."sys_menu"."status" IS '菜单状态(字典)';
COMMENT ON COLUMN "public"."sys_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_menu"."update_time" IS '修改时间';
COMMENT ON COLUMN "public"."sys_menu"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sys_menu"."update_user" IS '修改人';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO "public"."sys_menu" VALUES (107, 'mgr_add', 'mgr', '[0],[system],[mgr],', '添加用户', NULL, '/mgr/add', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (108, 'mgr_edit', 'mgr', '[0],[system],[mgr],', '修改用户', NULL, '/mgr/edit', 2, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (109, 'mgr_delete', 'mgr', '[0],[system],[mgr],', '删除用户', NULL, '/mgr/delete', 3, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (110, 'mgr_reset', 'mgr', '[0],[system],[mgr],', '重置密码', NULL, '/mgr/reset', 4, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (113, 'mgr_setRole', 'mgr', '[0],[system],[mgr],', '分配角色', NULL, '/mgr/setRole', 7, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (115, 'role_add', 'role', '[0],[system],[role],', '添加角色', NULL, '/role/add', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (116, 'role_edit', 'role', '[0],[system],[role],', '修改角色', NULL, '/role/edit', 2, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (117, 'role_remove', 'role', '[0],[system],[role],', '删除角色', NULL, '/role/remove', 3, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (130, 'druid', 'system', '[0],[system],', '监控管理', '', '/druid', 80, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:50:06', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (134, 'log_clean', 'log', '[0],[system],[log],', '清空日志', NULL, '/log/delLog', 3, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:39', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (138, 'dict_add', 'dict', '[0],[system],[dict],', '添加字典', NULL, '/dictType/addItem', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (139, 'dict_update', 'dict', '[0],[system],[dict],', '修改字典', NULL, '/dictType/editItem', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (140, 'dict_delete', 'dict', '[0],[system],[dict],', '删除字典', NULL, '/dictType/delete', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (128, 'businessLog', 'log', '[0],[log],', '业务日志', '', '/log/businessLog', 70, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:39', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (181, 'product_type', 'product_mgr', '[0],[product_mgr],', '产品分类', NULL, '/productMgr/productType', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (151, 'menu_list', 'menu', '[0],[system],[menu],', '菜单列表', '', '/menu/list', 5, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:25', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (156, 'dict_list', 'dict', '[0],[system],[dict],', '字典列表', '', '/dict/list', 5, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (157, 'dict_detail', 'dict', '[0],[system],[dict],', '字典详情', '', '/dict/detail', 6, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (158, 'log_list', 'log', '[0],[system],[log],', '日志列表', '', '/log/list', 2, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:39', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (159, 'log_detail', 'log', '[0],[system],[log],', '日志详情', '', '/log/detail', 3, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:39', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (160, 'del_login_log', 'loginLog', '[0],[system],[loginLog],', '清空登录日志', '', '/loginLog/delLoginLog', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:29', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (161, 'login_log_list', 'loginLog', '[0],[system],[loginLog],', '登录日志列表', '', '/loginLog/list', 2, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:29', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (133, 'loginLog', 'log', '[0],[log],', '登录日志', '', '/log/loginLog', 60, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:49:29', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (164, 'role_list', 'role', '[0],[system],[role],', '角色列表', '', '/role/list', 7, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (165, 'to_assign_role', 'mgr', '[0],[system],[mgr],', '分配角色跳转', '', '/mgr/role_assign', 8, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (166, 'to_user_edit', 'mgr', '[0],[system],[mgr],', '编辑用户跳转', '', '/mgr/user_edit', 9, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (167, 'mgr_list', 'mgr', '[0],[system],[mgr],', '用户列表', '', '/mgr/list', 10, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (114, 'role', 'system', '[0],[system],', '角色管理', '', '/system/role', 20, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (106, 'mgr', 'system', '[0],[system],', '用户管理', '', '/system/user', 10, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (111, 'mgr_freeze', 'mgr', '[0],[system],[mgr],', '冻结用户', NULL, '/mgr/freeze', 5, 3, 'N', NULL, 'DISABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (112, 'mgr_unfreeze', 'mgr', '[0],[system],[mgr],', '解除冻结用户', NULL, '/mgr/unfreeze', 6, 3, 'N', NULL, 'DISABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (118, 'role_setAuthority', 'role', '[0],[system],[role],', '配置菜单', NULL, '/role/setAuthority', 4, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (119, 'menu', 'system', '[0],[system],', '菜单管理', '', '/menu', 50, 2, 'Y', NULL, 'DISABLE', NULL, '2019-06-30 13:48:25', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (120, 'menu_add', 'menu', '[0],[system],[menu],', '添加菜单', NULL, '/menu/add', 1, 3, 'N', NULL, 'DISABLE', NULL, '2019-06-30 13:48:25', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (121, 'menu_edit', 'menu', '[0],[system],[menu],', '修改菜单', NULL, '/menu/edit', 2, 3, 'N', NULL, 'DISABLE', NULL, '2019-06-30 13:48:25', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (122, 'menu_remove', 'menu', '[0],[system],[menu],', '删除菜单', NULL, '/menu/remove', 3, 3, 'N', NULL, 'DISABLE', NULL, '2019-06-30 13:48:25', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (177, 'dev', 'dev_mgr', '[0],[dev_mgr],', '设备', NULL, '/deviceMgr/device', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (178, 'dev_group', 'dev_mgr', '[0],[dev_mgr],', '设备组', NULL, '/deviceMgr/deviceGroup', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (171, 'product_mgr', '0', '[0],', '产品管理', NULL, '/productMgr', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (132, 'dict', 'system', '[0],[system],', '字典管理', '', '/system/dictType', 40, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (186, 'sysParam', 'system', '[0],[system],', '系统参数', '', '/system/sysParam', 10, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (180, 'product', 'product_mgr', '[0],[product_mgr],', '产品', NULL, '/productMgr/product', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (168, 'usrGrp', 'system', '[0],[system],', '用户组管理', NULL, '/system/userGroup', 9, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (184, 'log', '0', '[0]', '日志管理', NULL, '/log', 4, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (105, 'system', '0', '[0],', '系统管理', 'layui-icon layui-icon-set', '/system', 20, 1, 'Y', NULL, 'ENABLE', NULL, '2019-03-29 16:32:27', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (170, 'dev_mgr', '0', '[0],', '设备管理', NULL, '/deviceMgr', 2, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (185, 'product_detail', 'product', '[0],[product_mgr],[product],', '产品详情', NULL, '/productMgr/product/detail', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (187, 'media', 'system', '[0],[system],', '通知配置', '', '/system/media', 80, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:50:06', NULL, 1);
INSERT INTO "public"."sys_menu" VALUES (189, 'home', 'analyse', '[0],[analyse],', '全局概览', NULL, '/analyse/home', 1, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (188, 'detail', 'dev', '[0],[dev_mgr],[dev],', '设备详情', '', '/deviceMgr/device/detail', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (179, 'alarmList', 'alarm', '[0],[alarm],', '告警记录', NULL, '/alarm/alarmList', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (190, 'latest', 'analyse', '[0],[analyse],', '最新数据', NULL, '/analyse/latest', 2, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (169, 'analyse', '0', '[0],', '统计分析', NULL, '/analyse', 1, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (191, 'alarmAnalyse', 'alarm', '[0],[alarm],', '告警分析', NULL, '/alarm/analyse', 3, 2, 'Y', NULL, 'DISABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (174, 'auth', '0', '[0],', '平台授权', NULL, '/auth', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (173, 'view', '0', '[0],', '可视化', NULL, '/view', 3, 1, 'Y', NULL, 'DISABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (176, 'dev_log', 'analyse', '[0],[analyse],', '设备日志', NULL, '/analyse/devLog', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (183, 'grafana', 'view', '[0],[view],', 'Grafana配置', NULL, '/view/grafana', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (192, 'alarm', '0', '[0],', '告警管理', NULL, '/alarm', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (194, 'rule', '0', '[0],', '规则引擎', NULL, '/rule', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (196, 'api', 'dev_mgr', '[0],[dev_mgr],', '设备调试', NULL, '/deviceMgr/api', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (197, 'about', '0', '[0],', '关于我们', NULL, '/about', 2, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (195, 'nodeRed', 'rule', '[0],[rule],', 'Node-Red', NULL, '/rule/nodeRed', 3, 2, 'Y', NULL, 'DISABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (193, 'scene ', 'rule', '[0],[rule],', '场景联动', NULL, '/rule/scene ', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (198, 'monitor', 'analyse', '[0],[analyse],', '平台监控', NULL, '/analyse/monitor', 4, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_menu" VALUES (199, 'weakMonitor', 'analyse', '[0],[analyse],', '弱电间监控', NULL, '/analyse/weakMonitor', 5, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_operation_log";
CREATE TABLE "public"."sys_operation_log" (
  "operation_log_id" int8 NOT NULL,
  "log_type" varchar(32) COLLATE "pg_catalog"."default",
  "log_name" varchar(255) COLLATE "pg_catalog"."default",
  "user_id" int8,
  "class_name" varchar(255) COLLATE "pg_catalog"."default",
  "method" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "succeed" varchar(32) COLLATE "pg_catalog"."default",
  "message" text COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."sys_operation_log"."operation_log_id" IS '操作日志ID';
COMMENT ON COLUMN "public"."sys_operation_log"."log_type" IS '日志类型【字典】';
COMMENT ON COLUMN "public"."sys_operation_log"."log_name" IS '日志名称';
COMMENT ON COLUMN "public"."sys_operation_log"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."sys_operation_log"."class_name" IS '类名称';
COMMENT ON COLUMN "public"."sys_operation_log"."method" IS '方法名称';
COMMENT ON COLUMN "public"."sys_operation_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_operation_log"."succeed" IS '是否成功【字典】';
COMMENT ON COLUMN "public"."sys_operation_log"."message" IS '备注';

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
INSERT INTO "public"."sys_operation_log" VALUES (1440880052786688000, '业务日志', '新增产品分类', 1, 'com.zmops.iot.web.product.controller.ProductTypeController', 'create', '2021-09-23 11:25:49.498', '成功', '');
INSERT INTO "public"."sys_operation_log" VALUES (1440880404000927744, '业务日志', '创建设备组', 1, 'com.zmops.iot.web.device.controller.DeviceGroupController', 'createDeviceGroup', '2021-09-23 11:27:13.234', '成功', '');
INSERT INTO "public"."sys_operation_log" VALUES (1440883048178216960, '异常日志', '', 1, NULL, NULL, '2021-09-23 11:37:43.655', '失败', 'com.zmops.iot.model.exception.ZbxApiException: User with username "Admin" already exists.
	at com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor.onSuccess(JsonBodyBuildInterceptor.java:78)
	at com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor.onSuccess(JsonBodyBuildInterceptor.java:28)
	at com.dtflys.forest.interceptor.InterceptorChain.onSuccess(InterceptorChain.java:64)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSuccess(MethodLifeCycleHandler.java:111)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSyncWithException(MethodLifeCycleHandler.java:55)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSync(MethodLifeCycleHandler.java:46)
	at com.dtflys.forest.backend.AbstractBackendResponseHandler.handleSync(AbstractBackendResponseHandler.java:36)
	at com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler.handleSync(OkHttp3ResponseHandler.java:29)
	at com.dtflys.forest.backend.okhttp3.executor.AbstractOkHttp3Executor.execute(AbstractOkHttp3Executor.java:260)
	at com.dtflys.forest.backend.okhttp3.executor.AbstractOkHttp3Executor.execute(AbstractOkHttp3Executor.java:288)
	at com.dtflys.forest.http.ForestRequest.execute(ForestRequest.java:1731)
	at com.dtflys.forest.http.ForestRequest.execute(ForestRequest.java:1748)
	at com.dtflys.forest.reflection.ForestMethod.invoke(ForestMethod.java:1230)
	at com.dtflys.forest.proxy.InterfaceProxyHandler.invoke(InterfaceProxyHandler.java:150)
	at com.sun.proxy.TProxy127.userAdd(Unknown Source)
	at com.zmops.iot.web.sys.service.SysUserService.createUser(SysUserService.java:132)
	at com.zmops.iot.web.sys.service.SysUserServiceTTFastClassBySpringCGLIBTT5b7cfc4d.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:367)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:118)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxyTDynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.zmops.iot.web.sys.service.SysUserServiceTTEnhancerBySpringCGLIBTT9bb24deb.createUser(<generated>)
	at com.zmops.iot.web.sys.controller.SysUserController.createUser(SysUserController.java:51)
	at com.zmops.iot.web.sys.controller.SysUserControllerTTFastClassBySpringCGLIBTT2e326b39.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint.proceed(MethodInvocationProceedingJoinPoint.java:88)
	at com.zmops.iot.web.log.aop.LogAop.recordSysLog(LogAop.java:54)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethodWithGivenArgs(AbstractAspectJAdvice.java:644)
	at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethod(AbstractAspectJAdvice.java:633)
	at org.springframework.aop.aspectj.AspectJAroundAdvice.invoke(AspectJAroundAdvice.java:70)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:175)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:95)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxyTDynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.zmops.iot.web.sys.controller.SysUserControllerTTEnhancerBySpringCGLIBTT2cad82eb.createUser(<generated>)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:105)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:878)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:792)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:517)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:584)
	at io.undertow.servlet.handlers.ServletHandler.handleRequest(ServletHandler.java:74)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:129)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:113)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:320)
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:126)
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:90)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:118)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:137)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:111)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:158)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at com.zmops.iot.core.auth.filter.JwtAuthorizationTokenFilter.doFilterInternal(JwtAuthorizationTokenFilter.java:77)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:92)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:92)
	at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:77)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:105)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:56)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:215)
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:178)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:358)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:271)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:97)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at io.undertow.servlet.handlers.FilterHandler.handleRequest(FilterHandler.java:84)
	at io.undertow.servlet.handlers.security.ServletSecurityRoleHandler.handleRequest(ServletSecurityRoleHandler.java:62)
	at io.undertow.servlet.handlers.ServletChainT1.handleRequest(ServletChain.java:68)
	at io.undertow.servlet.handlers.ServletDispatchingHandler.handleRequest(ServletDispatchingHandler.java:36)
	at io.undertow.servlet.handlers.RedirectDirHandler.handleRequest(RedirectDirHandler.java:68)
	at io.undertow.servlet.handlers.security.SSLInformationAssociationHandler.handleRequest(SSLInformationAssociationHandler.java:111)
	at io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler.handleRequest(ServletAuthenticationCallHandler.java:57)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.security.handlers.AbstractConfidentialityHandler.handleRequest(AbstractConfidentialityHandler.java:46)
	at io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler.handleRequest(ServletConfidentialityConstraintHandler.java:64)
	at io.undertow.security.handlers.AuthenticationMechanismsHandler.handleRequest(AuthenticationMechanismsHandler.java:60)
	at io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler.handleRequest(CachedAuthenticatedSessionHandler.java:77)
	at io.undertow.security.handlers.AbstractSecurityContextAssociationHandler.handleRequest(AbstractSecurityContextAssociationHandler.java:43)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.servlet.handlers.ServletInitialHandler.handleFirstRequest(ServletInitialHandler.java:269)
	at io.undertow.servlet.handlers.ServletInitialHandler.accessT100(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandlerT2.call(ServletInitialHandler.java:133)
	at io.undertow.servlet.handlers.ServletInitialHandlerT2.call(ServletInitialHandler.java:130)
	at io.undertow.servlet.core.ServletRequestContextThreadSetupActionT1.call(ServletRequestContextThreadSetupAction.java:48)
	at io.undertow.servlet.core.ContextClassLoaderSetupActionT1.call(ContextClassLoaderSetupAction.java:43)
	at io.undertow.servlet.handlers.ServletInitialHandler.dispatchRequest(ServletInitialHandler.java:249)
	at io.undertow.servlet.handlers.ServletInitialHandler.accessT000(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandlerT1.handleRequest(ServletInitialHandler.java:99)
	at io.undertow.server.Connectors.executeRootHandler(Connectors.java:390)
	at io.undertow.server.HttpServerExchangeT1.run(HttpServerExchange.java:836)
	at org.jboss.threads.ContextClassLoaderSavingRunnable.run(ContextClassLoaderSavingRunnable.java:35)
	at org.jboss.threads.EnhancedQueueExecutor.safeRun(EnhancedQueueExecutor.java:2019)
	at org.jboss.threads.EnhancedQueueExecutorTThreadBody.doRunTask(EnhancedQueueExecutor.java:1558)
	at org.jboss.threads.EnhancedQueueExecutorTThreadBody.run(EnhancedQueueExecutor.java:1449)
	at java.lang.Thread.run(Thread.java:748)
');
INSERT INTO "public"."sys_operation_log" VALUES (1440883284019736576, '异常日志', '', 1, NULL, NULL, '2021-09-23 11:38:39.884', '失败', 'com.zmops.iot.model.exception.ZbxApiException: User group with ID "107" is not available.
	at com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor.onSuccess(JsonBodyBuildInterceptor.java:78)
	at com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor.onSuccess(JsonBodyBuildInterceptor.java:28)
	at com.dtflys.forest.interceptor.InterceptorChain.onSuccess(InterceptorChain.java:64)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSuccess(MethodLifeCycleHandler.java:111)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSyncWithException(MethodLifeCycleHandler.java:55)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSync(MethodLifeCycleHandler.java:46)
	at com.dtflys.forest.backend.AbstractBackendResponseHandler.handleSync(AbstractBackendResponseHandler.java:36)
	at com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler.handleSync(OkHttp3ResponseHandler.java:29)
	at com.dtflys.forest.backend.okhttp3.executor.AbstractOkHttp3Executor.execute(AbstractOkHttp3Executor.java:260)
	at com.dtflys.forest.backend.okhttp3.executor.AbstractOkHttp3Executor.execute(AbstractOkHttp3Executor.java:288)
	at com.dtflys.forest.http.ForestRequest.execute(ForestRequest.java:1731)
	at com.dtflys.forest.http.ForestRequest.execute(ForestRequest.java:1748)
	at com.dtflys.forest.reflection.ForestMethod.invoke(ForestMethod.java:1230)
	at com.dtflys.forest.proxy.InterfaceProxyHandler.invoke(InterfaceProxyHandler.java:150)
	at com.sun.proxy.TProxy127.userAdd(Unknown Source)
	at com.zmops.iot.web.sys.service.SysUserService.createUser(SysUserService.java:132)
	at com.zmops.iot.web.sys.service.SysUserServiceTTFastClassBySpringCGLIBTT5b7cfc4d.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:367)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:118)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxyTDynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.zmops.iot.web.sys.service.SysUserServiceTTEnhancerBySpringCGLIBTT9bb24deb.createUser(<generated>)
	at com.zmops.iot.web.sys.controller.SysUserController.createUser(SysUserController.java:51)
	at com.zmops.iot.web.sys.controller.SysUserControllerTTFastClassBySpringCGLIBTT2e326b39.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint.proceed(MethodInvocationProceedingJoinPoint.java:88)
	at com.zmops.iot.web.log.aop.LogAop.recordSysLog(LogAop.java:54)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethodWithGivenArgs(AbstractAspectJAdvice.java:644)
	at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethod(AbstractAspectJAdvice.java:633)
	at org.springframework.aop.aspectj.AspectJAroundAdvice.invoke(AspectJAroundAdvice.java:70)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:175)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:95)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxyTDynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.zmops.iot.web.sys.controller.SysUserControllerTTEnhancerBySpringCGLIBTT2cad82eb.createUser(<generated>)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:105)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:878)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:792)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:517)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:584)
	at io.undertow.servlet.handlers.ServletHandler.handleRequest(ServletHandler.java:74)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:129)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:113)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:320)
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:126)
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:90)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:118)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:137)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:111)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:158)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at com.zmops.iot.core.auth.filter.JwtAuthorizationTokenFilter.doFilterInternal(JwtAuthorizationTokenFilter.java:77)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:92)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:92)
	at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:77)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:105)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:56)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:215)
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:178)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:358)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:271)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:97)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at io.undertow.servlet.handlers.FilterHandler.handleRequest(FilterHandler.java:84)
	at io.undertow.servlet.handlers.security.ServletSecurityRoleHandler.handleRequest(ServletSecurityRoleHandler.java:62)
	at io.undertow.servlet.handlers.ServletChainT1.handleRequest(ServletChain.java:68)
	at io.undertow.servlet.handlers.ServletDispatchingHandler.handleRequest(ServletDispatchingHandler.java:36)
	at io.undertow.servlet.handlers.RedirectDirHandler.handleRequest(RedirectDirHandler.java:68)
	at io.undertow.servlet.handlers.security.SSLInformationAssociationHandler.handleRequest(SSLInformationAssociationHandler.java:111)
	at io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler.handleRequest(ServletAuthenticationCallHandler.java:57)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.security.handlers.AbstractConfidentialityHandler.handleRequest(AbstractConfidentialityHandler.java:46)
	at io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler.handleRequest(ServletConfidentialityConstraintHandler.java:64)
	at io.undertow.security.handlers.AuthenticationMechanismsHandler.handleRequest(AuthenticationMechanismsHandler.java:60)
	at io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler.handleRequest(CachedAuthenticatedSessionHandler.java:77)
	at io.undertow.security.handlers.AbstractSecurityContextAssociationHandler.handleRequest(AbstractSecurityContextAssociationHandler.java:43)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.servlet.handlers.ServletInitialHandler.handleFirstRequest(ServletInitialHandler.java:269)
	at io.undertow.servlet.handlers.ServletInitialHandler.accessT100(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandlerT2.call(ServletInitialHandler.java:133)
	at io.undertow.servlet.handlers.ServletInitialHandlerT2.call(ServletInitialHandler.java:130)
	at io.undertow.servlet.core.ServletRequestContextThreadSetupActionT1.call(ServletRequestContextThreadSetupAction.java:48)
	at io.undertow.servlet.core.ContextClassLoaderSetupActionT1.call(ContextClassLoaderSetupAction.java:43)
	at io.undertow.servlet.handlers.ServletInitialHandler.dispatchRequest(ServletInitialHandler.java:249)
	at io.undertow.servlet.handlers.ServletInitialHandler.accessT000(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandlerT1.handleRequest(ServletInitialHandler.java:99)
	at io.undertow.server.Connectors.executeRootHandler(Connectors.java:390)
	at io.undertow.server.HttpServerExchangeT1.run(HttpServerExchange.java:836)
	at org.jboss.threads.ContextClassLoaderSavingRunnable.run(ContextClassLoaderSavingRunnable.java:35)
	at org.jboss.threads.EnhancedQueueExecutor.safeRun(EnhancedQueueExecutor.java:2019)
	at org.jboss.threads.EnhancedQueueExecutorTThreadBody.doRunTask(EnhancedQueueExecutor.java:1558)
	at org.jboss.threads.EnhancedQueueExecutorTThreadBody.run(EnhancedQueueExecutor.java:1449)
	at java.lang.Thread.run(Thread.java:748)
');
INSERT INTO "public"."sys_operation_log" VALUES (1440883334531739648, '异常日志', '', 1, NULL, NULL, '2021-09-23 11:38:51.926', '失败', 'com.zmops.iot.model.exception.ZbxApiException: User group with ID "107" is not available.
	at com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor.onSuccess(JsonBodyBuildInterceptor.java:78)
	at com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor.onSuccess(JsonBodyBuildInterceptor.java:28)
	at com.dtflys.forest.interceptor.InterceptorChain.onSuccess(InterceptorChain.java:64)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSuccess(MethodLifeCycleHandler.java:111)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSyncWithException(MethodLifeCycleHandler.java:55)
	at com.dtflys.forest.reflection.MethodLifeCycleHandler.handleSync(MethodLifeCycleHandler.java:46)
	at com.dtflys.forest.backend.AbstractBackendResponseHandler.handleSync(AbstractBackendResponseHandler.java:36)
	at com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler.handleSync(OkHttp3ResponseHandler.java:29)
	at com.dtflys.forest.backend.okhttp3.executor.AbstractOkHttp3Executor.execute(AbstractOkHttp3Executor.java:260)
	at com.dtflys.forest.backend.okhttp3.executor.AbstractOkHttp3Executor.execute(AbstractOkHttp3Executor.java:288)
	at com.dtflys.forest.http.ForestRequest.execute(ForestRequest.java:1731)
	at com.dtflys.forest.http.ForestRequest.execute(ForestRequest.java:1748)
	at com.dtflys.forest.reflection.ForestMethod.invoke(ForestMethod.java:1230)
	at com.dtflys.forest.proxy.InterfaceProxyHandler.invoke(InterfaceProxyHandler.java:150)
	at com.sun.proxy.TProxy127.userAdd(Unknown Source)
	at com.zmops.iot.web.sys.service.SysUserService.createUser(SysUserService.java:132)
	at com.zmops.iot.web.sys.service.SysUserServiceTTFastClassBySpringCGLIBTT5b7cfc4d.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:367)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:118)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxyTDynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.zmops.iot.web.sys.service.SysUserServiceTTEnhancerBySpringCGLIBTT9bb24deb.createUser(<generated>)
	at com.zmops.iot.web.sys.controller.SysUserController.createUser(SysUserController.java:51)
	at com.zmops.iot.web.sys.controller.SysUserControllerTTFastClassBySpringCGLIBTT2e326b39.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint.proceed(MethodInvocationProceedingJoinPoint.java:88)
	at com.zmops.iot.web.log.aop.LogAop.recordSysLog(LogAop.java:54)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethodWithGivenArgs(AbstractAspectJAdvice.java:644)
	at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethod(AbstractAspectJAdvice.java:633)
	at org.springframework.aop.aspectj.AspectJAroundAdvice.invoke(AspectJAroundAdvice.java:70)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:175)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:95)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxyTCglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxyTDynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.zmops.iot.web.sys.controller.SysUserControllerTTEnhancerBySpringCGLIBTT2cad82eb.createUser(<generated>)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:105)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:878)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:792)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:517)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:584)
	at io.undertow.servlet.handlers.ServletHandler.handleRequest(ServletHandler.java:74)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:129)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:113)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:320)
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:126)
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:90)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:118)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:137)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:111)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:158)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at com.zmops.iot.core.auth.filter.JwtAuthorizationTokenFilter.doFilterInternal(JwtAuthorizationTokenFilter.java:77)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.web.filter.CorsFilter.doFilterInternal(CorsFilter.java:92)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:92)
	at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:77)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:105)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:56)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.springframework.security.web.FilterChainProxyTVirtualFilterChain.doFilter(FilterChainProxy.java:334)
	at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:215)
	at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:178)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:358)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:271)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:97)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandlerTFilterChainImpl.doFilter(FilterHandler.java:131)
	at io.undertow.servlet.handlers.FilterHandler.handleRequest(FilterHandler.java:84)
	at io.undertow.servlet.handlers.security.ServletSecurityRoleHandler.handleRequest(ServletSecurityRoleHandler.java:62)
	at io.undertow.servlet.handlers.ServletChainT1.handleRequest(ServletChain.java:68)
	at io.undertow.servlet.handlers.ServletDispatchingHandler.handleRequest(ServletDispatchingHandler.java:36)
	at io.undertow.servlet.handlers.RedirectDirHandler.handleRequest(RedirectDirHandler.java:68)
	at io.undertow.servlet.handlers.security.SSLInformationAssociationHandler.handleRequest(SSLInformationAssociationHandler.java:111)
	at io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler.handleRequest(ServletAuthenticationCallHandler.java:57)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.security.handlers.AbstractConfidentialityHandler.handleRequest(AbstractConfidentialityHandler.java:46)
	at io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler.handleRequest(ServletConfidentialityConstraintHandler.java:64)
	at io.undertow.security.handlers.AuthenticationMechanismsHandler.handleRequest(AuthenticationMechanismsHandler.java:60)
	at io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler.handleRequest(CachedAuthenticatedSessionHandler.java:77)
	at io.undertow.security.handlers.AbstractSecurityContextAssociationHandler.handleRequest(AbstractSecurityContextAssociationHandler.java:43)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.servlet.handlers.ServletInitialHandler.handleFirstRequest(ServletInitialHandler.java:269)
	at io.undertow.servlet.handlers.ServletInitialHandler.accessT100(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandlerT2.call(ServletInitialHandler.java:133)
	at io.undertow.servlet.handlers.ServletInitialHandlerT2.call(ServletInitialHandler.java:130)
	at io.undertow.servlet.core.ServletRequestContextThreadSetupActionT1.call(ServletRequestContextThreadSetupAction.java:48)
	at io.undertow.servlet.core.ContextClassLoaderSetupActionT1.call(ContextClassLoaderSetupAction.java:43)
	at io.undertow.servlet.handlers.ServletInitialHandler.dispatchRequest(ServletInitialHandler.java:249)
	at io.undertow.servlet.handlers.ServletInitialHandler.accessT000(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandlerT1.handleRequest(ServletInitialHandler.java:99)
	at io.undertow.server.Connectors.executeRootHandler(Connectors.java:390)
	at io.undertow.server.HttpServerExchangeT1.run(HttpServerExchange.java:836)
	at org.jboss.threads.ContextClassLoaderSavingRunnable.run(ContextClassLoaderSavingRunnable.java:35)
	at org.jboss.threads.EnhancedQueueExecutor.safeRun(EnhancedQueueExecutor.java:2019)
	at org.jboss.threads.EnhancedQueueExecutorTThreadBody.doRunTask(EnhancedQueueExecutor.java:1558)
	at org.jboss.threads.EnhancedQueueExecutorTThreadBody.run(EnhancedQueueExecutor.java:1449)
	at java.lang.Thread.run(Thread.java:748)
');
INSERT INTO "public"."sys_operation_log" VALUES (1440883824174788608, '业务日志', '创建用户', 1, 'com.zmops.iot.web.sys.controller.SysUserController', 'createUser', '2021-09-23 11:40:48.667', '成功', '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role";
CREATE TABLE "public"."sys_role" (
  "role_id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "sort" int2,
  "create_user" int8,
  "update_user" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6)
)
;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO "public"."sys_role" VALUES (1, '超级管理员', 'administrator', 1, NULL, 1, NULL, '2021-08-17 10:57:36.612');
INSERT INTO "public"."sys_role" VALUES (1432233337871355904, '测试', '', NULL, 1, 1, '2021-08-30 14:46:52.014', '2021-08-30 14:46:52.014');
INSERT INTO "public"."sys_role" VALUES (1437618920839958528, '三石TestgOtXD2', NULL, NULL, 1, 1, '2021-09-14 11:27:15.053', '2021-09-14 11:27:15.053');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_menu";
CREATE TABLE "public"."sys_role_menu" (
  "role_id" int8,
  "menu_id" int8
)
;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO "public"."sys_role_menu" VALUES (1423482240088260608, 1);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 175);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1423549393533521920, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 175);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1423551494242271232, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 175);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1422463279817150464, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1423551559929266176, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 175);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1423551670717612032, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 175);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1423552915037523968, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 175);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1423840436349898752, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 185);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 186);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 187);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 188);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 189);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 190);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 191);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 192);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 193);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 194);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 195);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 196);
INSERT INTO "public"."sys_role_menu" VALUES (1428542898655903744, 197);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 185);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 186);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 187);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 188);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 189);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 190);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 191);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 192);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 193);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 194);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 195);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 196);
INSERT INTO "public"."sys_role_menu" VALUES (1428552324309762048, 197);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 185);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 186);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 187);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 188);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 189);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 190);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 191);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 192);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 193);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 194);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 195);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 196);
INSERT INTO "public"."sys_role_menu" VALUES (1428552760613847040, 197);
INSERT INTO "public"."sys_role_menu" VALUES (1428553795793240064, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1428555085013237760, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1428555388848619520, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1428559148366872576, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1432233337871355904, 189);
INSERT INTO "public"."sys_role_menu" VALUES (1432233337871355904, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1, 189);
INSERT INTO "public"."sys_role_menu" VALUES (1, 190);
INSERT INTO "public"."sys_role_menu" VALUES (1, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1, 197);
INSERT INTO "public"."sys_role_menu" VALUES (1, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1, 188);
INSERT INTO "public"."sys_role_menu" VALUES (1, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1, 196);
INSERT INTO "public"."sys_role_menu" VALUES (1, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1, 185);
INSERT INTO "public"."sys_role_menu" VALUES (1, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1, 192);
INSERT INTO "public"."sys_role_menu" VALUES (1, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1, 194);
INSERT INTO "public"."sys_role_menu" VALUES (1, 193);
INSERT INTO "public"."sys_role_menu" VALUES (1, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1, 186);
INSERT INTO "public"."sys_role_menu" VALUES (1, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1, 107);
INSERT INTO "public"."sys_role_menu" VALUES (1, 108);
INSERT INTO "public"."sys_role_menu" VALUES (1, 109);
INSERT INTO "public"."sys_role_menu" VALUES (1, 110);
INSERT INTO "public"."sys_role_menu" VALUES (1, 113);
INSERT INTO "public"."sys_role_menu" VALUES (1, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1, 115);
INSERT INTO "public"."sys_role_menu" VALUES (1, 116);
INSERT INTO "public"."sys_role_menu" VALUES (1, 117);
INSERT INTO "public"."sys_role_menu" VALUES (1, 118);
INSERT INTO "public"."sys_role_menu" VALUES (1, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1, 187);
INSERT INTO "public"."sys_role_menu" VALUES (1437255079924846592, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1437255079924846592, 186);
INSERT INTO "public"."sys_role_menu" VALUES (1437255079924846592, 187);
INSERT INTO "public"."sys_role_menu" VALUES (1437255079924846592, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1, 198);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user";
CREATE TABLE "public"."sys_user" (
  "account" varchar(255) COLLATE "pg_catalog"."default",
  "password" varchar(255) COLLATE "pg_catalog"."default",
  "salt" varchar(255) COLLATE "pg_catalog"."default",
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "email" varchar(255) COLLATE "pg_catalog"."default",
  "phone" varchar(255) COLLATE "pg_catalog"."default",
  "role_id" int8,
  "user_group_id" int8,
  "status" varchar(32) COLLATE "pg_catalog"."default",
  "create_user" int8,
  "update_user" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "user_id" int8 NOT NULL DEFAULT nextval('sys_user_user_id_seq'::regclass),
  "zbx_token" varchar(255) COLLATE "pg_catalog"."default",
  "zbx_id" varchar(64) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."sys_user"."account" IS '登陆账号名';
COMMENT ON COLUMN "public"."sys_user"."password" IS '密码';
COMMENT ON COLUMN "public"."sys_user"."salt" IS '密码盐';
COMMENT ON COLUMN "public"."sys_user"."name" IS '用户名';
COMMENT ON COLUMN "public"."sys_user"."email" IS '电子邮箱地址';
COMMENT ON COLUMN "public"."sys_user"."phone" IS '手机号';
COMMENT ON COLUMN "public"."sys_user"."role_id" IS '角色ID';
COMMENT ON COLUMN "public"."sys_user"."user_group_id" IS '用户组ID';
COMMENT ON COLUMN "public"."sys_user"."status" IS '状态';
COMMENT ON COLUMN "public"."sys_user"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."sys_user"."update_user" IS '修改人';
COMMENT ON COLUMN "public"."sys_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_user"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."sys_user"."zbx_token" IS 'zabbix 登陆 token';
COMMENT ON COLUMN "public"."sys_user"."zbx_id" IS 'zabbix 用户ID';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO "public"."sys_user" VALUES ('root', '17db03c22596b7609c7c9704f16663e0', 'abcdef', '超级管理员', '888888@qq.com', '13812345678', 1, NULL, 'ENABLE', 1, 1, '2021-07-30 21:43:02.686', '2021-07-30 21:43:02.686', 1, '6828c069d5e939b384513d705a2b9ff4', '4');
INSERT INTO "public"."sys_user" VALUES ('Admin', '897e47e15311c2fafa7408af5d33b76b', 'q4y9d', '超级管理员', '', '', 1, 1433259921541644288, 'ENABLE', 1, 1, '2021-09-23 11:40:48.622', '2021-09-23 11:40:48.622', 117, '7f7ae1a47db539231208c8e3b743059d', '1');
INSERT INTO "public"."sys_user" VALUES ('zuolan', 'e1e874669f7b8d5d9c339abf919668e2', '2c6t3', '卓蓝', '', '', 1, 1435061972567298048, 'ENABLE', 1, 1, '2021-09-16 20:19:35.538', '2021-09-16 20:19:35.538', 116, NULL, '80');
INSERT INTO "public"."sys_user" VALUES ('sanshi', '8368532ffd4039b103d1c71511671d99', 'j75bd', '三石', '', '', 1, 1435061972567298048, 'ENABLE', 1, 1, '2021-09-08 15:08:53.598', '2021-09-08 15:08:53.598', 79, NULL, '42');

-- ----------------------------
-- Table structure for sys_user_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_group";
CREATE TABLE "public"."sys_user_group" (
  "group_name" varchar(255) COLLATE "pg_catalog"."default",
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(0),
  "create_user" int8,
  "update_time" timestamp(0),
  "update_user" int8,
  "status" varchar(32) COLLATE "pg_catalog"."default" DEFAULT 1,
  "user_group_id" int8 NOT NULL
)
;

-- ----------------------------
-- Records of sys_user_group
-- ----------------------------
INSERT INTO "public"."sys_user_group" VALUES ('默认用户组', '7', '', '2021-09-02 10:46:09', 1, '2021-09-02 10:46:09', 1, 'ENABLE', 1433259921541644288);

-- ----------------------------
-- Table structure for sys_usrgrp_devicegrp
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_usrgrp_devicegrp";
CREATE TABLE "public"."sys_usrgrp_devicegrp" (
  "user_group_id" int8,
  "device_group_id" int8
)
;

-- ----------------------------
-- Records of sys_usrgrp_devicegrp
-- ----------------------------

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS "public"."tag";
CREATE TABLE "public"."tag" (
  "sid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "tag" varchar(64) COLLATE "pg_catalog"."default",
  "value" varchar(64) COLLATE "pg_catalog"."default",
  "id" int4 NOT NULL DEFAULT nextval('tag_id_seq'::regclass),
  "template_id" int4,
  "create_user" int8,
  "update_user" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6)
)
;
COMMENT ON COLUMN "public"."tag"."sid" IS '所属ID';
COMMENT ON COLUMN "public"."tag"."tag" IS '标签';
COMMENT ON COLUMN "public"."tag"."value" IS '标签值';
COMMENT ON COLUMN "public"."tag"."id" IS 'ID';
COMMENT ON COLUMN "public"."tag"."template_id" IS '继承的ID';
COMMENT ON COLUMN "public"."tag"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."tag"."update_user" IS '修改人';
COMMENT ON COLUMN "public"."tag"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."tag"."update_time" IS '更新时间';

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO "public"."tag" VALUES ('1432184478772101120', '位置', '位置', 157, NULL, 1, 1, '2021-08-30 11:35:29.303', '2021-08-30 11:35:29.303');
INSERT INTO "public"."tag" VALUES ('1432254913865859072', '位置', '位置', 159, 157, NULL, NULL, NULL, NULL);
INSERT INTO "public"."tag" VALUES ('1432255618861240320', '位置', '位置', 160, 157, NULL, NULL, NULL, NULL);
INSERT INTO "public"."tag" VALUES ('1434043521937584128', '45646', '456456', 171, NULL, 1, 1, '2021-09-14 18:05:22.27', '2021-09-14 18:05:22.27');
INSERT INTO "public"."tag" VALUES ('1434043521937584128', '1', '1', 172, NULL, 1, 1, '2021-09-14 18:05:22.27', '2021-09-14 18:05:22.27');
INSERT INTO "public"."tag" VALUES ('1434043521937584128', '2', '3', 173, NULL, 1, 1, '2021-09-14 18:05:22.27', '2021-09-14 18:05:22.27');
INSERT INTO "public"."tag" VALUES ('1437965905006678016', 'SSp7u9WF11', '三石TestHdF4i011', 174, NULL, 1, 1, '2021-09-15 10:26:02.639', '2021-09-15 10:26:02.639');
INSERT INTO "public"."tag" VALUES ('1437966016684216320', 'SSLspEVQ11', '三石TestOwN6U311', 175, NULL, 1, 1, '2021-09-15 10:26:29.264', '2021-09-15 10:26:29.264');
INSERT INTO "public"."tag" VALUES ('1437966016684216320', 'SSLspEVQ12', '三石TestOwN6U312', 176, NULL, 1, 1, '2021-09-15 10:26:29.264', '2021-09-15 10:26:29.264');
INSERT INTO "public"."tag" VALUES ('1437966016684216320', 'SSLspEVQ13', '三石TestOwN6U313', 177, NULL, 1, 1, '2021-09-15 10:26:29.264', '2021-09-15 10:26:29.264');

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."device_online_report_id_seq"
OWNED BY "public"."device_online_report"."id";
SELECT setval('"public"."device_online_report_id_seq"', 5, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."devices_groups_id_seq"
OWNED BY "public"."devices_groups"."id";
SELECT setval('"public"."devices_groups_id_seq"', 171, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."media_type_setting_id_seq"
OWNED BY "public"."media_type_setting"."id";
SELECT setval('"public"."media_type_setting_id_seq"', 2, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."messages_id_seq"
OWNED BY "public"."messages"."id";
SELECT setval('"public"."messages_id_seq"', 222, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."product_event_relation_id_seq"
OWNED BY "public"."product_event_relation"."id";
SELECT setval('"public"."product_event_relation_id_seq"', 43, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."product_event_tags_id_seq"
OWNED BY "public"."product_event_tags"."id";
SELECT setval('"public"."product_event_tags_id_seq"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."product_service_relation_id_seq"
OWNED BY "public"."product_service_relation"."id";
SELECT setval('"public"."product_service_relation_id_seq"', 24, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."product_status_function_relation_id_seq"
OWNED BY "public"."product_status_function_relation"."id";
SELECT setval('"public"."product_status_function_relation_id_seq"', 8, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."product_type_product_type_id_seq"
OWNED BY "public"."product_type"."id";
SELECT setval('"public"."product_type_product_type_id_seq"', 215, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."service_execute_record_id_seq"
OWNED BY "public"."service_execute_record"."id";
SELECT setval('"public"."service_execute_record_id_seq"', 2, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."service_id_seq"
OWNED BY "public"."product_service"."id";
SELECT setval('"public"."service_id_seq"', 33, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."service_param_id_seq"
OWNED BY "public"."product_service_param"."id";
SELECT setval('"public"."service_param_id_seq"', 19, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_user_group_user_group_id_seq"
OWNED BY "public"."sys_user_group"."user_group_id";
SELECT setval('"public"."sys_user_group_user_group_id_seq"', 30, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_user_user_id_seq"
OWNED BY "public"."sys_user"."user_id";
SELECT setval('"public"."sys_user_user_id_seq"', 118, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tag_id_seq"
OWNED BY "public"."tag"."id";
SELECT setval('"public"."tag_id_seq"', 180, true);

-- ----------------------------
-- Primary Key structure for table device
-- ----------------------------
ALTER TABLE "public"."device" ADD CONSTRAINT "device_pkey" PRIMARY KEY ("device_id");

-- ----------------------------
-- Primary Key structure for table device_group
-- ----------------------------
ALTER TABLE "public"."device_group" ADD CONSTRAINT "device_group_pkey" PRIMARY KEY ("device_group_id");

-- ----------------------------
-- Primary Key structure for table device_online_report
-- ----------------------------
ALTER TABLE "public"."device_online_report" ADD CONSTRAINT "device_online_report_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table devices_groups
-- ----------------------------
ALTER TABLE "public"."devices_groups" ADD CONSTRAINT "devices_groups_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table media_type_setting
-- ----------------------------
ALTER TABLE "public"."media_type_setting" ADD CONSTRAINT "media_type_setting_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table messages
-- ----------------------------
ALTER TABLE "public"."messages" ADD CONSTRAINT "messages_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table product
-- ----------------------------
ALTER TABLE "public"."product" ADD CONSTRAINT "product_pkey" PRIMARY KEY ("product_id");

-- ----------------------------
-- Primary Key structure for table product_attribute
-- ----------------------------
ALTER TABLE "public"."product_attribute" ADD CONSTRAINT "product_attribute_pkey" PRIMARY KEY ("attr_id");

-- ----------------------------
-- Primary Key structure for table product_attribute_event
-- ----------------------------
ALTER TABLE "public"."product_attribute_event" ADD CONSTRAINT "product_attribute_copy1_pkey" PRIMARY KEY ("attr_id");

-- ----------------------------
-- Primary Key structure for table product_event
-- ----------------------------
ALTER TABLE "public"."product_event" ADD CONSTRAINT "product_event_pkey" PRIMARY KEY ("event_rule_id");

-- ----------------------------
-- Primary Key structure for table product_event_expression
-- ----------------------------
ALTER TABLE "public"."product_event_expression" ADD CONSTRAINT "product_event_copy1_pkey1" PRIMARY KEY ("event_exp_id");

-- ----------------------------
-- Primary Key structure for table product_event_relation
-- ----------------------------
ALTER TABLE "public"."product_event_relation" ADD CONSTRAINT "product_event_relation_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table product_event_tags
-- ----------------------------
ALTER TABLE "public"."product_event_tags" ADD CONSTRAINT "product_event_tags_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table product_group
-- ----------------------------
ALTER TABLE "public"."product_group" ADD CONSTRAINT "product_group_pkey" PRIMARY KEY ("product_group_id");

-- ----------------------------
-- Primary Key structure for table product_service
-- ----------------------------
ALTER TABLE "public"."product_service" ADD CONSTRAINT "service_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table product_service_param
-- ----------------------------
ALTER TABLE "public"."product_service_param" ADD CONSTRAINT "service_param_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table product_service_relation
-- ----------------------------
ALTER TABLE "public"."product_service_relation" ADD CONSTRAINT "product_service_relation_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table product_status_function
-- ----------------------------
ALTER TABLE "public"."product_status_function" ADD CONSTRAINT "product_status_function_pkey" PRIMARY KEY ("rule_id");

-- ----------------------------
-- Primary Key structure for table product_status_function_relation
-- ----------------------------
ALTER TABLE "public"."product_status_function_relation" ADD CONSTRAINT "product_status_function_relation_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table product_type
-- ----------------------------
ALTER TABLE "public"."product_type" ADD CONSTRAINT "product_type_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table service_execute_record
-- ----------------------------
ALTER TABLE "public"."service_execute_record" ADD CONSTRAINT "service_execute_record_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table sys_config
-- ----------------------------
ALTER TABLE "public"."sys_config" ADD CONSTRAINT "sys_config_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table sys_dict
-- ----------------------------
ALTER TABLE "public"."sys_dict" ADD CONSTRAINT "sys_dict_pkey" PRIMARY KEY ("dict_id");

-- ----------------------------
-- Primary Key structure for table sys_dict_type
-- ----------------------------
ALTER TABLE "public"."sys_dict_type" ADD CONSTRAINT "sys_dict_type_pkey" PRIMARY KEY ("dict_type_id");

-- ----------------------------
-- Primary Key structure for table sys_login_log
-- ----------------------------
ALTER TABLE "public"."sys_login_log" ADD CONSTRAINT "sys_login_log_pkey" PRIMARY KEY ("login_log_id");

-- ----------------------------
-- Primary Key structure for table sys_menu
-- ----------------------------
ALTER TABLE "public"."sys_menu" ADD CONSTRAINT "sys_menu_pkey" PRIMARY KEY ("menu_id");

-- ----------------------------
-- Primary Key structure for table sys_operation_log
-- ----------------------------
ALTER TABLE "public"."sys_operation_log" ADD CONSTRAINT "sys_operation_log_pkey" PRIMARY KEY ("operation_log_id");

-- ----------------------------
-- Primary Key structure for table sys_role
-- ----------------------------
ALTER TABLE "public"."sys_role" ADD CONSTRAINT "sys_role_pkey" PRIMARY KEY ("role_id");

-- ----------------------------
-- Primary Key structure for table sys_user
-- ----------------------------
ALTER TABLE "public"."sys_user" ADD CONSTRAINT "sys_user_pkey" PRIMARY KEY ("user_id");

-- ----------------------------
-- Primary Key structure for table sys_user_group
-- ----------------------------
ALTER TABLE "public"."sys_user_group" ADD CONSTRAINT "sys_user_group_pkey" PRIMARY KEY ("user_group_id");

-- ----------------------------
-- Primary Key structure for table tag
-- ----------------------------
ALTER TABLE "public"."tag" ADD CONSTRAINT "tag_pkey" PRIMARY KEY ("id");
