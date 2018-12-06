package boboteca.dao;

import boboteca.model.Address;
import boboteca.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


class AddresDAO {
    private Connection conn = null;
    Boolean insertAddress(Address address){
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
            return true;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }

    }

    Boolean updateAddress(Address address) {
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
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    void removeAddress(int addressId){
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

    Address findAdressById(Integer adressId) {
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
