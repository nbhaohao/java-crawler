package cool.zhangzihao;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcCrawlerDao implements CrawlerDao {

    private static final String DB_USER_NAME = "root";
    private static final String DB_USER_PASS = "123456";
    private Connection connection;

    public JdbcCrawlerDao() {
        try {
            connectDatabase();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }


    @Override
    public void closeDatabase() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public void connectDatabase() throws SQLException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        String jdbcUrl = "jdbc:h2:file:" + new File(projectDir, "news").getAbsolutePath();
        connection = DriverManager.getConnection(jdbcUrl, DB_USER_NAME, DB_USER_PASS);
    }

    @Override
    public void insertNewsToDatabase(String title, String article, String link) {
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement("insert into NEWS (TITLE, CONTENT, URL) VALUES (?, ?, ?);")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, article);
            preparedStatement.setString(3, link);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }


    public void deleteLinkFromDB(String link) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from LINKS_TO_BE_PROCESSED where LINK = ?;")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public synchronized String getNextBeProcessedLinkFromDatabase() {
        try (
                PreparedStatement preparedStatement = connection.prepareStatement("select LINK from LINKS_TO_BE_PROCESSED limit 1;");
                ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                String linkResult = resultSet.getString(1);
                deleteLinkFromDB(linkResult);
                return linkResult;
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public void insertLinkToBeProcessedTable(String link) {
        insertLinkToTable("LINKS_TO_BE_PROCESSED", link);
    }

    @Override
    public void insertLinkToProcessedTable(String link) {
        insertLinkToTable("LINKS_ALREADY_PROCESSED", link);
    }

    public void insertLinkToTable(String tableName, String link) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into " + tableName + " (LINK) values (?);")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }


    @Override
    public boolean checkIsLinkExistFromDatabase(String link) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select LINK from LINKS_ALREADY_PROCESSED where LINK = ?;")) {
            preparedStatement.setString(1, link);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                return resultSet.next();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }
}
