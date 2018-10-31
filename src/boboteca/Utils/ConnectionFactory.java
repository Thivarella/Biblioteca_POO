package boboteca.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public ConnectionFactory(){}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/boboteca", "SA", "");
        } catch (SQLException var1) {
            throw new RuntimeException(var1);
        }
    }

    public static void close(Connection conn) {
        try {
            conn.close();
        } catch (SQLException var2) {
            throw new RuntimeException(var2);
        }
    }
}
