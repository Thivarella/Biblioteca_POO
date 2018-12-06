package boboteca.comandos;

import java.io.IOException;

public interface Commands {
	
	Object execute(Integer choice) throws IOException;

}
