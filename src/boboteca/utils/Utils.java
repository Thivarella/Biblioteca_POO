package boboteca.utils;

import boboteca.model.Book;
import boboteca.model.User;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

public class Utils {

    public static void checkFiletoDelete(String file) {
        File outfile = new File(file);

        if (outfile.exists()) {
            outfile.delete();
        }
    }

    public static int getMaxId(String table){
        Connection conn = null;
        try {
            Integer id = 1;
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT MAX(id) FROM " + table;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
               id += resultSet.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            ConnectionFactory.close(conn);
        }
    }

    public static Date getToday(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        java.util.Date atualDate = calendar.getTime();
        return new java.sql.Date(atualDate.getTime());
    }

    public static Date getReturnDate(Integer value){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, value);
        java.util.Date atualDate = calendar.getTime();
        return new java.sql.Date(atualDate.getTime());
    }


    public static void searchCep(String cep, TextField textFieldStreet, TextField textFieldNumber, TextField textFieldNeighborhood, TextField textFieldAddon, TextField textFieldCity, TextField textFieldState) throws IOException, org.json.simple.parser.ParseException {
        if(cep.length()>7){
            URL url = new URL("https://viacep.com.br/ws/"+cep+"/json/");
            URLConnection urlConnection = url.openConnection();
            if(!urlConnection.getInputStream().equals(null)) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new BufferedReader(bufferedReader));
                Boolean erro = (jsonObject.get("erro") == null) ? false : (Boolean) jsonObject.get("erro");
                if (!erro) {
                    if(jsonObject.get("logradouro").equals(""))
                        textFieldStreet.requestFocus();
                    else {
                        textFieldNumber.requestFocus();
                    }
                    textFieldStreet.setText((String) jsonObject.get("logradouro"));
                    textFieldNeighborhood.setText((String) jsonObject.get("bairro"));
                    textFieldCity.setText((String) jsonObject.get("localidade"));
                    textFieldState.setText((String) jsonObject.get("uf"));
                }else {
                    errorMessage(textFieldStreet,textFieldNeighborhood,textFieldCity,textFieldState);
                }
            }else {
                errorMessage(textFieldStreet, textFieldNeighborhood, textFieldCity, textFieldState);
            }
            textFieldAddon.setDisable(false);
            textFieldNumber.setDisable(false);
        }
    }

    private static void errorMessage(TextField textFieldStreet, TextField textFieldNeighborhood, TextField textFieldCity, TextField textFieldState) {
        JOptionPane.showMessageDialog(null,"CEP não encontrado",null,JOptionPane.INFORMATION_MESSAGE,null);
        textFieldStreet.setDisable(false);
        textFieldStreet.setText("");
        textFieldStreet.requestFocus();
        textFieldNeighborhood.setDisable(false);
        textFieldNeighborhood.setText("");
        textFieldCity.setDisable(false);
        textFieldCity.setText("");
        textFieldState.setDisable(false);
        textFieldCity.setText("");
    }

    public static String formatMoney(Double aDouble){
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(aDouble);
    }

    public static Double doubleFromString(String string){
        return Double.parseDouble(string.replace("R$ ","").replace(".","").replace(",","."));
    }

    /**
     * Monta a mascara para Data (dd/MM/yyyy).
     *
     * @param textField TextField
     */
    public static void dateField(final TextField textField) {
        maxField(textField, 10);

        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() < 11) {
                    String value = textField.getText();
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceFirst("(\\d{2})(\\d)", "$1/$2");
                    value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");
                    textField.setText(value);
                    positionCaret(textField);
                }
            }
        });
    }

    /**
     * Campo que aceita somente numericos.
     *
     * @param textField TextField
     */
    public static void numericField(final JFXTextField textField) {
        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = textField.getText().charAt(oldValue.intValue());
                    if (!(ch >= '0' && ch <= '9')) {
                        textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                    }
                }
            }
        });
    }

    /**
     * Monta a mascara para os campos Telefone.
     *
     * @param textField TextField
     */
    public static void phoneField(final JFXTextField textField) {
        maxField(textField, 15);

        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{2})(\\d)", "($1) $2");
                if(textField.getLength() < 15){
                    value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
                }else {
                    value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
                }
                textField.setText(value);
                positionCaret(textField);
            }
        });
    }

    /**
     * Monta a mascara para os campos CEP.
     *
     * @param textField TextField
     */
    public static void cepField(final TextField textField) {
        maxField(textField, 9);

        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
                value = value.replaceFirst("(\\d{5})\\-(\\d{3})", "$1-$2");
                textField.setText(value);
                positionCaret(textField);
            }
        });
    }

    /**
     * Devido ao incremento dos caracteres das mascaras eh necessario que o cursor sempre se posicione no final da string.
     *
     * @param textField TextField
     */
    private static void positionCaret(final TextField textField) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Posiciona o cursor sempre a direita.
                textField.positionCaret(textField.getText().length());
            }
        });
    }

    /**
     * @param textField TextField.
     * @param length    Tamanho do campo.
     */
    public static void maxField(final TextField textField, final Integer length) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (newValue.length() > length)
                    textField.setText(oldValue);
            }
        });
    }

    public static Integer getInteger(Integer id, String sql, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1,id);
        ResultSet resultSet = ps.executeQuery();
        Integer count = 0;
        while (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        return count;
    }

    public static Integer getIntegerWithStatement(Integer userId, PreparedStatement ps) throws SQLException {
        ps.setLong(1, userId);
        ResultSet resultSet = ps.executeQuery();
        int count = 0;
        while (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        return count;
    }

    /**
     * @param ps Prepared Statement já com o sql.
     * @param id id do Objeto principal a ser salvo no banco.
     * @param book Livro usado na transação.
     * @param user Usuário usado na transação.
     */
    public static void prepareStatement(PreparedStatement ps, Integer id, Book book, User user) throws SQLException {
        ps.setInt(1, id);
        ps.setInt(2, book.getId());
        ps.setInt(3, user.getId());
    }

    /**
     * @param textFieldList textFields do contexto.
     */
    public static void clearingFields(List<JFXTextField> textFieldList) {
        for (JFXTextField textField:textFieldList) {
            textField.setText("");
        }
    }

    /**
     *
     * @param jfxTextFields lista de textFields.
     * @param txtBookCode textField.
     * @param txtBookName textField.
     * @param txtBookAuthor textField.
     * @param txtBookCategory textField.
     * @param txtBookYear textField.
     */
    public static void JfxTexts(List<JFXTextField> jfxTextFields, JFXTextField txtBookCode, JFXTextField txtBookName, JFXTextField txtBookAuthor, JFXTextField txtBookCategory, JFXTextField txtBookYear) {
        jfxTextFields.add(txtBookCode);
        jfxTextFields.add(txtBookName);
        jfxTextFields.add(txtBookAuthor);
        jfxTextFields.add(txtBookCategory);
        jfxTextFields.add(txtBookYear);
    }
}
