## 多线程爬虫

## 开发流程

1. 项目初始化
    * 初始化 pom.xml
    * 接入 `maven-checkstyle-plugin`
    * 接入 `org.junit.jupiter`
    * 接入 `CircleCI`
2. 完成 `Crawler` 基本逻辑
    * 可以把某个 url 作为起点，遍历页面上的所有 a 标签，访问这些 a 标签，查找页面上的目标内容，递归执行。
    * 介入 `SpotBugs Plugin`
    * 使用 H2 数据库，使用 JDBC，做到可以断点续传功能。
