package boboteca.DAO;

import boboteca.Model.Book;
import boboteca.Utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BookDAO {
    Connection conn = null;
    public void insertBook(Book book){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO books (id, name, author, category, year, priority_id, disponibility ) VALUES (?,?,?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,book.getId());
            ps.setString(2,book.getName());
            ps.setString(3,book.getAuthor());
            ps.setString(4,book.getCategory());
            ps.setString(5,book.getYear());
            ps.setInt(6, book.getPriority().getId());
            ps.setBoolean(7,book.getDisponibility());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void updateBook(Book book){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE books SET name=?, author=?, category=?, year=?, priority_id=?, disponibility=? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,book.getName());
            ps.setString(2,book.getAuthor());
            ps.setString(3,book.getCategory());
            ps.setString(4,book.getYear());
            ps.setInt(5, book.getPriority().getId());
            ps.setBoolean(6,book.getDisponibility());
            ps.setInt(7,book.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void removeBook(int bookId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,bookId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Book> findAllBooks(String search, String filter){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM books";
            if (!search.equals("")) {
                sql += " WHERE " + filter + " LIKE '%" + search + "%';";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            List<Book> bookList = new ArrayList<>();
            while (resultSet.next()){
                bookList.add(getBook(resultSet));
            }
            return bookList;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public Book findBookById(Integer bookId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM books WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,bookId);
            ResultSet resultSet = ps.executeQuery();
            Book book = new Book();
            while (resultSet.next()) {
                book = getBook(resultSet);
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    public Book findBookByIdAndDisponibilityTrue(Integer bookId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM books WHERE id=? AND disponibility= true";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookId);
            ResultSet resultSet = ps.executeQuery();
            Book book = new Book();
            while (resultSet.next()) {
                book = getBook(resultSet);
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private Book getBook(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                new GenericDAO().getPriorityById(resultSet.getInt(6)),
                resultSet.getBoolean(7));
    }
}
