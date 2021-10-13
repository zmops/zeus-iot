/*
 Navicat Premium Data Transfer

 Source Server         : 172.16.60.98-测试LOT
 Source Server Type    : PostgreSQL
 Source Server Version : 130004
 Source Host           : 172.16.60.98:5432
 Source Catalog        : zeus-iot
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 130004
 File Encoding         : 65001

 Date: 13/10/2021 20:52:05
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
  "online" int2,
  "latest_online" timestamp(6)
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
COMMENT ON COLUMN "public"."device"."latest_online" IS '最近在线时间';

-- ----------------------------
-- Records of device
-- ----------------------------

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

-- ----------------------------
-- Table structure for media_type_setting
-- ----------------------------
DROP TABLE IF EXISTS "public"."media_type_setting";
CREATE TABLE "public"."media_type_setting" (
  "id" int4 NOT NULL DEFAULT nextval('media_type_setting_id_seq'::regclass),
  "type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "template" text COLLATE "pg_catalog"."default",
  "webhooks" text COLLATE "pg_catalog"."default",
  "tenant_id" int8
)
;
COMMENT ON COLUMN "public"."media_type_setting"."type" IS '类型';
COMMENT ON COLUMN "public"."media_type_setting"."template" IS '消息模板';
COMMENT ON COLUMN "public"."media_type_setting"."webhooks" IS '推送配置';
COMMENT ON COLUMN "public"."media_type_setting"."tenant_id" IS '租户ID';

-- ----------------------------
-- Records of media_type_setting
-- ----------------------------
INSERT INTO "public"."media_type_setting" VALUES (1, 'wechat', '
{
        "content": "广州今日天气：29度，大部分多云，降雨概率：80%",
        "mentioned_list":["wangqing","@all"],
        "mentioned_mobile_list":["13800001111","@all"]
 }
', '{"url":"https://oapi.dingtalk.com/robot/send?access_token=dd0f8a3340c15ec0f1d54b91980584c1127abe6dd143a964d44385647bc15124"}', NULL);
INSERT INTO "public"."media_type_setting" VALUES (2, 'dingtalk', '{
    "msgtype":"text",
    "text": {
        "content":"%s"
    }
}', '{"url":"https://oapi.dingtalk.com/robot/send?access_token=dd","secret":"5c60"}', NULL);
INSERT INTO "public"."media_type_setting" VALUES (3, 'feishu', '{
    "msg_type":"text",
    "content": {
        "text":"%s"
    }
}', '{"url":"https://open.feishu.cn/open-apis/bot/v2/hook/c43e56f","secret":"xoaP"}', NULL);

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
  "template_id" int8,
  "delay" int2,
  "unit" varchar(4) COLLATE "pg_catalog"."default",
  "valuemapid" varchar(8) COLLATE "pg_catalog"."default"
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
COMMENT ON COLUMN "public"."product_attribute"."delay" IS '取数间隔';
COMMENT ON COLUMN "public"."product_attribute"."unit" IS '取数间隔单位 s m h ';
COMMENT ON COLUMN "public"."product_attribute"."valuemapid" IS '值映射ID';

-- ----------------------------
-- Records of product_attribute
-- ----------------------------

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
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "create_user" int8,
  "update_time" timestamp(6),
  "update_user" int8,
  "exp_logic" varchar(10) COLLATE "pg_catalog"."default",
  "event_notify" varchar(1) COLLATE "pg_catalog"."default",
  "status" char(1) COLLATE "pg_catalog"."default",
  "classify" varchar(2) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_event"."event_rule_id" IS '告警规则ID';
COMMENT ON COLUMN "public"."product_event"."event_rule_name" IS '告警规则名称';
COMMENT ON COLUMN "public"."product_event"."event_level" IS '告警等级';
COMMENT ON COLUMN "public"."product_event"."remark" IS '备注';
COMMENT ON COLUMN "public"."product_event"."exp_logic" IS 'and 或者 or';
COMMENT ON COLUMN "public"."product_event"."event_notify" IS '0 否 1 是';
COMMENT ON COLUMN "public"."product_event"."classify" IS '0 告警 1场景联动';

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
  "product_attr_key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "device_id" varchar(255) COLLATE "pg_catalog"."default",
  "unit" varchar(16) COLLATE "pg_catalog"."default",
  "product_attr_id" int8,
  "product_attr_type" varchar(4) COLLATE "pg_catalog"."default",
  "period" varchar(8) COLLATE "pg_catalog"."default",
  "attr_value_type" varchar(8) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_event_expression"."event_exp_id" IS '告警函数ID';
COMMENT ON COLUMN "public"."product_event_expression"."event_rule_id" IS '告警规则ID';
COMMENT ON COLUMN "public"."product_event_expression"."product_attr_key" IS 'item key';
COMMENT ON COLUMN "public"."product_event_expression"."device_id" IS '设备ID';
COMMENT ON COLUMN "public"."product_event_expression"."product_attr_id" IS '属性ID';
COMMENT ON COLUMN "public"."product_event_expression"."product_attr_type" IS '属性类型 属性 事件';
COMMENT ON COLUMN "public"."product_event_expression"."period" IS '取值周期 时间 周期';
COMMENT ON COLUMN "public"."product_event_expression"."attr_value_type" IS '属性值类型';

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
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default",
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
  "name" varchar(32) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "value" varchar(255) COLLATE "pg_catalog"."default",
  "device_id" varchar(64) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_service_param"."service_id" IS '服务ID';
COMMENT ON COLUMN "public"."product_service_param"."key" IS '参数标识';
COMMENT ON COLUMN "public"."product_service_param"."name" IS '参数名称';
COMMENT ON COLUMN "public"."product_service_param"."remark" IS '备注';
COMMENT ON COLUMN "public"."product_service_param"."value" IS '参数值';
COMMENT ON COLUMN "public"."product_service_param"."device_id" IS '关联设备ID';

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
  "zbx_id" varchar(32) COLLATE "pg_catalog"."default",
  "rule_function" varchar(10) COLLATE "pg_catalog"."default",
  "rule_status" int2 DEFAULT 1,
  "create_user" int8,
  "update_user" int8,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "rule_id" int8 NOT NULL,
  "rule_condition" varchar(255) COLLATE "pg_catalog"."default",
  "rule_function_recovery" varchar(10) COLLATE "pg_catalog"."default",
  "rule_condition_recovery" varchar(255) COLLATE "pg_catalog"."default",
  "attr_id" int8,
  "attr_id_recovery" int8,
  "unit" varchar(8) COLLATE "pg_catalog"."default",
  "unit_recovery" varchar(8) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."product_status_function"."zbx_id" IS 'zabbix trigger id';
COMMENT ON COLUMN "public"."product_status_function"."rule_function" IS '''nodata'' or ''last''';
COMMENT ON COLUMN "public"."product_status_function"."rule_status" IS '0 or 1';
COMMENT ON COLUMN "public"."product_status_function"."rule_id" IS '触发器ID，离线 上线';
COMMENT ON COLUMN "public"."product_status_function"."rule_condition" IS '''5m'' or ''4''';
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
INSERT INTO "public"."product_type" VALUES (1, 0, '默认产品分组', NULL, '[0],');

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
INSERT INTO "public"."sys_config" VALUES (1145915627211370502, '获取token的header标识', 'ZEUS_TOKEN_HEADER_NAME', 'N', NULL, 'Authorization', '获取token的header标识', 1, 1, '2019-10-16 23:02:39', '2021-08-03 16:38:12.432', 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1143468689664876546, '管理系统名称', 'ZEUS_SYSTEM_NAME', 'N', NULL, 'ZEUS-IOT', '管理系统名称', 1, 1, '2019-06-25 18:39:15', '2021-08-09 17:17:30.189', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370501, '获取系统地密钥过期时间', 'ZEUS_JWT_SECRET_EXPIRE', 'N', NULL, '86400', '获取系统地密钥过期时间（单位：秒），默认1天', 1, 1, '2019-10-16 23:02:39', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143469008025133058, 'OAuth2登录用户的账号标识', 'ZEUS_OAUTH2_PREFIX', 'N', NULL, 'oauth2', 'OAuth2登录用户的账号标识', 1, 1, '2019-06-25 18:40:31', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370499, '文件上传路径', 'ZEUS_FILE_UPLOAD_PATH', 'N', NULL, '/tmp', '文件上传默认目录', 1, 1, '2019-08-30 09:10:40', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143324237579165697, '验证码开关', 'ZEUS_KAPTCHA_OPEN', 'Y', 1106120265689055233, 'DISABLE', '是否开启验证码', 1, 1, '2019-06-24 12:46:43', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370498, 'Zeus发布的编号', 'ZEUS_SYSTEM_RELEASE_VERSION', 'N', NULL, '10', '用于防止浏览器缓存相关的js和css', 1, 1, '2019-07-02 12:42:30', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143468867767607297, '默认系统密码', 'ZEUS_DEFAULT_PASSWORD', 'N', NULL, '111111', '默认系统密码', 1, 1, '2019-06-25 18:39:57', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143468867767607208, '单点登录开关', 'ZEUS_SIGN_IN', 'N', NULL, 'DISABLE', '是否启用单点登录', 1, 1, '2019-06-25 18:39:57', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1143468867767607207, '默认租户角色ID', 'ZEUS_TENANT_ROLE_ID', 'N', NULL, '1', '默认租户角色ID', 1, 1, '2019-06-25 18:39:57', '2021-08-03 16:38:12.432', 'ENABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370496, '全局主机组Id', 'ZEUS_HOST_GROUP_ID', 'Y', NULL, '19', '全局主机和模板组ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1143468867767607298, '用户角色ID', 'ZEUS_ADMIN_ROLE_ID', 'N', NULL, '3', '用户角色ID', 1, 1, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370497, '离线回调ActionId', 'ZEUS_OFFLINE_ACTION_ID', 'Y', NULL, '7', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370503, '告警回调ActionId', 'ZEUS_ALARM_ACTION_ID', 'Y', NULL, '8', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370504, '动作回调ActionId', 'ZEUS_EXEC_ACTION_ID', 'Y', NULL, '9', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');
INSERT INTO "public"."sys_config" VALUES (1145915627211370505, '事件回调ActionId', 'ZEUS_EVENT_ACTION_ID', 'Y', NULL, '10', '回调动作ID', NULL, NULL, NULL, NULL, 'DISABLE');

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
INSERT INTO "public"."sys_dict" VALUES (1423897784372199431, 1160532886713155590, '3', '整数', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199432, 1160532886713155590, '0', '小数', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199433, 1160532886713155590, '1', '字符', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199434, 1160532886713155590, '4', '文本', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199470, 1160532886713155589, 'mg', '毫克', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199471, 1160532886713155589, 'g', '克', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199472, 1160532886713155589, 'kg', '千克', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199473, 1160532886713155589, 't', '吨', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '质量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199474, 1160532886713155589, 'Pa', '帕斯卡', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '压力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199475, 1160532886713155589, 'kPa', '千帕斯卡', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '压力单位');
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
INSERT INTO "public"."sys_dict" VALUES (1423897784372199435, 1160532886713155589, '%', '百分比', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '常用单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199437, 1160532886713155589, 'r/min', '转每分钟', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '常用单位');
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
INSERT INTO "public"."sys_dict" VALUES (1423897784372199428, 1160532886713155588, '2', '主动上报', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199430, 1160532886713155588, '18', '根据单个属性预处理', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199436, 1160532886713155589, 'count', '次', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '常用单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199506, 1160532886713155591, '25', 'Replace', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199507, 1160532886713155591, '4', 'Trim', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199508, 1160532886713155591, '2', 'Right trim', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199509, 1160532886713155591, '3', 'Left trim', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '文本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199510, 1160532886713155591, '11', 'XML XPath', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '结构化数据');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199511, 1160532886713155591, '12', 'JSONPath', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '结构化数据');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199514, 1160532886713155591, '1', '自定义倍数', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '自定义计算');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199517, 1160532886713155591, '7', '八进制转十进制', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '数制转换');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199518, 1160532886713155591, '8', '十六进制转十进制', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '数制转换');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199519, 1160532886713155591, '21', 'JavaScript', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '自定义脚本');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199500, 1160532886713155589, 'Ω', '欧姆', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199501, 1160532886713155589, 'KΩ', '千欧', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199502, 1160532886713155589, 'MΩ', '兆欧', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199503, 1160532886713155589, 'eV', '电子伏', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199504, 1160532886713155589, 'kW·h', '千瓦·时', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '电力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199505, 1160532886713155589, 'kgce', 'Kg标准煤', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '能源单位');
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
INSERT INTO "public"."sys_dict" VALUES (1423897784372199469, 1160532886713155589, 'L', '升', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '容积单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199476, 1160532886713155589, 'N', '牛顿', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199477, 1160532886713155589, 'N.m', '牛·米', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '力单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199478, 1160532886713155589, 'K', '开尔文', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '温度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199479, 1160532886713155589, '℃', '摄氏度', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '温度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199480, 1160532886713155589, '℉', '华氏度', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '温度单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199481, 1160532886713155589, 'J', '焦耳', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '能量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199482, 1160532886713155589, 'cal', '卡', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '能量单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199483, 1160532886713155589, 'W', '瓦特', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '功率单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199484, 1160532886713155589, 'kW', '千瓦特', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '功率单位');
INSERT INTO "public"."sys_dict" VALUES (1423897784372199485, 1160532886713155589, 'rad', '弧度', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '角度单位');
INSERT INTO "public"."sys_dict" VALUES (1428599180229115911, 1428599180229115904, '0', '', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1428599180229115912, 1428599180229115904, '1', '是', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1429987619034984449, 1142859918022911591, '1', '异步', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1429987619034984450, 1142859918022911591, '0', '同步', 'ENABLE', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199512, 1160532886713155591, '13', 'In range', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, '验证');
INSERT INTO "public"."sys_dict" VALUES (1440492197892923392, 1440492197679013888, 'SSm5lxzi', '三石Test3OrlDb', 'DISABLE', 99, NULL, '2021-09-22 09:44:37.685', '2021-09-22 09:44:37.755', 1, 1, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199531, 1160532886713155588, '0', 'Agent 采集', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199528, 1440492220332449792, '1', '信息', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199529, 1440492220332449792, '2', '低级', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199527, 1440492220332449792, '3', '中级', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199526, 1440492220332449792, '4', '高级', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."sys_dict" VALUES (1423897784372199525, 1440492220332449792, '5', '紧急', 'ENABLE', 1, NULL, NULL, NULL, NULL, NULL, NULL);

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
INSERT INTO "public"."sys_dict_type" VALUES (1440492220332449792, 'EVENT_LEVEL', '告警级别', '告警级别', 'N', 'ENABLE', NULL, '2021-09-22 09:44:43.035', 1, '2021-09-22 09:44:43.102', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1142859918022911591, 'EXECUTE_TYPE', '服务执行方式', '服务执行方式', 'N', 'ENABLE', 1, NULL, NULL, '2021-09-10 15:56:14.127', 1);
INSERT INTO "public"."sys_dict_type" VALUES (1428599180229115904, 'WHETHER', '是否', '是否', 'N', 'ENABLE', NULL, '2021-08-20 14:06:01.311', 1, '2021-08-20 14:06:01.311', 1);

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
  "update_user" int8,
  "admin_flag" varchar(32) COLLATE "pg_catalog"."default"
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
COMMENT ON COLUMN "public"."sys_menu"."admin_flag" IS '是否是超级管理员菜单';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO "public"."sys_menu" VALUES (113, 'mgr_setRole', 'mgr', '[0],[system],[mgr],', '分配角色', NULL, '/mgr/setRole', 7, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (139, 'dict_update', 'dict', '[0],[system],[dict],', '修改字典', NULL, '/dictType/editItem', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (140, 'dict_delete', 'dict', '[0],[system],[dict],', '删除字典', NULL, '/dictType/delete', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (151, 'menu_list', 'menu', '[0],[system],[menu],', '菜单列表', '', '/menu/list', 5, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:25', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (115, 'role_add', 'role', '[0],[system],[role],', '添加角色', NULL, '/role/add', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (116, 'role_edit', 'role', '[0],[system],[role],', '修改角色', NULL, '/role/edit', 2, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (107, 'mgr_add', 'mgr', '[0],[system],[mgr],', '添加用户', NULL, '/mgr/add', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (108, 'mgr_edit', 'mgr', '[0],[system],[mgr],', '修改用户', NULL, '/mgr/edit', 2, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (109, 'mgr_delete', 'mgr', '[0],[system],[mgr],', '删除用户', NULL, '/mgr/delete', 3, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (117, 'role_remove', 'role', '[0],[system],[role],', '删除角色', NULL, '/role/remove', 3, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (156, 'dict_list', 'dict', '[0],[system],[dict],', '字典列表', '', '/dict/list', 5, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (164, 'role_list', 'role', '[0],[system],[role],', '角色列表', '', '/role/list', 7, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (114, 'role', 'system', '[0],[system],', '角色管理', '', '/system/role', 20, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (128, 'businessLog', 'log', '[0],[log],', '业务日志', '', '/log/businessLog', 70, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:39', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (181, 'product_type', 'product_mgr', '[0],[product_mgr],', '产品分类', NULL, '/productMgr/productType', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (119, 'menu', 'system', '[0],[system],', '菜单管理', '', '/menu', 50, 2, 'Y', NULL, 'DISABLE', NULL, '2019-06-30 13:48:25', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (132, 'dict', 'system', '[0],[system],', '字典管理', '', '/system/dictType', 40, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (158, 'log_list', 'log', '[0],[system],[log],', '日志列表', '', '/log/list', 2, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:39', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (133, 'loginLog', 'log', '[0],[log],', '登录日志', '', '/log/loginLog', 60, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:49:29', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (186, 'sysParam', 'system', '[0],[system],', '系统参数', '', '/system/sysParam', 10, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (167, 'mgr_list', 'mgr', '[0],[system],[mgr],', '用户列表', '', '/mgr/list', 10, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (174, 'auth', '0', '[0],', '平台授权', NULL, '/auth', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'Y');
INSERT INTO "public"."sys_menu" VALUES (196, 'api', 'dev_mgr', '[0],[dev_mgr],', '设备调试', NULL, '/deviceMgr/debug', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (110, 'reset', 'mgr', '[0],[system],[mgr],', '重置密码', NULL, '/mgr/reset', 4, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (177, 'dev', 'dev_mgr', '[0],[dev_mgr],', '设备', NULL, '/deviceMgr/device', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (178, 'dev_group', 'dev_mgr', '[0],[dev_mgr],', '设备组', NULL, '/deviceMgr/deviceGroup', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (171, 'product_mgr', '0', '[0],', '产品管理', NULL, '/productMgr', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (180, 'product', 'product_mgr', '[0],[product_mgr],', '产品', NULL, '/productMgr/product', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (184, 'log', '0', '[0]', '日志管理', NULL, '/log', 4, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (105, 'system', '0', '[0],', '系统管理', 'layui-icon layui-icon-set', '/system', 20, 1, 'Y', NULL, 'ENABLE', NULL, '2019-03-29 16:32:27', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (170, 'dev_mgr', '0', '[0],', '设备管理', NULL, '/deviceMgr', 2, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (187, 'media', 'system', '[0],[system],', '通知配置', '', '/system/media', 80, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:50:06', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (189, 'home', 'analyse', '[0],[analyse],', '全局概览', NULL, '/analyse/home', 1, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (179, 'alarmList', 'alarm', '[0],[alarm],', '告警记录', NULL, '/alarm/alarmList', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (169, 'analyse', '0', '[0],', '统计分析', NULL, '/analyse', 1, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (190, 'latest', 'analyse', '[0],[analyse],', '最新数据', NULL, '/analyse/latest', 1, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (191, 'alarmAnalyse', 'alarm', '[0],[alarm],', '告警分析', NULL, '/alarm/analyse', 3, 2, 'Y', NULL, 'DISABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (173, 'view', '0', '[0],', '可视化', NULL, '/view', 3, 1, 'Y', NULL, 'DISABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (176, 'dev_log', 'analyse', '[0],[analyse],', '设备日志', NULL, '/analyse/devLog', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (183, 'grafana', 'view', '[0],[view],', 'Grafana配置', NULL, '/view/grafana', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (192, 'alarm', '0', '[0],', '告警管理', NULL, '/alarm', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (194, 'rule', '0', '[0],', '规则引擎', NULL, '/rule', 3, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (197, 'about', '0', '[0],', '关于我们', NULL, '/about', 2, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (106, 'userMgr', '0', '[0],', '用户管理', '', '/userMgr', 10, 1, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (168, 'usrGrp', 'userMgr', '[0],[userMgr],', '用户组管理', NULL, '/userMgr/userGroup', 9, 1, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (138, 'dict_add', 'dict', '[0],[system],[dict],', '添加字典', NULL, '/dictType/addItem', 1, 3, 'N', NULL, 'ENABLE', NULL, '2019-06-30 13:49:04', NULL, 1, 'Y');
INSERT INTO "public"."sys_menu" VALUES (206, 'dev_detail', 'dev', '[0],[dev_mgr],[dev]', '设备详情', NULL, '/deviceMgr/device/detail', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (205, 'dev_list', 'dev', '[0],[dev_mgr],[dev]', '设备列表', NULL, '/deviceMgr/device/list', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (204, 'dev_delete', 'dev', '[0],[dev_mgr],[dev]', '删除设备', NULL, '/deviceMgr/device/delete', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (207, 'product_add', 'product', '[0],[product_mgr],[product]', '增加产品', NULL, '/productMgr/product', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (208, 'product_update', 'product', '[0],[product_mgr],[product]', '修改产品', NULL, '/productMgr/product', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (209, 'product_delete', 'product', '[0],[product_mgr],[product]', '删除产品', NULL, '/productMgr/product', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (210, 'product_list', 'product', '[0],[product_mgr],[product]', '产品列表', NULL, '/productMgr/product', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (211, 'product_detail', 'product', '[0],[product_mgr],[product]', '产品详情', NULL, '/productMgr/product', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (212, 'tenant', 'system', '[0],[system],', '租户管理', '', '/system/tenant', 10, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (213, 'tenant_add', 'tenant', '[0],[system],', '租户增加', '', '/system/tenant', 10, 3, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (214, 'tenant_update', 'tenant', '[0],[system],', '租户修改', '', '/system/tenant', 10, 3, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (215, 'tenant_list', 'tenant', '[0],[system],', '租户列表', '', '/system/tenant', 10, 3, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (216, 'tenant_delete', 'tenant', '[0],[system],', '租户删除', '', '/system/tenant', 10, 3, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (217, 'mgr', 'userMgr', '[0],[userMgr],', '用户', '', '/userMgr/user', 10, 1, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:07', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (195, 'nodeRed', 'rule', '[0],[rule],', 'Node-Red', NULL, '/rule/nodeRed', 3, 2, 'Y', NULL, 'DISABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (199, 'proxyInfo', 'system', '[0],[proxy],', '代理管理', '', '/proxy/proxyInfo', 20, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (200, 'proxyMonitor', 'system', '[0],[proxy],', '代理监控', '', '/proxy/proxyMonitor', 20, 2, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (198, 'proxy', 'system', '[0],', '边缘采集', '', '/proxy', 20, 1, 'Y', NULL, 'ENABLE', NULL, '2019-06-30 13:48:12', NULL, 1, 'N');
INSERT INTO "public"."sys_menu" VALUES (201, 'grafana', 'analyse', '[0],[analyse],', 'Grafana 演示大屏', NULL, '/analyse/grafana', 5, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (203, 'dev_update', 'dev', '[0],[dev_mgr],[dev]', '修改设备', NULL, '/deviceMgr/device/update', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (202, 'dev_add', 'dev', '[0],[dev_mgr],[dev]', '增加设备', NULL, '/deviceMgr/device/add', 3, 3, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');
INSERT INTO "public"."sys_menu" VALUES (193, 'scene ', 'rule', '[0],[rule],', '场景联动', NULL, '/rule/scene', 3, 2, 'Y', NULL, 'ENABLE', NULL, NULL, NULL, NULL, 'N');

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
INSERT INTO "public"."sys_role_menu" VALUES (1, 169);
INSERT INTO "public"."sys_role_menu" VALUES (1, 170);
INSERT INTO "public"."sys_role_menu" VALUES (1, 171);
INSERT INTO "public"."sys_role_menu" VALUES (1, 172);
INSERT INTO "public"."sys_role_menu" VALUES (1, 173);
INSERT INTO "public"."sys_role_menu" VALUES (1, 174);
INSERT INTO "public"."sys_role_menu" VALUES (1, 175);
INSERT INTO "public"."sys_role_menu" VALUES (1, 176);
INSERT INTO "public"."sys_role_menu" VALUES (1, 177);
INSERT INTO "public"."sys_role_menu" VALUES (1, 178);
INSERT INTO "public"."sys_role_menu" VALUES (1, 179);
INSERT INTO "public"."sys_role_menu" VALUES (1, 180);
INSERT INTO "public"."sys_role_menu" VALUES (1, 181);
INSERT INTO "public"."sys_role_menu" VALUES (1, 182);
INSERT INTO "public"."sys_role_menu" VALUES (1, 183);
INSERT INTO "public"."sys_role_menu" VALUES (1, 168);
INSERT INTO "public"."sys_role_menu" VALUES (1, 184);
INSERT INTO "public"."sys_role_menu" VALUES (1, 128);
INSERT INTO "public"."sys_role_menu" VALUES (1, 133);
INSERT INTO "public"."sys_role_menu" VALUES (1, 132);
INSERT INTO "public"."sys_role_menu" VALUES (1, 201);
INSERT INTO "public"."sys_role_menu" VALUES (1, 202);
INSERT INTO "public"."sys_role_menu" VALUES (1, 203);
INSERT INTO "public"."sys_role_menu" VALUES (1, 204);
INSERT INTO "public"."sys_role_menu" VALUES (1, 205);
INSERT INTO "public"."sys_role_menu" VALUES (1, 206);
INSERT INTO "public"."sys_role_menu" VALUES (1, 207);
INSERT INTO "public"."sys_role_menu" VALUES (1, 208);
INSERT INTO "public"."sys_role_menu" VALUES (1, 209);
INSERT INTO "public"."sys_role_menu" VALUES (1, 211);
INSERT INTO "public"."sys_role_menu" VALUES (1, 210);
INSERT INTO "public"."sys_role_menu" VALUES (1, 212);
INSERT INTO "public"."sys_role_menu" VALUES (1, 217);
INSERT INTO "public"."sys_role_menu" VALUES (1, 105);
INSERT INTO "public"."sys_role_menu" VALUES (1, 106);
INSERT INTO "public"."sys_role_menu" VALUES (1, 107);
INSERT INTO "public"."sys_role_menu" VALUES (1, 108);
INSERT INTO "public"."sys_role_menu" VALUES (1, 109);
INSERT INTO "public"."sys_role_menu" VALUES (1, 110);
INSERT INTO "public"."sys_role_menu" VALUES (1, 111);
INSERT INTO "public"."sys_role_menu" VALUES (1, 112);
INSERT INTO "public"."sys_role_menu" VALUES (1, 113);
INSERT INTO "public"."sys_role_menu" VALUES (1, 114);
INSERT INTO "public"."sys_role_menu" VALUES (1, 115);
INSERT INTO "public"."sys_role_menu" VALUES (1, 116);
INSERT INTO "public"."sys_role_menu" VALUES (1, 117);
INSERT INTO "public"."sys_role_menu" VALUES (1, 118);
INSERT INTO "public"."sys_role_menu" VALUES (1, 119);
INSERT INTO "public"."sys_role_menu" VALUES (1, 120);
INSERT INTO "public"."sys_role_menu" VALUES (1, 121);
INSERT INTO "public"."sys_role_menu" VALUES (1, 122);
INSERT INTO "public"."sys_role_menu" VALUES (1, 185);
INSERT INTO "public"."sys_role_menu" VALUES (1, 186);
INSERT INTO "public"."sys_role_menu" VALUES (1, 187);
INSERT INTO "public"."sys_role_menu" VALUES (1, 188);
INSERT INTO "public"."sys_role_menu" VALUES (1, 189);
INSERT INTO "public"."sys_role_menu" VALUES (1, 190);
INSERT INTO "public"."sys_role_menu" VALUES (1, 191);
INSERT INTO "public"."sys_role_menu" VALUES (1, 192);
INSERT INTO "public"."sys_role_menu" VALUES (1, 193);
INSERT INTO "public"."sys_role_menu" VALUES (1, 194);
INSERT INTO "public"."sys_role_menu" VALUES (1, 195);
INSERT INTO "public"."sys_role_menu" VALUES (1, 196);
INSERT INTO "public"."sys_role_menu" VALUES (1, 197);
INSERT INTO "public"."sys_role_menu" VALUES (1, 213);
INSERT INTO "public"."sys_role_menu" VALUES (1, 214);
INSERT INTO "public"."sys_role_menu" VALUES (1, 215);
INSERT INTO "public"."sys_role_menu" VALUES (1, 216);
INSERT INTO "public"."sys_role_menu" VALUES (1, 198);
INSERT INTO "public"."sys_role_menu" VALUES (1, 199);
INSERT INTO "public"."sys_role_menu" VALUES (1, 200);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user";
CREATE TABLE "public"."sys_user" (
  "account" varchar(32) COLLATE "pg_catalog"."default",
  "password" varchar(32) COLLATE "pg_catalog"."default",
  "salt" varchar(32) COLLATE "pg_catalog"."default",
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "email" varchar(64) COLLATE "pg_catalog"."default",
  "phone" varchar(16) COLLATE "pg_catalog"."default",
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
INSERT INTO "public"."sys_user" VALUES ('Admin', 'cbde417443393372dbac9c185a6ec159', 'gt3zs', '超级管理员', '', '', 1, 1437232484602372096, 'ENABLE', 1, 1, '2021-09-28 09:38:33.668', '2021-09-28 09:38:33.668', 122, 'f93a1bea2549f93fad75bdde71e6b4fe', '1');
INSERT INTO "public"."sys_user" VALUES ('root', '17db03c22596b7609c7c9704f16663e0', 'abcdef', '超级管理员', '888888@qq.com', '13812345678', 1, 1437232484602372096, 'ENABLE', 1, 1, '2021-07-30 21:43:02.686', '2021-07-30 21:43:02.686', 1, '5859e004e8d2a23e6c330c3f9cd277e2', '4');

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

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."device_online_report_id_seq"
OWNED BY "public"."device_online_report"."id";
SELECT setval('"public"."device_online_report_id_seq"', 7, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."devices_groups_id_seq"
OWNED BY "public"."devices_groups"."id";
SELECT setval('"public"."devices_groups_id_seq"', 183, true);

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
SELECT setval('"public"."messages_id_seq"', 234, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."product_event_relation_id_seq"
OWNED BY "public"."product_event_relation"."id";
SELECT setval('"public"."product_event_relation_id_seq"', 62, true);

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
SELECT setval('"public"."product_type_product_type_id_seq"', 226, true);

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
SELECT setval('"public"."sys_user_user_id_seq"', 123, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tag_id_seq"
OWNED BY "public"."tag"."id";
SELECT setval('"public"."tag_id_seq"', 196, true);

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
