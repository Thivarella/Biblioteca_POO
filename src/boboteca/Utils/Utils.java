package boboteca.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utils {

    public static int getMaxId(String table){
        Connection conn = null;
        try {
            Integer id = 1;
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT MAX(id) FROM " + table;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
               id += resultSet.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }
}
