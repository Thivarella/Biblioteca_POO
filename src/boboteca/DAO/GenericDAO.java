package boboteca.DAO;

import boboteca.Model.Address;
import boboteca.Model.Generic;
import boboteca.Utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class GenericDAO {
    Connection conn = null;

    public List<Generic> getPriority(){
        return getGenerics("SELECT * FROM priority");
    }

    public Generic getPriorityById(Integer priorityId){
        return getGeneric(priorityId, "SELECT * FROM priority WHERE id=?;");
    }

    private Generic getGeneric(Integer priorityId, String s) {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = s;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, priorityId);
            ResultSet resultSet = ps.executeQuery();
            Generic priority = new Generic();
            while (resultSet.next()) {
                priority = new Generic(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                );
            }
            return priority;
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            ConnectionFactory.close(conn);
        }
    }

    public Generic getGenderById(Integer genderId){
        return getGeneric(genderId, "SELECT * FROM gender WHERE id=?;");
    }

    public Generic getCategoryById(Integer categoryId){
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM category WHERE id=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ResultSet resultSet = ps.executeQuery();
            Generic category = new Generic();
            while (resultSet.next()){
                category = getCategory(resultSet);
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private List<Generic> getGenerics(String sql) {
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            List<Generic> categoryList = new ArrayList<>();
            while (resultSet.next()){
                Generic category = new Generic(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                );
                categoryList.add(category);
            }
            return categoryList;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public List<Generic> getGender(){
        return getGenerics("SELECT * FROM gender");
    }

    public List<Generic> getCategory() {
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM category;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            List<Generic> categoryList = new ArrayList<>();
            while (resultSet.next()){
                categoryList.add(getCategory(resultSet));
            }
            return categoryList;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    private Generic getCategory(ResultSet resultSet) throws SQLException {
        return new Generic(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getInt(3),
                resultSet.getDouble(4)
        );
    }
}
