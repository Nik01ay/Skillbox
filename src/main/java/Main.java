
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;


public class Main {
    static final String siteUrl = //"https://lenta.ru/";
                                  "http://imkp.ru/";
                                   // "https://yandex.ru/";
                                    //"https://skillbox.ru/";

    static final int coreCount= Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws SQLException {
/*

        DBConnection.addPage("www.fge1.ru", 201, "11fqewg gffdsag fdsg");
        DBConnection.addPage("www.njgfdc2.ru", 202, "22fqewg gffdsag fdsg");
        DBConnection.addPage("www.wer3.ru", 203, "33fqewg gffdsag fdsg");
        DBConnection.executeMultiInsert();
        System.out.println("execute");
*/

        Page page = new Page(siteUrl); //, 0, new ArrayList<>());
        System.out.println(1);
        String map = (String) new ForkJoinPool(coreCount).invoke(new PageTask(page));
        System.out.println(2);
        System.out.println(coreCount);
        System.out.println(3);
        System.out.println(map);
        System.out.println(4);

    }
}

