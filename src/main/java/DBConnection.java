import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    private static String dbName = "finder";
    private static String dbUser = "root";
    private static String dbPass = "testtest";

    private static StringBuilder insertQuery = new StringBuilder();

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
                        "PRIMARY KEY(id))");//, " +
                        //"UNIQUE KEY name(`path`(50)))");
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
        System.out.println(sql);
        synchronized (getConnection()) {DBConnection.getConnection().createStatement().execute(sql);}


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

}
