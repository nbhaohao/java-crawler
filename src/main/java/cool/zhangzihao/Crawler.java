package cool.zhangzihao;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;

public class Crawler {
    private final CrawlerConfig crawlerConfig;

    public Crawler(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public void start() {
        try {
            String beProcessedLink;
            while ((beProcessedLink = crawlerConfig.pullNextBeProcessedLink()) != null) {
                if (crawlerConfig.isLinkProcessed(beProcessedLink)) {
                    continue;
                }
                if (crawlerConfig.isTargetPage(beProcessedLink)) {
                    String pageHtmlData = this.getPageData(beProcessedLink);
                    if (pageHtmlData != null) {
                        Document document = Jsoup.parse(pageHtmlData);
                        this.saveAllHrefsInPage(document);
                        crawlerConfig.onParsePage(document, beProcessedLink);
                    }
                }
                this.putLinkToProcessedPool(beProcessedLink);
            }
        } finally {
            crawlerConfig.onCrawlerComplete();
        }
    }

    public void saveAllHrefsInPage(Document document) {
        crawlerConfig.onPutAllHrefsToBeProcessedPoll(
                document.select("a").stream()
                        .map(element -> element.attr("href"))
                        .filter(hrefString -> !hrefString.toLowerCase().equals("javascript:void(0)"))
                        .collect(Collectors.toList())
        );
    }

    public String getPageData(String link) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(link);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    return EntityUtils.toString(entity);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return null;
    }

    public void putLinkToProcessedPool(String link) {
        crawlerConfig.onPutProcessedLink(link);
    }
}
