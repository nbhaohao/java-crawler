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
3. 接入 `Flyway`
    * 使用 `Flyway` 完成自动化初始化数据库环境。
4. 接入 `MyBatis`
    * 抽取 `DatabaseAccessObject` 将 JDBC 的代码抽取出来，抽取 `DAO` 接口，方便后续替换 JDBC 为 ORM。
    * 使用 `MyBatis` 用 ORM 的方式来重构之前的 JDBC 代码。
5. 使用 MySQL
    * 使用 Docker 启动一个 MySQL 数据库，来代替之前的 H2 数据库。
    * MySQL 中文乱码问题：
        * 数据库使用 `alter database character set utf8mb4 collate utf8mb4_unicode_ci;`
        * jdbc url 要携带相关参数， jdbc:mysql://localhost:3306/news?characterEncoding=utf8