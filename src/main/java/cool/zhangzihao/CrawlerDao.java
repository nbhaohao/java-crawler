package cool.zhangzihao;

public interface CrawlerDao {
    void closeDatabase();

    void insertNewsToDatabase(String title, String article, String link);

    String getNextBeProcessedLinkFromDatabase();

    void insertLinkToBeProcessedTable(String link);

    void insertLinkToProcessedTable(String link);

    boolean checkIsLinkExistFromDatabase(String link);
}
