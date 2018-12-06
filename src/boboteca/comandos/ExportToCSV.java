package boboteca.comandos;

import boboteca.utils.ConnectionFactory;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import static boboteca.export.ExportTags.getPATHCSV;
import static boboteca.utils.Utils.checkFiletoDelete;

public class ExportToCSV {
    private Connection conn;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final String TABLE = "export";
    private SimpleObjectProperty<Logger> logger = new SimpleObjectProperty<>(this, "logger");

    public void exportUserCsv() throws IOException {
        calendar.setTime(new Date());
        String file = "Usuários "+ simpleDateFormat.format(calendar.getTime())+".csv";
        checkFiletoDelete(getPATHCSV() + file);
        createExport("id INTEGER, name VARCHAR(255), gender VARCHAR(255), category VARCHAR(255), telephone VARCHAR(255), is_librarian VARCHAR(255), street VARCHAR(255), number INTEGER NOT NULL, complement VARCHAR(255), neighborhood VARCHAR(255), cep VARCHAR(255), city VARCHAR(255), state VARCHAR(255)");

        setTableSource(file);
        setTableHeader("código,nome,sexo,categoria,telefone,usuário_normal,logradouro,número,complemento,bairro,cep,cidade,estado");
        insertIntoFile("SELECT u.id, u.name, g.label, c.label, u.telephone, (CASE WHEN is_librarian = true THEN 'Não' ELSE 'Sim' END), a.street, a.number, a.complement, a.neighborhood, a.cep, a.city, a.state FROM user u JOIN address a ON u.address_id = a.id JOIN gender g ON g.id = u.gender_id JOIN category c ON c.id = u.category_id");
        dropExport();
        movetoFolder(getPATHCSV()+file);
    }

    public void exportBookCsv() throws IOException {
        calendar.setTime(new Date());
        String file = "Livros "+ simpleDateFormat.format(calendar.getTime())+".csv";
        checkFiletoDelete(getPATHCSV() + file);
        createExport("id INTEGER, name VARCHAR(255), author VARCHAR(255), category VARCHAR(255), year VARCHAR(255), priority VARCHAR(255), disponibility VARCHAR(255)");

        setTableSource(file);
        setTableHeader("código,nome,autor,categoria,ano_de_publicação,prioridade,disponibilidade");
        insertIntoFile("SELECT b.id, b.name, b.author, b.category, b.year, p.label, (CASE WHEN disponibility = true THEN 'Sim' ELSE 'Não' END) FROM books b JOIN priority p ON b.priority_id = p.id");
        dropExport();
        movetoFolder(getPATHCSV()+file);
    }

    public void exportLoansCsv() throws IOException {
        calendar.setTime(new Date());
        String file = "Empréstimos "+ simpleDateFormat.format(calendar.getTime())+".csv";
        checkFiletoDelete(getPATHCSV() + file);
        createExport("id INTEGER, book_id INTEGER, book_name VARCHAR(255), user_id INTEGER, user_name VARCHAR(255), loan_date DATE, return_date DATE, is_returned VARCHAR(255), returned_date DATE");

        setTableSource(file);
        setTableHeader("código,código_do_livro,nome_do_livro,código_do_usuário,nome_do_usuário,data_do_empréstimo,data_de_devolução,devolvido,devolvido_em");
        insertIntoFile("SELECT l.id, b.id, b.name, u.id, u.name, l.loan_date, l.return_date, (CASE WHEN l.is_returned = true THEN 'Sim' ELSE 'Não' END), l.returned_date FROM loans l JOIN books b ON b.id = l.book_id JOIN user u ON u.id = l.user_id");
        dropExport();
        movetoFolder(getPATHCSV()+file);
    }

    public void exportTaxesCsv() throws IOException {
        calendar.setTime(new Date());
        String file = "Multas "+ simpleDateFormat.format(calendar.getTime())+".csv";
        checkFiletoDelete(getPATHCSV() + file);
        createExport("id INTEGER, book_id INTEGER, book_name VARCHAR(255), user_id INTEGER, user_name VARCHAR(255), loan_id INTEGER, description VARCHAR(255), value DOUBLE, is_paid VARCHAR(255)");

        setTableSource(file);
        setTableHeader("código,código_do_livro,nome_do_livro,código_do_usuário,nome_do_usuário,código_do_empréstimo,descrição,valor,situação");
        insertIntoFile("SELECT t.id, b.id, b.name, u.id, u.name, l.id, t.description, t.value, (CASE WHEN t.is_paid = true THEN 'Pago' ELSE 'Em aberto' END) FROM taxes t JOIN books b ON b.id = t.book_id JOIN user u ON u.id = t.user_id JOIN loans l ON l.id = t.loan_id");
        dropExport();
        movetoFolder(getPATHCSV()+file);
    }

    public void exportBookingCsv() throws IOException {
        calendar.setTime(new Date());
        String file = "Reservas "+ simpleDateFormat.format(calendar.getTime())+".csv";
        checkFiletoDelete(getPATHCSV() + file);
        createExport("id INTEGER, book_id INTEGER, book_name VARCHAR(255), user_id INTEGER, user_name VARCHAR(255), booking_date VARCHAR(255), status VARCHAR(255)");

        setTableSource(file);
        setTableHeader("código,código_do_livro,nome_do_livro,código_do_usuário,nome_do_usuário,data_da_reserva,status");
        insertIntoFile("SELECT bo.id, b.id, b.name, u.id, u.name, to_char(bo.booking_date,'dd/MM/yyyy'),(CASE WHEN bo.status = true THEN 'Reservado' ELSE 'Reserva Finalizada' END) FROM booking bo JOIN books b ON b.id = bo.book_id JOIN user u ON u.id = bo.user_id");
        dropExport();
        movetoFolder(getPATHCSV()+file);
    }

    private void movetoFolder(String filePath) throws IOException {
        File file = new File(filePath);

        if(file.renameTo(new File("/home/thiago/Documentos/Relatórios_Boboteca/"+file.getName()))&& Files.deleteIfExists(Paths.get(file.getPath()))){
            logger.get().info("File Deleted");
        }
    }

    private void dropExport() {
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DROP TABLE " + TABLE + ";")) {
                ps.execute();
            }
        } catch (SQLException e) {
            logger.get().info(e.toString());
        }
    }

    private void insertIntoFile(String select) {
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(String.format("INSERT INTO %s (%s);", TABLE, select))) {
                ps.execute();
            }
        } catch (SQLException e) {
            logger.get().info(e.toString());
        }
    }

    private void setTableHeader(String headers) {
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(String.format("SET TABLE %s SOURCE HEADER '%s';", TABLE, headers))) {
                ps.execute();
            }
        } catch (SQLException e) {
            logger.get().info(e.toString());
        }
    }

    private void setTableSource(String file) {
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(String.format("SET TABLE %s SOURCE '%s;ignore_first=true;encoding=UTF-8';", TABLE, file))) {
                ps.execute();
            }
        } catch (SQLException e) {
            logger.get().info(e.toString());
        }
    }

    private void createExport(String columns) {
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(String.format("CREATE TEXT TABLE export (%s)", columns))) {
                ps.execute();
            }
        } catch (SQLException e) {
            logger.get().info(e.toString());
        }
    }
}
