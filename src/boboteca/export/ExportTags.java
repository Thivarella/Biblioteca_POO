package boboteca.export;

import java.util.Arrays;
import java.util.List;

public class ExportTags {
    private ExportTags(){}
    private static final List<String> BOOKKEYS = Arrays.asList("id","nome","autor","categoria","ano_de_publicação","prioridade","disponibilidade");
    private static final String FILEPATH = "/home/thiago/Documentos/Relatórios_Boboteca/";
    private static final List<String> LOANKEYS = Arrays.asList("id","código_do_livro","nome_do_livro","código_do_usuário","nome_do_usuário","data_de_empréstimo","data_de_devolução","devolvido","devolvido_em");
    private static final String PATHCSV = "/home/thiago/Área de Trabalho/hsqldb-2.4.1/hsqldb/data/";
    private static final List<String> TAXKEYS = Arrays.asList("id","código_do_livro","nome_do_livro","código_do_usuário","nome_do_usuário","código_do_empréstimo","descrição","valor","situação");
    private static final List<String> BOOKINGKEYS = Arrays.asList("id","código_do_livro","nome_do_livro","código_do_usuário","nome_do_usuário","data_da-reserva","status");
    private static final List<String> USERKEYS = Arrays.asList("id","nome","sexo","categoria","telefone","usuário_normal","logradouro","número","complemento","bairro","cep","cidade","estado");

    public static List<String> getBOOKKEYS() {
        return BOOKKEYS;
    }

    public static String getFILEPATH() {
        return FILEPATH;
    }

    public static List<String> getLOANKEYS() {
        return LOANKEYS;
    }

    public static String getPATHCSV() {
        return PATHCSV;
    }

    public static List<String> getTAXKEYS() {
        return TAXKEYS;
    }

    public static List<String> getUSERKEYS() {
        return USERKEYS;
    }

    public static List<String> getBOOKINGKEYS() {
        return BOOKINGKEYS;
    }
}
