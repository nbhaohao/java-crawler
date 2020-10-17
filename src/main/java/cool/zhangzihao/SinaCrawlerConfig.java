package cool.zhangzihao;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class SinaCrawlerConfig implements CrawlerConfig {
    public static final String SINA_INIT_LINK_URL = "https://sina.cn";
    private final CrawlerDao dao = new JdbcCrawlerDao();

    @Override
    public boolean isTargetPage(String link) {
        return (link.equals(SINA_INIT_LINK_URL) || link.contains("news.sina.cn")) && !link.contains("passport.sina.cn");
    }

    @Override
    public void onParsePage(Document document, String link) {
        Element articleElement = document.selectFirst(".art_content");
        if (articleElement != null) {
            Element titleElement = document.selectFirst(".art_tit_h1");
            dao.insertNewsToDatabase(titleElement.text(), articleElement.text(), link);
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
        dao.insertLinkToTable("LINKS_ALREADY_PROCESSED", link);
    }

    @Override
    public void onPutAllHrefsToBeProcessedPoll(List<String> links) {
        links.forEach(link -> dao.insertLinkToTable("LINKS_TO_BE_PROCESSED", link));
    }

    @Override
    public boolean isLinkProcessed(String link) {
        return dao.checkIsLinkExistFromDatabase(link);
    }
}
