package cool.zhangzihao;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SinaCrawlerConfig implements CrawlerConfig {
    public static final String SINA_INIT_LINK_URL = "https://sina.cn";
    private static final String DB_USER_NAME = "root";
    private static final String DB_USER_PASS = "123456";
    private final Connection connection;

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public SinaCrawlerConfig() throws SQLException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        String jdbcUrl = "jdbc:h2:file:" + new File(projectDir, "news").getAbsolutePath();
        connection = DriverManager.getConnection(jdbcUrl, DB_USER_NAME, DB_USER_PASS);
    }

    @Override
    public boolean isTargetPage(String link) {
        return (link.equals(SINA_INIT_LINK_URL) || link.contains("news.sina.cn")) && !link.contains("passport.sina.cn");
    }

    @Override
    public void onParsePage(Document document, String link) {
        Element articleElement = document.selectFirst(".art_content");
        if (articleElement != null) {
            Element titleElement = document.selectFirst(".art_tit_h1");
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement("insert into NEWS (TITLE, CONTENT, URL) VALUES (?, ?, ?);")) {
                preparedStatement.setString(1, titleElement.text());
                preparedStatement.setString(2, articleElement.text());
                preparedStatement.setString(3, link);
                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw new RuntimeException(throwables);
            }
            System.out.println(articleElement.text());
        }
    }

    @Override
    public void onCrawlerComplete() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public void onPollLink(String link) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from LINKS_TO_BE_PROCESSED where LINK = ?;")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
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
    public void onPutProcessedLink(String link) {
        this.insertLinkToTable("LINKS_ALREADY_PROCESSED", link);
    }

    @Override
    public void onPutAllHrefsToBeProcessedPoll(List<String> links) {
        links.forEach(link -> this.insertLinkToTable("LINKS_TO_BE_PROCESSED", link));
    }

    @Override
    public Collection<String> getInitToBeProcessedLinks() {
        try {
            return getLinksFromDB(connection, "LINKS_TO_BE_PROCESSED");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public boolean isLinkProcessed(String link) {
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

    public static List<String> getLinksFromDB(Connection connection, String tableName) throws SQLException {
        List<String> data = new ArrayList<>();
        try (
                PreparedStatement preparedStatement = connection.prepareStatement("select link\n" +
                        "from " + tableName + ";")) {
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                while (resultSet.next()) {
                    data.add(resultSet.getString(1));
                }
            }
        }
        return data;
    }
}
