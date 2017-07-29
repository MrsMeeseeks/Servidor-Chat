package mockComandosChatBot;


import org.json.JSONObject;

import Tests.MockServidor;
import comandosChatBot.ComandoChatBot;
import paqueteEnvios.PaqueteMensaje;
import servidor.Servidor;

public class MockFecha extends ComandoChatBot {
	private String msjFinal;
	private String [] fecha;
	@Override
	public void ejecutar() {
		String husoHorario = getHusoHorario();
		if (husoHorario!=null) {
			JSONObject j = getJSONFromURL("http://api.timezonedb.com/v2/get-time-zone?key=J74Q9QN1RN7M&format=json&by=zone&zone=" + husoHorario);
			if (j!=null) {
				String result = j.getString("formatted");
				String[] fechaYhora = result.split(" ", 2);
				fecha = fechaYhora[0].split("-", 3);
				msjFinal = "La fecha es: " + fecha[2] + "-" + fecha[1] + "-" + fecha[0];
			} else {
				msjFinal = "Error al tratar de conseguir los datos de la fecha.";
			}
		} else {
			msjFinal = "Error al tratar de conseguir la zona horaria.";
		}

		PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);

		String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsjChat() + "\n";
		MockServidor.getSalitas().get(paqueteMensaje.getNombreSala()).agregarMsj(msjAgregar);


	}

	private String getHusoHorario() {
		JSONObject j;
		j = getJSONFromURL("http://ip-api.com/json");
		if (j != null) {
			String r = j.getString("timezone");
			if (!r.equals("N/A"))
				return r;
		} 
		Servidor.getLog().append("No se pudo encontrar los datos del huso horario.");
		return null;
	}
	
}
