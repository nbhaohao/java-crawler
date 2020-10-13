package cool.zhangzihao;

import org.jsoup.nodes.Document;

public interface CrawlerConfig {
    /**
     * 判断是否是需要爬取数据的页面
     *
     * @param link 页面的链接
     * @return 是否需要爬取
     */
    boolean isTargetPage(String link);

    /**
     * 交由调用者自行处理每页的文档对象
     *
     * @param document 页面文档对象
     */
    void onParsePage(Document document);
}
