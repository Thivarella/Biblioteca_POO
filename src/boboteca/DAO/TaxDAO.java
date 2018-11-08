package boboteca.DAO;

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
            String sql = "INSERT INTO taxes (id, book_id, user_id, description, value, is_paid) VALUES (?,?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,tax.getId());
            ps.setInt(2,tax.getBook().getId());
            ps.setInt(3,tax.getUser().getId());
            ps.setString(4,tax.getDescription());
            ps.setDouble(5,tax.getValue());
            ps.setBoolean(6, tax.getPaid());
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
            String sql = "UPDATE taxes SET is_paid = ? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, tax.getPaid());
            ps.setInt(2, tax.getId());
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
            String sql = "DELETE FROM taxes WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,taxId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Tax> findAllTaxByUserId(Integer userId, Boolean isPaid) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM taxes WHERE user_id=? AND is_paid=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setBoolean(2, isPaid);
            ResultSet resultSet = ps.executeQuery();
            List<Tax> taxList = new ArrayList<>();
            while (resultSet.next()) {
                taxList.add(getTax(resultSet));
            }
            return taxList;
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    private Tax getTax(ResultSet resultSet) throws SQLException {
        return new Tax(
                resultSet.getInt(1),
                new BookDAO().findBookById(resultSet.getInt(2)),
                new UserDAO().findUserById(resultSet.getInt(3)),
                resultSet.getString(4),
                resultSet.getDouble(5),
                resultSet.getBoolean(6));
    }
}
