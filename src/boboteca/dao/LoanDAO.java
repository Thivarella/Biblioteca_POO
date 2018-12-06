package boboteca.dao;

import boboteca.model.*;
import boboteca.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static boboteca.utils.Utils.prepareStatement;


public class LoanDAO {
    private Connection conn = null;

    public void insertLoan(Loan loan){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO loans (id, book_id, user_id, loan_date, return_date) VALUES (?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            prepareStatement(ps, loan.getId(), loan.getBook(), loan.getUser());
            ps.setDate(4,loan.getLoanDate());
            ps.setDate(5,loan.getReturnDate());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Loan> findAllLoanByUserId(Integer userId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT l.ID, b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY, u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL, c.VALUE, c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN, l.LOAN_DATE, l.RETURN_DATE, l.IS_RETURNED, l.RETURNED_DATE FROM loans l JOIN books b ON b.ID = l.BOOK_ID JOIN PRIORITY p ON p.ID = b.PRIORITY_ID JOIN user u ON u.id = l.USER_ID JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID WHERE user_id=? AND is_returned = false;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();
            List<Loan> loanList = new ArrayList<>();
            while (resultSet.next()) {
                loanList.add(getLoan(resultSet));
            }
            return loanList;
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Loan> findAllLoan() {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT l.ID, b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY, u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL, c.VALUE,c.ADDITIONAL_VALUE,  a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN, l.LOAN_DATE, l.RETURN_DATE, l.IS_RETURNED, l.RETURNED_DATE FROM loans l JOIN books b ON b.ID = l.BOOK_ID JOIN PRIORITY p ON p.ID = b.PRIORITY_ID JOIN user u ON u.id = l.USER_ID JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            List<Loan> loanList = new ArrayList<>();
            while (resultSet.next()) {
                loanList.add(getLoan(resultSet));
            }
            return loanList;
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    private Loan getLoan(ResultSet resultSet) throws SQLException {
        Book book = new Book(resultSet.getInt(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getString(6),new Generic(resultSet.getInt(7),resultSet.getString(8)),resultSet.getBoolean(9));
        Generic generic = new Generic(resultSet.getInt(14),resultSet.getString(15),resultSet.getInt(16),resultSet.getDouble(17));
        User user = new User(resultSet.getInt(10),resultSet.getString(11), new Generic(resultSet.getInt(12),resultSet.getString(13)),generic,new Address(resultSet.getInt(18),resultSet.getString(19),resultSet.getInt(20),resultSet.getString(21),resultSet.getString(22),resultSet.getString(23),resultSet.getString(24),resultSet.getString(25)),resultSet.getString(26),resultSet.getBoolean(27));
        Loan loan = new Loan(
                resultSet.getInt(1),
                book,
                user,
                resultSet.getDate(28),
                resultSet.getDate(29),
                resultSet.getBoolean(30));
        if (loan.getChecked()){ loan.setReturnedDate(resultSet.getDate(31));}
        return loan;
    }

    public void doBookReturn(Loan loan) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new java.util.Date());
            java.util.Date atualDate = calendar.getTime();
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE loans SET is_returned = ?, returned_date = ? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, loan.getChecked());
            ps.setDate(2, new java.sql.Date(atualDate.getTime()));
            ps.setInt(3, loan.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    public void doBookRenew(Loan loan) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(loan.getReturnDate());
            calendar.add(Calendar.DATE, 7);
            java.util.Date atualDate = calendar.getTime();
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE loans SET return_date = ? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(atualDate.getTime()));
            ps.setInt(2, loan.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    protected Loan findLoanById(int loanId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT l.ID, b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY, u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL, c.VALUE,c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN, l.LOAN_DATE, l.RETURN_DATE, l.IS_RETURNED, l.RETURNED_DATE FROM loans l JOIN books b ON b.ID = l.BOOK_ID JOIN PRIORITY p ON p.ID = b.PRIORITY_ID JOIN user u ON u.id = l.USER_ID JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,loanId);
            ResultSet resultSet = ps.executeQuery();
            Loan loan = new Loan();
            while (resultSet.next()) {
                loan = getLoan(resultSet);
            }
            return loan;
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }
}
