package boboteca.DAO;

import boboteca.Model.Book;
import boboteca.Model.Tax;
import boboteca.Utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TaxDAO {
    Connection conn = null;
    public void insertTax(Tax tax){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO tax (id, book_id, user_id, description, value) VALUES (?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,tax.getId());
            ps.setInt(2,tax.getBook().getId());
            ps.setInt(3,tax.getUser().getId());
            ps.setString(4,tax.getDescription());
            ps.setDouble(5,tax.getValue());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void updateTax(Tax tax){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE tax SET book_id=?, user_id=?, description=?, value=? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,tax.getBook().getId());
            ps.setInt(2,tax.getUser().getId());
            ps.setString(4,tax.getDescription());
            ps.setDouble(5,tax.getValue());
            ps.setInt(5,tax.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void removeTax(int taxId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM tax WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,taxId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

//    public List<Tax> findAllTax(){
//        try {
//            conn = ConnectionFactory.getConnection();
//            String sql = "SELECT * FROM tax";
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ResultSet resultSet = ps.executeQuery();
//            List<Tax> taxList = new ArrayList<>();
//            while (resultSet.next()){
//                taxList.add(getTax(resultSet));
//            }
//            return taxList;
//        } catch (SQLException e) {
//            throw new RuntimeException();
//        }finally {
//            ConnectionFactory.close(conn);
//        }
//    }

//    public Tax findAdressById(Integer adressId) {
//        try {
//            conn = ConnectionFactory.getConnection();
//            String sql = "SELECT * FROM adress WHERE id=?";
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ps.setInt(1,adressId);
//            ResultSet resultSet = ps.executeQuery();
//            return getTax(resultSet);
//        } catch (SQLException e) {
//            throw new RuntimeException();
//        }finally {
//            ConnectionFactory.close(conn);
//        }
//    }

//    private Tax getTax(ResultSet resultSet) throws SQLException {
//        return new Tax(
//                resultSet.getInt(1),
//                new BookDAO().findBookById(resultSet.getInt(2)),
//                new UserDAO().findUserById(resultSet.getInt(3)),
//                resultSet.getString(4),
//                resultSet.getDouble(5));
//    }
}
