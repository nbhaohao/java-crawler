package cool.zhangzihao;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class SinaCrawlerConfig implements CrawlerConfig {
    public static final String SINA_INIT_LINK_URL = "https://sina.cn";
    //        private final CrawlerDao dao = new JdbcCrawlerDao();
    private final CrawlerDao dao = new MyBatisCrawlerDao();

    @Override
    public boolean isTargetPage(String link) {
        return (link.equals(SINA_INIT_LINK_URL) || link.contains("news.sina.cn")) && !link.contains("passport.sina.cn");
    }

    @Override
    public void onParsePage(Document document, String link) {
        Element articleElement = document.selectFirst(".art_content");
        if (articleElement != null) {
            Element titleElement = document.selectFirst(".art_tit_h1");
            String titleContent = titleElement != null ? titleElement.text() : "无标题";
            dao.insertNewsToDatabase(titleContent, articleElement.text(), link);
            System.out.println(articleElement.text());
        }
    }

    @Override
    public void onCrawlerComplete() {
        dao.closeDatabase();
    }

    @Override
    public String pullNextBeProcessedLink() {
        return dao.getNextBeProcessedLinkFromDatabase();
    }

    @Override
    public void onPutProcessedLink(String link) {
        dao.insertLinkToProcessedTable(link);
    }

    @Override
    public void onPutAllHrefsToBeProcessedPoll(List<String> links) {
        links.forEach(dao::insertLinkToBeProcessedTable);
    }

    @Override
    public boolean isLinkProcessed(String link) {
        return dao.checkIsLinkExistFromDatabase(link);
    }
}
