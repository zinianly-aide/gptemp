# 员工快照系统项目概述

## 项目简介
员工快照系统是一个用于记录和分析员工数据的Spring Boot应用，提供员工信息查询、统计分析和趋势展示功能。

## 技术栈
- Java 17
- Spring Boot 3.2.2
- Thymeleaf
- MyBatis
- PostgreSQL
- Docker

## 主要功能
1. 员工数据快照记录
2. 员工总数统计和变化率分析
3. 员工趋势图表展示
4. RESTful API接口
5. 测试模式支持（Mock服务）

## 项目结构
- src/main/java - Java源代码
- src/main/resources - 配置文件和模板
- src/test - 测试代码
- Dockerfile - 容器化配置
- docker-compose.yml - 多容器部署配置

## 运行方式
1. 本地运行：`mvn spring-boot:run`
2. Docker运行：`docker-compose up`
3. 测试模式：`docker-compose up test`