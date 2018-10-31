package boboteca.DAO;

import boboteca.Model.Book;
import boboteca.Model.Booking;
import boboteca.Utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BookingDAO {
    Connection conn = null;
    public void insertBooking(Booking booking){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO booking (id, book_id, user_id, booking_date, status) VALUES (?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,booking.getId());
            ps.setInt(2,booking.getBook().getId());
            ps.setInt(3,booking.getUser().getId());
            ps.setDate(4,booking.getBookingDate());
            ps.setBoolean(5,booking.getStaus());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
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
//
//    public List<Booking> findAllBooking(){
//        try {
//            conn = ConnectionFactory.getConnection();
//            String sql = "SELECT * FROM booking";
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ResultSet resultSet = ps.executeQuery();
//            List<Booking> bookingList = new ArrayList<>();
//            while (resultSet.next()){
//                bookingList.add(getBooking(resultSet));
//            }
//            return bookingList;
//        } catch (SQLException e) {
//            throw new RuntimeException();
//        }finally {
//            ConnectionFactory.close(conn);
//        }
//    }

//    public Booking findAdressById(Integer adressId) {
//        try {
//            conn = ConnectionFactory.getConnection();
//            String sql = "SELECT * FROM adress WHERE id=?";
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ps.setInt(1,adressId);
//            ResultSet resultSet = ps.executeQuery();
//            return getBooking(resultSet);
//        } catch (SQLException e) {
//            throw new RuntimeException();
//        }finally {
//            ConnectionFactory.close(conn);
//        }
//    }

//    private Booking getBooking(ResultSet resultSet) throws SQLException {
//        return new Booking(
//                resultSet.getInt(1),
//                new BookDAO().findBookById(resultSet.getInt(2)),
//                new UserDAO().findUserById(resultSet.getInt(3)),
//                resultSet.getDate(4),
//                resultSet.getBoolean(5));
//    }
}
