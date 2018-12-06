package boboteca.dao;

import boboteca.model.Book;
import boboteca.model.Generic;
import boboteca.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static boboteca.utils.Utils.getIntegerWithStatement;


public class BookDAO {
    private GenericDAO genericDAO = new GenericDAO();
    public void insertBook(Book book){
        Connection conn = ConnectionFactory.getConnection();
        try {
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
        Connection conn = ConnectionFactory.getConnection();
        try {
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
        Connection conn = ConnectionFactory.getConnection();
        try {
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
        Connection conn = ConnectionFactory.getConnection();
        try {
            String sql = "SELECT b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY FROM books b  JOIN PRIORITY p ON p.ID = b.PRIORITY_ID ";
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

    protected Book findBookById(Integer bookId){
        Connection conn = ConnectionFactory.getConnection();
        try {
            String sql = "SELECT b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY FROM books b  JOIN PRIORITY p ON p.ID = b.PRIORITY_ID WHERE id=?";
            return getBook(bookId, sql, conn);
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    public Book findBookByIdAndDisponibilityTrue(Integer bookId) {
        Connection conn = ConnectionFactory.getConnection();
        try {
            String sql = "SELECT b.ID, b.NAME, b.AUTHOR, b.CATEGORY, b.YEAR, p.id, p.label, b.DISPONIBILITY FROM books b  JOIN PRIORITY p ON p.ID = b.PRIORITY_ID WHERE id=? AND disponibility = true";
            return getBook(bookId, sql, conn);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private Book getBook(Integer bookId, String sql, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, bookId);
        ResultSet resultSet = ps.executeQuery();
        Book book = new Book();
        while (resultSet.next()) {
            book = getBook(resultSet);
        }
        return book;
    }

    private Book getBook(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                new Generic(resultSet.getInt(6),resultSet.getString(7)),
                resultSet.getBoolean(8));
    }

    public Integer getBookLoanCount(Integer id) {
        Connection conn = ConnectionFactory.getConnection();
        try {
            String sql = "SELECT COUNT(id) FROM loans WHERE book_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            return getIntegerWithStatement(id, ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }
}
