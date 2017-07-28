package comandosChatBot;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

import conexion.Web.DatosWeb;
import servidor.Servidor;

public class FechaHora extends ComandoChatBot{
	private String husoHorario;
	
	private int dia=0; 
	private int mes=0;
	private int año=0;
	private int hora=0;
	private int minutos=0;
	private int segundos=0;
	
	@Override
	public void ejecutar() {
		
		try {
			JSONObject j = DatosWeb.getJSONFromURL("https://script.google.com/macros/s/AKfycbyd5AcbAnWi2Yn0xhFRbyzS4qMq1VucMVgVvhul5XqS9HkAyJY/exec?tz="  +  husoHorario);

			this.dia = j.getInt("day"); 
			this.mes=j.getInt("month");
			this.año=j.getInt("year");
			this.hora=j.getInt("hours");
			this.minutos=j.getInt("minutes");
			this.segundos=j.getInt("seconds");
			
			} catch (Exception e) {
				Servidor.getLog().append("No se pudo obtener json la Fecha. DatosWeb->getFechaHora");
			}
	}
}

