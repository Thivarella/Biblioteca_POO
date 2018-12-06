package boboteca.export;

import boboteca.comandos.Commands;
import boboteca.comandos.ExportToCSV;
import boboteca.comandos.ExportToJSON;
import boboteca.comandos.ExportToXML;

import java.io.IOException;

public class ExportBook implements Commands {
    @Override
    public Object execute(Integer choice) throws IOException {
        switch (choice){
            case 1:
                new ExportToXML().exportBooksToXML();
                break;
            case 2:
                new ExportToJSON().exportBooksToJSON();
                break;
            case 3:
                new ExportToCSV().exportBookCsv();
                break;
            default:
                break;
        }
        return null;
    }
}
