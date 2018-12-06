package boboteca.comandos;

import boboteca.utils.ConnectionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleObjectProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static boboteca.export.ExportTags.*;
import static boboteca.utils.Utils.checkFiletoDelete;

public class ExportToJSON {
    private SimpleObjectProperty<Logger> logger = new SimpleObjectProperty<>(this, "logger");
    private Connection conn;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static String extension = ".json";

    public void exportUserToJSON() {
        String filename = "Usuários "+ simpleDateFormat.format(calendar.getTime())+extension;
        checkFiletoDelete(getFILEPATH()+filename);

        try {
            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT u.id, u.name, g.label, c.label, u.telephone, (CASE WHEN is_librarian = true THEN 'Não' ELSE 'Sim' END) as normal_user, a.street, a.number, a.complement, a.neighborhood, a.cep, a.city, a.state FROM user u JOIN address a ON u.address_id = a.id JOIN gender g ON g.id = u.gender_id JOIN category c ON c.id = u.category_id;")) {
                resultSet = ps.executeQuery();
            }
            JSONArray jsonArray = getJSONArray(getUSERKEYS(),resultSet);
            saveJSON(filename, jsonArray);

        } catch (SQLException e) {
            logger.get().info(e.toString());
        }



    }

    private JSONArray getJSONArray(List<String> keys, ResultSet resultSet) throws SQLException {
        JSONArray jsonElements = new JSONArray();
        while (resultSet.next()){
            JSONObject jsonObject = new JSONObject();
            for(int i = 1; i<= keys.size();i++){
                jsonObject.put(keys.get(i-1),resultSet.getString(i));
            }
            jsonElements.add(jsonObject);
        }
        return jsonElements;
    }

    private void saveJSON(String filename, JSONArray jsonArray) {
        try (FileWriter file = new FileWriter(getFILEPATH()+filename)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jsonArray.toJSONString());
            file.write(gson.toJson(je));
            file.flush();

        } catch (IOException e) {
            logger.get().info(e.toString());
        }
    }

    public void exportBooksToJSON() {
        String filename = "Livros "+ simpleDateFormat.format(calendar.getTime())+extension;
        checkFiletoDelete(getFILEPATH()+filename);

        try {
            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT b.id, b.name, b.author, b.category, b.year, p.label, (CASE WHEN disponibility = true THEN 'Sim' ELSE 'Nao' END) FROM books b JOIN priority p ON b.priority_id = p.id;")) {
                resultSet = ps.executeQuery();
            }

            JSONArray jsonArray = getJSONArray(getBOOKKEYS(),resultSet);
            saveJSON(filename, jsonArray);

        } catch (SQLException e) {
            logger.get().info(e.toString());
        }



    }

    public void exportLoansToJSON() {
        String filename = "Empréstimos "+ simpleDateFormat.format(calendar.getTime())+extension;
        checkFiletoDelete(getFILEPATH()+filename);

        try {
            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT l.id, b.id, b.name, u.id, u.name, l.loan_date, l.return_date, (CASE WHEN l.is_returned = true THEN 'Sim' ELSE 'Nao' END), l.returned_date FROM loans l JOIN books b ON b.id = l.book_id JOIN user u ON u.id = l.user_id;")) {
                resultSet = ps.executeQuery();
            }
            JSONArray jsonArray = getJSONArray(getLOANKEYS(),resultSet);
            saveJSON(filename, jsonArray);

        } catch (SQLException e) {
            logger.get().info(e.toString());
        }



    }

    public void exportTaxesToJSON() {
        String filename = "Multas "+ simpleDateFormat.format(calendar.getTime())+extension;
        checkFiletoDelete(getFILEPATH()+filename);

        try {
            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT t.id, b.id, b.name, u.id, u.name, l.id, t.description, t.value, (CASE WHEN t.is_paid = true THEN 'Pago' ELSE 'Em aberto' END) FROM taxes t JOIN books b ON b.id = t.book_id JOIN user u ON u.id = t.user_id JOIN loans l ON l.id = t.loan_id;")) {
                resultSet = ps.executeQuery();
            }
            JSONArray jsonArray = getJSONArray(getTAXKEYS(),resultSet);
            saveJSON(filename, jsonArray);

        } catch (SQLException e) {
            logger.get().info(e.toString());
        }



    }

    public void exportBookingToJSON() {
        String filename = "Reservas "+ simpleDateFormat.format(calendar.getTime())+extension;
        checkFiletoDelete(getFILEPATH()+filename);

        try {
            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT bo.id, b.id, b.name, u.id, u.name, to_char(bo.booking_date,'dd/MM/yyyy'),(CASE WHEN bo.status = true THEN 'Reservado' ELSE 'Reserva Finalizada' END) FROM booking bo JOIN books b ON b.id = bo.book_id JOIN user u ON u.id = bo.user_id;")) {
                resultSet = ps.executeQuery();
            }
            JSONArray jsonArray = getJSONArray(getBOOKINGKEYS(),resultSet);
            saveJSON(filename, jsonArray);

        } catch (SQLException e) {
            logger.get().info(e.toString());
        }



    }
}
