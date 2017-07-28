package comandosChatBot;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import conexion.Web.DatosWeb;
import servidor.Servidor;

public class Ciudad extends ComandoChatBot{
	private String ciudad="";
	private String timeZone="";
	
	@Override
	public void ejecutar() {
		JSONObject j;
		try {
			j = DatosWeb.getJSONFromURL("http://ip-api.com/json");
			
			this.ciudad = j.getString("country");
		    this.timeZone=j.getString("timezone");
			if (ciudad.equals("N/A") ||  this.timeZone.equals("N/A") ){
				Servidor.getLog().append("No se pudo encontrar la ciudad indicada json la la ciudad Json.");
			}
			
			} catch (Exception e) {
				Servidor.getLog().append("No se pudo obtener json la la ciudad Json.");
			}
	}
}
