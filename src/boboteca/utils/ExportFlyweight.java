package boboteca.utils;

import boboteca.comandos.Commands;
import boboteca.export.*;

import java.io.IOException;
import java.util.Hashtable;

public class ExportFlyweight {
	
	private static ExportFlyweight instance;
	
	Hashtable<Integer, Commands> comandos =
			new Hashtable<>();
	
	private ExportFlyweight() {
		comandos.put(1, new ExportUser());
		comandos.put(2, new ExportBook());
		comandos.put(3, new ExportLoan());
		comandos.put(4, new ExportTax());
		comandos.put(5, new ExportBooking());
	}

	public Object getComando(Integer codigo, Integer choice) throws IOException {
		return comandos.get(codigo).execute(choice);
	}
	
	public static synchronized ExportFlyweight getInstance() {
		if(instance == null)
			instance = new ExportFlyweight();
		return instance;
	}
}










