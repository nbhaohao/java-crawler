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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public class Crawler {
    private CrawlerConfig crawlerConfig;
    private LinkedList<String> linksPool;
    private Set<String> processedSet;

    public Crawler(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
        linksPool = new LinkedList<>();
        processedSet = new HashSet<>();
    }

    public Crawler(CrawlerConfig crawlerConfig, String... startLinks) {
        this(crawlerConfig);
        linksPool.addAll(Arrays.asList(startLinks));
    }


    public void start() {
        while (!this.isNoMoreLink()) {
            String link = this.getFirstLinkAndRemoveIt();
            if (this.isLinkProcessed(link)) {
                continue;
            }
            if (crawlerConfig.isTargetPage(link)) {
                String pageHtmlData = this.getPageData(link);
                if (pageHtmlData != null) {
                    Document document = Jsoup.parse(pageHtmlData);
                    this.saveAllHrefsInPage(document);
                    crawlerConfig.onParsePage(document);
                }
                this.addLinkToProcessedSet(link);
                continue;
            }
            this.addLinkToProcessedSet(link);
        }
    }

    public void saveAllHrefsInPage(Document document) {
        linksPool.addAll(document.select("a").stream().map(element -> element.attr("href")).collect(Collectors.toList()));
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

    public void addLinkToProcessedSet(String link) {
        processedSet.add(link);
    }

    public String getFirstLinkAndRemoveIt() {
        return linksPool.remove();
    }

    public boolean isLinkProcessed(String link) {
        return processedSet.contains(link);
    }

    public boolean isNoMoreLink() {
        return linksPool.isEmpty();
    }
}
