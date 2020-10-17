package cool.zhangzihao;

public class Main {

    public static void main(String[] args) {
        Crawler crawler = new Crawler(new SinaCrawlerConfig());
        crawler.start();
    }
}
