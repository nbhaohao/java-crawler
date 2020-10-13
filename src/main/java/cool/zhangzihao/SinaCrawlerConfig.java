package cool.zhangzihao;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SinaCrawlerConfig implements CrawlerConfig {
    public static final String SINA_INIT_LINK_URL = "https://sina.cn/";

    @Override
    public boolean isTargetPage(String link) {
        return (link.equals(SINA_INIT_LINK_URL) || link.contains("news.sina.cn")) && !link.contains("passport.sina.cn");
    }

    @Override
    public void onParsePage(Document document) {
        Element articleElement = this.getArticleFromSinaPage(document);
        if (articleElement != null) {
            System.out.println(articleElement.text());
        }
    }

    public Element getArticleFromSinaPage(Document document) {
        return document.selectFirst(".art_content");
    }
}
