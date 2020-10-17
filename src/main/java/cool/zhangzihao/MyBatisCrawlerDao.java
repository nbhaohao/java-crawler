package cool.zhangzihao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MyBatisCrawlerDao implements CrawlerDao {

    private SqlSessionFactory sqlSessionFactory;
    public static final String MAPPER_NAME = "cool.zhangzihao.MyMapper";

    public MyBatisCrawlerDao() {
        String resource = "db/mybatis/config.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getMapperStatementName(String name) {
        return MAPPER_NAME + "." + name;
    }

    @Override
    public void closeDatabase() {

    }

    @Override
    public void insertNewsToDatabase(String title, String article, String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            News news = new News(title, article, link);
            session.insert(getMapperStatementName("insertNews"), news);
        }
    }

    public void deleteLinkFromDB(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.delete(getMapperStatementName("deleteLink"), link);
        }
    }

    @Override
    public synchronized String getNextBeProcessedLinkFromDatabase() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            String nextLink = session.selectOne(getMapperStatementName("selectNextBeProcessedLink"));
            if (nextLink != null) {
                deleteLinkFromDB(nextLink);
                return nextLink;
            }
        }
        return null;
    }

    public void insertLinkToTable(String tableName, String link) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", tableName);
        params.put("link", link);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert(getMapperStatementName("insertLink"), params);
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

    @Override
    public boolean checkIsLinkExistFromDatabase(String link) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            int result = session.selectOne(getMapperStatementName("selectLinkFromProcessedTable"), link);
            return result > 0;
        }
    }
}
