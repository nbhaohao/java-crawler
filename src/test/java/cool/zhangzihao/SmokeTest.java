package cool.zhangzihao;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SmokeTest {
    @Test
    public void test() {
        try {
            Crawler crawler = new Crawler(new SinaCrawlerConfig());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }
}
