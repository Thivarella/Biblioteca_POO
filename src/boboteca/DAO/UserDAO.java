package boboteca.DAO;

import boboteca.Model.User;
import boboteca.Utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {
    Connection conn = null;
    public void insertUser(User user){
        if(new AddresDAO().insertAddress(user.getAddress())){
            try {
                conn = ConnectionFactory.getConnection();
                String sql = "INSERT INTO user (id, name, password, gender, category, address_id, telephone, is_librarian ) VALUES (?,?,?,?,?,?,?,?);";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1,user.getId());
                ps.setString(2,user.getName());
                ps.setString(3,user.getPassword());
                ps.setInt(4,user.getGender());
                ps.setInt(5,user.getCategory());
                ps.setInt(6,user.getAddress().getId());
                ps.setString(7,user.getTelephone());
                ps.setBoolean(8,user.getLibrarian());
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException();
            }finally {
                ConnectionFactory.close(conn);
            }
        }
    }

    public void updateUser(User user){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE user SET name=?, gender=?, category=?, address_id=?, telephone=?, is_librarian=? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,user.getName());
            ps.setInt(2,user.getGender());
            ps.setInt(3,user.getCategory());
            ps.setInt(4,user.getAddress().getId());
            ps.setString(5,user.getTelephone());
            ps.setBoolean(6,user.getLibrarian());
            ps.setInt(7,user.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void removeUser(int userId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM user WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,userId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<User> findAllUsers(){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM user";
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
            String sql = "SELECT * FROM user WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,userId);
            ResultSet resultSet = ps.executeQuery();
            return getUser(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getInt(3),
                resultSet.getInt(4),
                new AddresDAO().findAdressById(resultSet.getInt(6)),
                resultSet.getString(7),
                resultSet.getBoolean(8));
    }

}
