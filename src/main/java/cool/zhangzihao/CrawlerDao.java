package cool.zhangzihao;

public interface CrawlerDao {
    void closeDatabase();

    void insertNewsToDatabase(String title, String article, String link);

    void deleteLinkFromDB(String link);

    String getNextBeProcessedLinkFromDatabase();

    void insertLinkToTable(String tableName, String link);

    boolean checkIsLinkExistFromDatabase(String link);
}
