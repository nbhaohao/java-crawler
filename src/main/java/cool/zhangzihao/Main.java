package cool.zhangzihao;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        Crawler crawler = new Crawler(new SinaCrawlerConfig());
        crawler.start();
    }
}
