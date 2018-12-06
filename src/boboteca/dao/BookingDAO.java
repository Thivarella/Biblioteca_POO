package boboteca.dao;

import boboteca.model.*;
import boboteca.proxy.ProxyBookDAO;
import boboteca.proxy.ProxyUserDAO;
import boboteca.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static boboteca.utils.Utils.getIntegerWithStatement;
import static boboteca.utils.Utils.prepareStatement;


public class BookingDAO {
    private Connection conn = null;
    private ProxyBookDAO bookDAO = ProxyBookDAO.getInstance();
    private ProxyUserDAO userDAO = ProxyUserDAO.getInstance();

    public void insertBooking(Booking booking) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO booking (id, book_id, user_id, booking_date, status) VALUES (?,?,?,?,?);";

            PreparedStatement ps = conn.prepareStatement(sql);
            prepareStatement(ps, booking.getId(), booking.getBook(), booking.getUser());
            ps.setDate(4, booking.getBookingDate());
            ps.setBoolean(5, booking.getStaus());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }


    public void updateBooking(Booking booking){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE booking SET book_id=?, user_id=?, booking_date=?, status=? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,booking.getBook().getId());
            ps.setInt(2,booking.getUser().getId());
            ps.setDate(3,booking.getBookingDate());
            ps.setBoolean(4,booking.getStaus());
            ps.setInt(5,booking.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void removeBooking(int bookingId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM booking WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,bookingId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Booking> findAllBooking(){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT bo.ID, b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY, u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL, c.VALUE,c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN, bo.BOOKING_DATE, bo.STATUS FROM BOOKING bo JOIN books b ON b.ID = bo.BOOK_ID JOIN PRIORITY p ON p.ID = b.PRIORITY_ID JOIN user u ON u.id = bo.USER_ID JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID;";
            PreparedStatement ps = conn.prepareStatement(sql);
            return getBookings(ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Booking> findAllBookingByUserId(Integer userId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT bo.ID, b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY, u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL, c.VALUE, c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN, bo.BOOKING_DATE, bo.STATUS FROM BOOKING bo JOIN books b ON b.ID = bo.BOOK_ID JOIN PRIORITY p ON p.ID = b.PRIORITY_ID JOIN user u ON u.id = bo.USER_ID JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID WHERE user_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,userId);
            return getBookings(ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private List<Booking> getBookings(PreparedStatement ps) throws SQLException {
        ResultSet resultSet = ps.executeQuery();
        List<Booking> bookingList = new ArrayList<>();
        while (resultSet.next()){
            bookingList.add(getBooking(resultSet));
        }
        return bookingList;
    }

    private Booking getBooking(ResultSet resultSet) throws SQLException {
        Book book = new Book(resultSet.getInt(2),resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getString(6),new Generic(resultSet.getInt(7),resultSet.getString(8)),resultSet.getBoolean(9));
        Generic generic = new Generic(resultSet.getInt(14),resultSet.getString(15),resultSet.getInt(16),resultSet.getDouble(17));
        User user = new User(resultSet.getInt(10),resultSet.getString(11), new Generic(resultSet.getInt(12),resultSet.getString(13)),generic,new Address(resultSet.getInt(18),resultSet.getString(19),resultSet.getInt(20),resultSet.getString(21),resultSet.getString(22),resultSet.getString(23),resultSet.getString(24),resultSet.getString(25)),resultSet.getString(26),resultSet.getBoolean(27));
        return new Booking(
                resultSet.getInt(1),
                book,
                user,
                resultSet.getDate(28),
                resultSet.getBoolean(29));
    }

    public Integer findBookAlreadyBooked(Integer userId, Integer bookId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(id) FROM booking WHERE user_id = ? AND book_id = ? AND status = true ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(2, bookId);
            return getIntegerWithStatement(userId, ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public Integer findReservedBook(Integer bookId, Integer userId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(id) FROM booking WHERE user_id != ? AND book_id = ? AND status = true ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(2, bookId);
            return getIntegerWithStatement(userId, ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void findBookingByBookIdAndStatusTrue(Integer bookId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(id) FROM booking WHERE book_id = ? AND status = true ";
            PreparedStatement ps = conn.prepareStatement(sql);
            if(getIntegerWithStatement(bookId, ps) > 0){
                String updateSql = "UPDATE booking SET status = false WHERE book_id = ?";
                PreparedStatement ps2 = conn.prepareStatement(updateSql);
                ps2.setLong(1, bookId);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }
}
