package cool.zhangzihao;

public class Main {

    public static void main(String[] args) {
        SinaCrawlerConfig sinaCrawlerConfig = new SinaCrawlerConfig();
        for (int i = 0; i < 8; i++) {
            new Crawler(sinaCrawlerConfig).start();
        }
    }
}
