import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Page {
    private String url;
    private int level;
    private int statusCode;
    private List<Page> childrenPages;
    private static HashSet<String> visitLinks = new HashSet<>();
    private static int lethBaseUrl;
    public Page(String url, int level, List<Page> childrenPages) {
        this(url, level);
        this.childrenPages = childrenPages;

    }

    public Page(String url, int level) {

        this.url = url;
        this.level = level;
        childrenPages = null;
        visitLinks.add(url);

        if (lethBaseUrl == 0)
        {
            lethBaseUrl = url.length();
        }
    }

    public Page(String url) {

        this(url, 0, new ArrayList<>());


    }

    public Page() {
        childrenPages = new ArrayList<>();
    }

    public boolean contains(String url){
        return visitLinks.contains(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Page> getChildrenPages() {
        return childrenPages;
    }

    public void setChildrenPages(List<Page> childrenPages) {
        this.childrenPages = childrenPages;
    }

    @Override
    public String toString() {

        String s = "".concat("\t".repeat(Math.max(0, getLevel()))).concat(url).concat("\n");

        return s;

    }

    public void addPage(Page page) {
        childrenPages.add(page);

    }


    public Document readSite(String url) {

        Document doc = null;

        try {
            setStatusCode(url);
            if (statusCode == 200)
            doc = Jsoup.connect(url)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .ignoreContentType(true)
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public void setStatusCode (String url) throws IOException {

       Connection.Response response = Jsoup.connect(url)
               .userAgent("Chrome/4.0.249.0 Safari/532.5")
               .referrer("http://www.google.com")
               .ignoreContentType(true)
               .timeout(10000)
               .execute();
        statusCode = response.statusCode();

    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getTrimUrl(){
        //tring tmpUrl = url.substring(url.indexOf("//")+2);
        //System.out.println(tmpUrl.substring(tmpUrl.indexOf('/')) +" N " + url);

        //url.substring(lethBaseUrl);
        return url.substring(lethBaseUrl-1);


    }

}

