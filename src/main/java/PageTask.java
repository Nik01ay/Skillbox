import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class PageTask extends RecursiveTask {

    private Page page;

    private final String url;

    public PageTask(Page page) {
        this.page = page;
        url = page.getUrl();

    }
    // когда заканчиваются все потоки записать в бд

    @Override
    protected String compute() {

        List<PageTask> taskList = new ArrayList<>();
        if (url.endsWith(".pdf")) {
            return page.toString();
        }
        final Document doc = page.readSite(url);

        try {

            DBConnection.addPage(page.getTrimUrl(), page.getStatusCode(), page.getStatusCode() == 200 ? doc.html().replaceAll("'","\"") : " ошибка соед ");
            // todo сделать блок добавления текста из тегов title и body
            DBConnection.addLemms(doc.text());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if (doc == null) {
            return "";
        }

        Elements links = doc.select("a");

        links.forEach(link -> {

            final String href = link.attr("abs:href");
            if (href.contains(url)) {

                if (!page.contains(href)) {
                    Page child;
                    try {
                        child = new Page(href, page.getLevel() + 1, new ArrayList<>());

                    } catch (Exception ignored) {
                        // тут просто добавляем битую ссылку, вызов которой приводит к ошибке.
                        child = new Page(href, page.getLevel() + 1);
                    }
                    page.addPage(child);
                    // todo тут создаю новый процесс
                    PageTask task = new PageTask(child);

                    try {
                        Thread.sleep(130);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    task.fork();
                    taskList.add(task);

                    //}

                }
            }

        });


        StringBuilder mapSite = new StringBuilder(page.toString());


        for (PageTask task : taskList) {
            mapSite.append((String) task.join());
        }


        return mapSite.toString();
    }
}
