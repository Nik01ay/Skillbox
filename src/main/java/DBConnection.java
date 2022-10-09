import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class DBConnection {
    private static Connection connection;

    private static String dbName = "finder";
    private static String dbUser = "root";
    private static String dbPass = "testtest";

    private static StringBuilder insertQuery = new StringBuilder();
    private static StringBuilder insertLemms = new StringBuilder();

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName + "?autoReconnect=true&useSSL=false",//"?useSSL=false/" +
                        dbUser, dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS `pages`");
                connection.createStatement().execute("CREATE TABLE `pages`(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "`path` TINYTEXT NOT NULL, " +
                        "code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "UNIQUE KEY (`path`(50)))");

                connection.createStatement().execute("DROP TABLE IF EXISTS `field`");
                connection.createStatement().execute("CREATE TABLE `field`(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "selector VARCHAR(255) NOT NULL, " +
                        "weight FLOAT NOT NULL, " +
                        "PRIMARY KEY(id)) ");
                connection.createStatement().execute("INSERT INTO field(name, selector, weight) " +
                        "VALUES" +
                        "('title', 'title', 1), " +
                        "('body', 'body', 0.8)");

                connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
                connection.createStatement().execute("CREATE TABLE lemma(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "lemma VARCHAR(255) NOT NULL, " +
                        "frequency INT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "UNIQUE KEY (`lemma`(50)))");

                connection.createStatement().execute("DROP TABLE IF EXISTS `index`");
                connection.createStatement().execute("CREATE TABLE `index`(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "page_id INT NOT NULL, " +
                        "lemma_id INT NOT NULL, " +
                        "`rank` FLOAT NOT NULL, " +
                        "PRIMARY KEY(id))");


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void executeMultiInsert() throws SQLException {
        String sql = "INSERT INTO pages(`path`, code, content) " +
                "VALUES" + insertQuery.toString();
        //"ON DUPLICATE KEY UPDATE `count`=`count` + 1";
        //System.out.println(sql);
        synchronized (getConnection()) {
            DBConnection.getConnection().createStatement().execute(sql);
        }
    }

    public static void executeMultiInsertToLemms() throws SQLException {
        String sql = "INSERT INTO lemma(lemma, frequency) " +
                "VALUES" + insertLemms.toString() +
                "ON DUPLICATE KEY UPDATE `frequency`=`frequency` + 1";
        System.out.println(sql);
        synchronized (getConnection()) {
            DBConnection.getConnection().createStatement().execute(sql);
        }
    }

    public static void addPage(String path, int code, String content) throws SQLException {
        //content = content.replace('.', '-');

        synchronized (insertQuery) {
            insertQuery.append((insertQuery.length() == 0 ? "" : ",") +
                    "('" + path + "', " + code + ", '" + content + "')");

            if (insertQuery.length() > 1048576) {
                executeMultiInsert();
                System.out.println(">1048576");
                insertQuery.setLength(0);
            }
        }
    }

    public static void addLemms(String content) {
// todo дописать добавления в rank
        try {
            Set<String> lemmaSet = LemmaFinder.getInstance().getLemmaSet(content);
            lemmaSet.stream().forEach(s -> {
                synchronized (insertLemms) {
                    insertLemms.append((insertLemms.length() == 0 ? "" : ", ") +
                            "('" + s + "', " +
                            "1" + ")"
                    );
                }
            });
            if (insertLemms.length() > 1048576) {
                executeMultiInsertToLemms();
                System.out.println(">1048576 lems");
                insertLemms.setLength(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
