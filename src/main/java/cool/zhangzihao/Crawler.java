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
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Crawler {
    private CrawlerConfig crawlerConfig;

    public Crawler(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public void start() {
        try {
            while (true) {
                LinkedList<String> linksPool = new LinkedList<>(this.crawlerConfig.getInitToBeProcessedLinks());
                if (linksPool.isEmpty()) {
                    break;
                }
                String link = this.getFirstLinkAndRemoveIt(linksPool);
                if (crawlerConfig.isLinkProcessed(link)) {
                    continue;
                }
                if (crawlerConfig.isTargetPage(link)) {
                    String pageHtmlData = this.getPageData(link);
                    if (pageHtmlData != null) {
                        Document document = Jsoup.parse(pageHtmlData);
                        this.saveAllHrefsInPage(document);
                        crawlerConfig.onParsePage(document, link);
                    }
                    this.putLinkToProcessedPool(link);
                    continue;
                }
                this.putLinkToProcessedPool(link);
            }
        } finally {
            crawlerConfig.onCrawlerComplete();
        }
    }

    public void saveAllHrefsInPage(Document document) {
        crawlerConfig.onPutAllHrefsToBeProcessedPoll(
                document.select("a").stream().map(element -> element.attr("href"))
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

    public String getFirstLinkAndRemoveIt(LinkedList<String> linksPool) {
        String firstLink = linksPool.remove();
        crawlerConfig.onPollLink(firstLink);
        return firstLink;
    }
}
