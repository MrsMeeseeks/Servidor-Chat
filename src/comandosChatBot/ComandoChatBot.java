package comandosChatBot;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

import paqueteEnvios.Comando;

public abstract class ComandoChatBot extends Comando {
	protected String nombreSala;

	public void setNombreSala(String nombreSala) {
		this.nombreSala = nombreSala;
	}
	
	public static JSONObject getJSONFromURL(final String dir) 
			throws IOException {
	    URL url = null;
		url = new URL(dir);
	    Scanner scan = null;
		scan = new Scanner(url.openStream(), "UTF-8");
	    String str = new String();
	    while (scan.hasNext()) {
	        str += scan.nextLine();
	    }
	    scan.close();
	    return new JSONObject(str);
	}

}
