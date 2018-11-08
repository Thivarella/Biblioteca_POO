package boboteca.DAO;

import boboteca.Model.Loan;
import boboteca.Utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class LoanDAO {
    Connection conn = null;
    public void insertLoan(Loan loan){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO loans (id, book_id, user_id, loan_date, return_date) VALUES (?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,loan.getId());
            ps.setInt(2,loan.getBook().getId());
            ps.setInt(3,loan.getUser().getId());
            ps.setDate(4,loan.getLoanDate());
            ps.setDate(5,loan.getReturnDate());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void updateLoan(Loan loan){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE loans SET book_id=?, user_id=?, loan_date=?, return_date=? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,loan.getBook().getId());
            ps.setInt(2,loan.getUser().getId());
            ps.setDate(3,loan.getLoanDate());
            ps.setDate(4,loan.getReturnDate());
            ps.setInt(5,loan.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Loan> findAllLoanByUserId(Integer userId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM loans WHERE user_id=? AND is_returned = false;";
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
            String sql = "SELECT * FROM loans";
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
        return new Loan(
                resultSet.getInt(1),
                new BookDAO().findBookById(resultSet.getInt(2)),
                new UserDAO().findUserById(resultSet.getInt(3)),
                resultSet.getDate(4),
                resultSet.getDate(5),
                resultSet.getBoolean(6));
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
}
