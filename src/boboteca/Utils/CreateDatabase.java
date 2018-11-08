package boboteca.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateDatabase {
    Connection conn = null;
    public void createTables(){
        try{
            conn = ConnectionFactory.getConnection();
            List<String> sqls = new ArrayList<>();
            sqls.add("CREATE TABLE IF NOT EXISTS gender (id INTEGER PRIMARY KEY,label VARCHAR(255));");
            sqls.add("CREATE TABLE IF NOT EXISTS category (id INTEGER PRIMARY KEY, label VARCHAR(255) NOT NULL, value INTEGER NOT NULL, additional_value DOUBLE NOT NULL);");
            sqls.add("CREATE TABLE IF NOT EXISTS priority (id INTEGER PRIMARY KEY, label VARCHAR(255));");
            sqls.add("CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY, name VARCHAR(255) NOT NULL, author VARCHAR(255) NOT NULL, category VARCHAR(255) NOT NULL, year VARCHAR(255) NOT NULL, priority_id INTEGER NOT NULL, disponibility BOOLEAN NOT NULL, FOREIGN KEY (priority_id) REFERENCES priority(id));");
            sqls.add("CREATE TABLE IF NOT EXISTS address( id INTEGER PRIMARY KEY, street VARCHAR(255), number INTEGER NOT NULL, complement VARCHAR(255), neighborhood VARCHAR(255), cep VARCHAR(255), city VARCHAR(255), state VARCHAR(255));");
            sqls.add("CREATE TABLE IF NOT EXISTS user( id INTEGER PRIMARY KEY, name VARCHAR(255), password VARCHAR(255), gender_id INTEGER NOT NULL, category_id INTEGER NOT NULL, address_id INTEGER NOT NULL, telephone VARCHAR(255), is_librarian VARCHAR(255), FOREIGN KEY (address_id) REFERENCES address(id), FOREIGN KEY (gender_id) REFERENCES gender(id), FOREIGN KEY (category_id) REFERENCES category(id));");
            sqls.add("CREATE TABLE IF NOT EXISTS loans( id INTEGER PRIMARY KEY, book_id INTEGER NOT NULL, user_id INTEGER NOT NULL, loan_date DATE NOT NULL, return_date DATE NOT NULL, is_returned BOOLEAN DEFAULT FALSE, returned_date DATE, FOREIGN KEY (book_id) REFERENCES books(id), FOREIGN KEY (user_id) REFERENCES user(id));");
            sqls.add("CREATE TABLE IF NOT EXISTS booking( id INTEGER PRIMARY KEY, book_id INTEGER NOT NULL, user_id INTEGER NOT NULL, booking_date DATE NOT NULL, status BOOLEAN NOT NULL, FOREIGN KEY (book_id) REFERENCES books(id), FOREIGN KEY (user_id) REFERENCES user(id));");
            sqls.add("CREATE TABLE IF NOT EXISTS taxes(id INTEGER PRIMARY KEY, book_id INTEGER NOT NULL, user_id INTEGER NOT NULL, description VARCHAR(255) NOT NULL, value DOUBLE NOT NULL, is_paid BOOLEAN NOT NULL, FOREIGN KEY (book_id) REFERENCES books(id), FOREIGN KEY (user_id) REFERENCES user(id));");
            sqls.add("INSERT INTO gender (id, label) VALUES (1,'Feminino');");
            sqls.add("INSERT INTO gender (id, label)  VALUES (2,'Masculino');");
            sqls.add("INSERT INTO gender (id, label)  VALUES (3,'Intersexo');");
            sqls.add("INSERT INTO category (id, label,value,additional_value) VALUES (1,'Aluno',20,0.2);");
            sqls.add("INSERT INTO category (id, label,value,additional_value) VALUES (2,'Professor',15,0.5);");
            sqls.add("INSERT INTO category (id, label,value,additional_value) VALUES (3,'Funcionário',12,0.3);");
            sqls.add("INSERT INTO category (id, label,value,additional_value) VALUES (4,'Comunidade',8,0.1);");
            sqls.add("INSERT INTO priority (id, label) VALUES (1,'Baixa');");
            sqls.add("INSERT INTO priority (id, label) VALUES (2,'Média');");
            sqls.add("INSERT INTO priority (id, label) VALUES (3,'Alta');");
            for (String sql:sqls) {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
