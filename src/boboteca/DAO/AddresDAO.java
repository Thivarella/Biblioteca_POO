package boboteca.DAO;

import boboteca.Model.Address;
import boboteca.Model.Book;
import boboteca.Utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AddresDAO {
    Connection conn = null;
    public Boolean insertAddress(Address address){
        Boolean saved = false;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO address (id, street, number, complement, neighborhood, cep, city, state ) VALUES (?,?,?,?,?,?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,address.getId());
            ps.setString(2,address.getStreet());
            ps.setInt(3,address.getNumber());
            ps.setString(4,address.getComplement());
            ps.setString(5,address.getNeighborhood());
            ps.setString(6,address.getCep());
            ps.setString(7,address.getCity());
            ps.setString(8,address.getState());
            ps.execute();
            saved = true;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
            return saved;
        }
    }

    public void updateAddress(Address address){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE address SET street=?, number=?, complement=?, neighborhood=?, cep=?, city=?, state=? WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,address.getStreet());
            ps.setInt(2,address.getNumber());
            ps.setString(3,address.getComplement());
            ps.setString(4,address.getNeighborhood());
            ps.setString(5,address.getCep());
            ps.setString(6,address.getCity());
            ps.setString(7,address.getState());
            ps.setInt(8,address.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public void removeAddress(int addressId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM address WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,addressId);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Address> findAllAddress(){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM address";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            List<Address> addressList = new ArrayList<>();
            while (resultSet.next()){
                addressList.add(getAddress(resultSet));
            }
            return addressList;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public Address findAdressById(Integer adressId) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM address WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,adressId);
            ResultSet resultSet = ps.executeQuery();
            Address address = new Address();
            while (resultSet.next()){
                address = getAddress(resultSet);
            }
            return address;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private Address getAddress(ResultSet resultSet) throws SQLException {
        return new Address(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getInt(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8));
    }
}
