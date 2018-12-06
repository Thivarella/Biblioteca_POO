package boboteca.export;

import boboteca.comandos.Commands;
import boboteca.comandos.ExportToCSV;
import boboteca.comandos.ExportToJSON;
import boboteca.comandos.ExportToXML;

import java.io.IOException;

public class ExportUser implements Commands {
    @Override
    public Object execute(Integer choice) throws IOException {
        switch (choice){
            case 1:
                new ExportToXML().exportUserToXML();
                break;
            case 2:
                new ExportToJSON().exportUserToJSON();
                break;
            case 3:
                new ExportToCSV().exportUserCsv();
                break;
        }
        return null;
    }
}
