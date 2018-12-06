package boboteca.dao;

import boboteca.model.Address;
import boboteca.model.Generic;
import boboteca.model.User;
import boboteca.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static boboteca.utils.Utils.getInteger;


public class UserDAO {
    private Connection conn = null;

    private AddresDAO addresDAO = new AddresDAO();

    public boolean insertUser(User user) {
        boolean inserted = false;
        if(addresDAO.insertAddress(user.getAddress())){
            try {
                conn = ConnectionFactory.getConnection();
                String sql = "INSERT INTO user (id, name, password, gender_id, category_id, address_id, telephone, is_librarian ) VALUES (?,?,?,?,?,?,?,?);";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1,user.getId());
                ps.setString(2,user.getName());
                ps.setString(3,user.getPassword());
                ps.setInt(4, user.getGender().getId());
                ps.setInt(5, user.getCategory().getId());
                ps.setInt(6,user.getAddress().getId());
                ps.setString(7,user.getTelephone());
                ps.setBoolean(8,user.getLibrarian());
                ps.execute();
                inserted = true;
            } catch (SQLException e) {
                throw new RuntimeException();
            }finally {
                ConnectionFactory.close(conn);
            }
        }
        return inserted;
    }

    public Boolean updateUser(User user) {
        boolean updated = false;
        if (addresDAO.updateAddress(user.getAddress())) {
            try {
                conn = ConnectionFactory.getConnection();
                String sql = "UPDATE user SET name=?, gender_id=?, category_id=?, address_id=?, telephone=?, is_librarian=? WHERE id=?;";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user.getName());
                ps.setInt(2, user.getGender().getId());
                ps.setInt(3, user.getCategory().getId());
                ps.setInt(4, user.getAddress().getId());
                ps.setString(5, user.getTelephone());
                ps.setBoolean(6, user.getLibrarian());
                ps.setInt(7, user.getId());
                ps.execute();
                updated = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                ConnectionFactory.close(conn);
            }
        }
        return updated;
    }

    public void removeUser(int userId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM user WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,userId);
            ps.execute();
            addresDAO.removeAddress(userId);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<User> findAllUsers(){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL,c.VALUE,c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN FROM user u JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            List<User> userList = new ArrayList<>();
            while (resultSet.next()){
                userList.add(getUser(resultSet));
            }
            return userList;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public User findUserById(Integer userId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL,c.VALUE,c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN FROM user u JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,userId);
            return getUser(ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        Generic generic = new Generic(resultSet.getInt(5),resultSet.getString(6),resultSet.getInt(7),resultSet.getDouble(8));
        return new User(resultSet.getInt(1),resultSet.getString(2), new Generic(resultSet.getInt(3),resultSet.getString(4)), generic,new Address(resultSet.getInt(9),resultSet.getString(10),resultSet.getInt(11),resultSet.getString(12),resultSet.getString(13),resultSet.getString(14),resultSet.getString(15),resultSet.getString(16)),resultSet.getString(17),resultSet.getBoolean(18));
    }

    public User Login(String userId, String password) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT u.ID, u.NAME, g.ID, g.LABEL, c.ID,c.LABEL,c.VALUE,c.ADDITIONAL_VALUE, a.ID, a.STREET, a.NUMBER, a.COMPLEMENT, a.NEIGHBORHOOD, a.CEP, a.CITY, a.STATE, u.TELEPHONE, u.IS_LIBRARIAN FROM user u JOIN GENDER G on u.GENDER_ID = G.ID JOIN CATEGORY C on u.CATEGORY_ID = C.ID JOIN ADDRESS A on u.ADDRESS_ID = A.ID WHERE u.ID = ? AND u.password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,userId);
            ps.setString(2,password);
            return getUser(ps);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private User getUser(PreparedStatement ps) throws SQLException {
        ResultSet resultSet = ps.executeQuery();
        User user = new User();
        while (resultSet.next()) {
            user = getUser(resultSet);
        }
        return user;
    }

    public Integer getTaxesCountByUser(Integer id) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(*) FROM taxes WHERE user_id = ? AND is_paid = false ";
            return getInteger(id, sql, conn);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public Integer getLoansCountByUser(Integer id) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ?;";
            return getInteger(id, sql, conn);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }
}
