# 数据库修复说明

## 微信小程序数据库修复脚本

为确保微信小程序功能正常运行，需要执行数据库修复脚本。这些脚本将为`customer`表添加微信小程序所需的字段。

### 执行选项

您可以选择以下两种方式之一执行修复：

#### 选项1：执行合并脚本（推荐）

执行单个合并脚本：
- `customer_table_complete_fix.sql` - 包含所有需要添加的字段

#### 选项2：分步执行

按照以下顺序执行两个脚本：
1. `小程序修复add_wechat_fields_to_customer.sql` - 添加基本微信字段
2. `customer_table_fix_missing_fields.sql` - 添加其他缺失字段

### 脚本说明

#### 1. customer_table_complete_fix.sql（推荐）

此合并脚本添加所有需要的字段：
- `wechat_openid` - 微信小程序用户的唯一标识OpenID
- `wechat_unionid` - 微信开放平台下的唯一应用标识UnionID
- `wechat_avatar_url` - 用户的微信头像URL
- `remark` - 备注
- `nickname` - 微信昵称
- `gender` - 性别（0未知，1男，2女）
- `country` - 国家
- `province` - 省份
- `city` - 城市
- `language` - 语言
- `session_key` - 微信会话密钥
- `last_login_time` - 最后登录时间

同时，脚本也添加了字段映射注释，说明Java对象属性与数据库字段的对应关系。

#### 2. 小程序修复add_wechat_fields_to_customer.sql

此脚本仅添加基本微信字段：
- `wechat_openid` - 微信小程序用户的唯一标识OpenID
- `wechat_unionid` - 微信开放平台下的唯一应用标识UnionID
- `wechat_avatar_url` - 用户的微信头像URL

#### 3. customer_table_fix_missing_fields.sql

此脚本添加其他缺失字段：
- `remark` - 备注
- `nickname` - 微信昵称
- `gender` - 性别（0未知，1男，2女）
- `country` - 国家
- `province` - 省份
- `city` - 城市
- `language` - 语言
- `session_key` - 微信会话密钥
- `last_login_time` - 最后登录时间

## 数据库与Java代码映射关系

| 数据库字段 | Java属性 |
|------------|----------|
| id | id |
| name | customerName |
| contact | contactPerson |
| phone | contactPhone |
| email | email |
| address | address |
| remark | remark |
| create_time | createTime |
| update_time | updateTime |
| wechat_openid | openid |
| nickname | nickname |
| wechat_unionid | unionid |
| wechat_avatar_url | avatarUrl |
| gender | gender |
| country | country |
| province | province |
| city | city |
| language | language |
| session_key | sessionKey |
| last_login_time | lastLoginTime |

## 执行方法

### 选项1：执行合并脚本（推荐）

1. 在SQL Server Management Studio中连接到数据库
2. 打开并执行 `customer_table_complete_fix.sql` 文件

### 选项2：分步执行

1. 在SQL Server Management Studio中连接到数据库
2. 先执行 `小程序修复add_wechat_fields_to_customer.sql` 文件
3. 再执行 `customer_table_fix_missing_fields.sql` 文件

所有脚本都包含了检查逻辑，可以安全地多次执行，不会重复添加已存在的列。 