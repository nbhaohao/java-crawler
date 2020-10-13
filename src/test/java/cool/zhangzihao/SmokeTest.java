package cool.zhangzihao;

import org.junit.jupiter.api.Test;

public class SmokeTest {
    @Test
    public void test() {
        Crawler crawler = new Crawler(new SinaCrawlerConfig(), SinaCrawlerConfig.SINA_INIT_LINK_URL);
    }
}
