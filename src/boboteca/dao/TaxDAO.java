package boboteca.dao;

import boboteca.model.*;
import boboteca.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static boboteca.utils.Utils.prepareStatement;


public class TaxDAO {
    private Connection conn = null;

    public void insertTax(Tax tax){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO taxes (id, book_id, user_id, loan_id, description, value, is_paid) VALUES (?,?,?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            prepareStatement(ps , tax.getId(), tax.getBook(), tax.getUser());
            ps.setInt(4,tax.getLoan().getId());
            ps.setString(5,tax.getDescription());
            ps.setDouble(6,tax.getValue());
            ps.setBoolean(7, tax.getPaid());
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

    public List<Tax> findAllTaxByUserId(Integer userId, Boolean isPaid) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT t.ID, b.id, b.name, b.author, b.category, b.year, p.id, p.LABEL, b.disponibility, u.id, u.NAME, g.ID, g.LABEL, c.ID, c.LABEL, c.VALUE,c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN , l.id, l.LOAN_DATE, l.RETURN_DATE, l.IS_RETURNED, l.RETURNED_DATE, t.DESCRIPTION, t.VALUE, t.IS_PAID FROM taxes t JOIN books b ON b.id = t.book_id JOIN user u ON u.id = t.user_id JOIN loans l ON l.id = t.loan_id JOIN address a ON a.ID = u.ADDRESS_ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN GENDER G on u.GENDER_ID = G.ID JOIN PRIORITY p ON p.id = b.PRIORITY_ID WHERE user_id=? ";
            if(isPaid != null){
                sql += "AND is_paid=?";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            if(isPaid != null){
                ps.setBoolean(2, isPaid);
            }
            return getTaxes(ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    private List<Tax> getTaxes(PreparedStatement ps) throws SQLException {
        ResultSet resultSet = ps.executeQuery();
        return getTax(resultSet);
    }

    private List<Tax> getTax(ResultSet resultSet) throws SQLException {
        List<Tax> taxList = new ArrayList<>();
        while (resultSet.next()) {
            Book book = new Book(resultSet.getInt(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getString(6),new Generic(resultSet.getInt(7),resultSet.getString(8)),resultSet.getBoolean(9));
            Generic generic = new Generic(resultSet.getInt(14),resultSet.getString(15),resultSet.getInt(16),resultSet.getDouble(17));
            User user = new User(resultSet.getInt(10),resultSet.getString(11), new Generic(resultSet.getInt(12),resultSet.getString(13)),generic,new Address(resultSet.getInt(18),resultSet.getString(19),resultSet.getInt(20),resultSet.getString(21),resultSet.getString(22),resultSet.getString(23),resultSet.getString(24),resultSet.getString(25)),resultSet.getString(26),resultSet.getBoolean(27));
            Loan loan = new Loan(resultSet.getInt(28),book,user,resultSet.getDate(29),resultSet.getDate(30),resultSet.getBoolean(31));
            loan.setReturnedDate(resultSet.getDate(32));
            taxList.add(new Tax(
                    resultSet.getInt(1),
                    book,
                    user,
                    loan,
                    resultSet.getString(33),
                    resultSet.getDouble(34),
                    resultSet.getBoolean(35)));
        }
        return taxList;
    }

    public List<Tax> findAllTax() {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT t.ID, b.id, b.name, b.author, b.category, b.year, p.id, p.LABEL, b.disponibility, u.id, u.NAME, g.ID, g.LABEL, c.ID, c.LABEL,c.VALUE,c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN , l.id, l.LOAN_DATE, l.RETURN_DATE, l.IS_RETURNED, l.RETURNED_DATE, t.DESCRIPTION, t.VALUE, t.IS_PAID FROM taxes t JOIN books b ON b.id = t.book_id JOIN user u ON u.id = t.user_id JOIN loans l ON l.id = t.loan_id JOIN address a ON a.ID = u.ADDRESS_ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN GENDER G on u.GENDER_ID = G.ID JOIN PRIORITY p ON p.id = b.PRIORITY_ID;";
            PreparedStatement ps = conn.prepareStatement(sql);
            return getTaxes(ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }
}
