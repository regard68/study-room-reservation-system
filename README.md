# 共享自习室预约管理系统

本项目是一个面向 JavaEE 课程设计的 Spring Boot Web 系统。学生可以查询并预约自习室座位，管理员可以维护用户、自习室、座位、预约和公告信息。

## 技术栈

- Java 17
- Spring Boot 3.3.5
- MyBatis 3
- Thymeleaf
- MySQL 8
- Maven

## 项目结构

```text
study-room-reservation-system
├── pom.xml
├── README.md
├── docs
│   ├── course-design.md
│   └── test-cases.md
└── src
    ├── main
    │   ├── java/com/studyroom
    │   │   ├── config
    │   │   ├── controller
    │   │   ├── entity
    │   │   ├── mapper
    │   │   ├── service
    │   │   ├── service/impl
    │   │   └── utils
    │   └── resources
    │       ├── static/css
    │       ├── templates
    │       ├── sql/schema.sql
    │       ├── sql/data.sql
    │       └── application.yml
    └── test
```

## 运行步骤

1. 安装 JDK 17 或更高版本、Maven 和 MySQL 8。
2. 在 MySQL 中依次执行：

```bash
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p study_room_db < src/main/resources/sql/data.sql
```

3. 使用环境变量填写你的 MySQL 用户名和真实密码。在项目根目录启动：

```bash
DB_USERNAME=root DB_PASSWORD='你的MySQL密码' mvn spring-boot:run
```

也可以使用交互式启动脚本，按提示输入 MySQL 密码：

```bash
./run.sh
```

推荐使用 `./run.sh`。脚本会自动检查端口、MySQL 服务、账号密码、数据库和五张核心表，数据库不存在时会自动导入 SQL。

本机开发可在项目根目录创建不会提交的 `.env.local`：

```bash
export DB_USERNAME='root'
export DB_PASSWORD='你的MySQL密码'
```

之后运行 `./run.sh` 时无需重复输入数据库账号密码。

如果你的 MySQL 账号没有密码，可以使用：

```bash
DB_USERNAME=root DB_PASSWORD='' mvn spring-boot:run
```

4. 浏览器访问 `http://localhost:8080`。

## 演示账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | admin | admin123 |
| 学生 | student | 123456 |

## 核心规则

- 只有状态为“开放”的自习室可以预约。
- 只有状态为“可用”的座位可以预约。
- 同一座位在同一日期、同一时间段不能重复预约。
- 预约座位时必须填写预约人姓名。
- 同一姓名在同一日期、同一时间段只能预约一个座位。
- 同一账号可以使用不同姓名，在同一时间段分别预约不同座位。
- 学生只能取消自己的、尚未结束的有效预约。
- 自习室的座位数量由系统根据座位表自动统计。

## 主要页面

- 登录、注册
- 学生首页、自习室列表、座位预约、我的预约、公告列表
- 管理员首页、用户管理、自习室管理、座位管理、预约管理、公告管理

学生注册只需要用户名和密码，注册成功后会自动登录并进入学生首页。

## Railway 云端部署

在线访问地址：<https://web-production-ef4f7.up.railway.app>

项目支持部署到 Railway。应用服务需要设置以下环境变量：

```text
DB_URL=jdbc:mysql://MySQL主机:端口/数据库名?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
DB_USERNAME=MySQL用户名
DB_PASSWORD=MySQL密码
SQL_INIT_MODE=always
```

`SQL_INIT_MODE=always` 会在云端首次启动时自动创建数据表和演示数据。建表脚本使用幂等语句，服务重新启动不会重复创建自习室、座位和公告。
