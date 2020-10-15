package cool.zhangzihao;

import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.List;

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
     * @param link     链接
     */
    void onParsePage(Document document, String link);

    /**
     * 爬虫完成后的回调
     */
    void onCrawlerComplete();

    /**
     * 从待处理池子中取出元素的回调
     *
     * @param link 链接
     */
    void onPollLink(String link);

    /**
     * 保存已处理过的链接
     *
     * @param link 链接
     */
    void onPutProcessedLink(String link);

    void onPutAllHrefsToBeProcessedPoll(List<String> links);

    /**
     * 获取初始化要处理的链接
     *
     * @return 要处理的链接的集合
     */
    Collection<String> getInitToBeProcessedLinks();

    /**
     * 判断这个链接是否被处理过
     *
     * @param link 链接
     * @return 是否被处理过
     */
    boolean isLinkProcessed(String link);
}
