package boboteca.comandos;

import boboteca.utils.ConnectionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
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

public class ExportToXML {
    private static final Logger LOGGER = Logger.getLogger( ExportToXML.class.getName() );
    private Connection conn;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public void exportUserToXML(){
        String filename = "Usuários "+ simpleDateFormat.format(calendar.getTime())+".xml";
        checkFiletoDelete(getFILEPATH()+filename);
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("usuarios");
            doc.appendChild(rootElement);

            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT u.id, u.name, g.label, c.label, u.telephone, (CASE WHEN is_librarian = true THEN 'Não' ELSE 'Sim' END) as normal_user, a.street, a.number, a.complement, a.neighborhood, a.cep, a.city, a.state FROM user u JOIN address a ON u.address_id = a.id JOIN gender g ON g.id = u.gender_id JOIN category c ON c.id = u.category_id;")) {
                resultSet = ps.executeQuery();
            }

            DOMSource source = createXML(doc,resultSet, rootElement, getUSERKEYS(),"usuario");
            saveXML(filename, source);

        } catch (ParserConfigurationException | TransformerException | SQLException pce) {
            LOGGER.info(pce.toString());
        }
    }

    private DOMSource createXML(Document doc, ResultSet resultSet, Element rootElement,List<String> keys, String tag) throws SQLException {
        DOMSource source = new DOMSource();
        while (resultSet.next()){
            Element firstElement = doc.createElement(tag);
            for (int i = 1; i <= keys.size(); i++){
                if ( i == 1) {
                    rootElement.appendChild(firstElement);
                    firstElement.setAttribute(keys.get(i-1), resultSet.getString(i));
                } else {
                    Element postElements = doc.createElement(keys.get(i-1));
                    postElements.appendChild(doc.createTextNode(resultSet.getString(i)));
                    firstElement.appendChild(postElements);
                }
            }
            source.setNode(doc);
        }
        return source;
    }

    public void exportBooksToXML(){
        String filename = "Livros "+ simpleDateFormat.format(calendar.getTime())+".xml";
        checkFiletoDelete(getFILEPATH()+filename);
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("livros");
            doc.appendChild(rootElement);

            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT b.id, b.name, b.author, b.category, b.year, p.label, (CASE WHEN disponibility = true THEN 'Sim' ELSE 'Nao' END) FROM books b JOIN priority p ON b.priority_id = p.id;")) {
                resultSet = ps.executeQuery();
            }

            DOMSource source = createXML(doc,resultSet,rootElement,getBOOKKEYS(),"livro");
            saveXML(filename, source);

        } catch (ParserConfigurationException | TransformerException | SQLException pce) {
            LOGGER.info(pce.toString());
        }
    }

    public void exportLoansToXML(){
        String filename = "Empréstimos "+ simpleDateFormat.format(calendar.getTime())+".xml";
        checkFiletoDelete(getFILEPATH()+filename);
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("emprestimos");
            doc.appendChild(rootElement);

            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT l.id, b.id, b.name, u.id, u.name, l.loan_date, l.return_date, (CASE WHEN l.is_returned = true THEN 'Sim' ELSE 'Nao' END), (CASE WHEN l.returned_date IS NULL THEN '' ELSE to_char(l.return_date,'dd/MM/yyyy') END) FROM loans l JOIN books b ON b.id = l.book_id JOIN user u ON u.id = l.user_id;")) {
                resultSet = ps.executeQuery();
            }

            DOMSource source = createXML(doc,resultSet,rootElement,getLOANKEYS(),"emprestimo");
            saveXML(filename, source);

        } catch (ParserConfigurationException | TransformerException | SQLException pce) {
            LOGGER.info(pce.toString());
        }
    }

    public void exportTaxesToXML(){
        String filename = "Multas "+ simpleDateFormat.format(calendar.getTime())+".xml";
        checkFiletoDelete(getFILEPATH()+filename);
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("multa");
            doc.appendChild(rootElement);

            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT t.id, b.id, b.name, u.id, u.name, l.id, t.description, t.value, (CASE WHEN t.is_paid = true THEN 'Pago' ELSE 'Em aberto' END) FROM taxes t JOIN books b ON b.id = t.book_id JOIN user u ON u.id = t.user_id JOIN loans l ON l.id = t.loan_id;")) {
                resultSet = ps.executeQuery();
            }

            DOMSource source = createXML(doc,resultSet,rootElement,getTAXKEYS(),"multas");
            saveXML(filename, source);

        } catch (ParserConfigurationException | TransformerException | SQLException pce) {
            LOGGER.info(pce.toString());
        }
    }

    public void exportBookingToXML(){
        String filename = "Reservas "+ simpleDateFormat.format(calendar.getTime())+".xml";
        checkFiletoDelete(getFILEPATH()+filename);
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("reservas");
            doc.appendChild(rootElement);

            conn = ConnectionFactory.getConnection();
            ResultSet resultSet;
            try (PreparedStatement ps = conn.prepareStatement("SELECT bo.id, b.id, b.name, u.id, u.name, to_char(bo.booking_date,'dd/MM/yyyy'),(CASE WHEN bo.status = true THEN 'Reservado' ELSE 'Reserva Finalizada' END) FROM booking bo JOIN books b ON b.id = bo.book_id JOIN user u ON u.id = bo.user_id;")) {
                resultSet = ps.executeQuery();
            }

            DOMSource source = createXML(doc,resultSet,rootElement,getBOOKINGKEYS(),"multas");
            saveXML(filename, source);

        } catch (ParserConfigurationException | TransformerException | SQLException pce) {
            LOGGER.info(pce.toString());
        }
    }

    private void saveXML(String filename, DOMSource source) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
        StreamResult result = new StreamResult(new File(getFILEPATH()+filename));
        transformer.transform(source, result);
    }
}
