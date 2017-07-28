package comandosChatBot;

import java.io.IOException;

import org.json.JSONObject;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMensaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class FechaHora extends ComandoChatBot{	
	private String[] fecha;
	private String hora;
	private String msjFinal;
	@Override
	public void ejecutar() {
			String husoHorario = getHusoHorario();
			if (husoHorario!=null) {
				try {
					JSONObject j = getJSONFromURL("http://api.timezonedb.com/v2/get-time-zone?key=J74Q9QN1RN7M&format=json&by=zone&zone=" + husoHorario);
					String result = j.getString("formatted");
					String[] fechaYhora = result.split(" ", 2);
					hora = fechaYhora[1];
					fecha = fechaYhora[0].split("-", 3);
					msjFinal = "La fecha es: " + fecha[2] +"-"+fecha[1]+"-"+fecha[0]+"\n"+"La hora es: "+hora;
					} catch (Exception e) {
					Servidor.getLog().append("No se pudo encontrar los datos de la fecha y hora." + System.lineSeparator());
				}
			} else {
				msjFinal = "Error al tratar de conseguir la zona horaria.";
			}
			try {
				PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);
				paqueteMensaje.setComando(Comando.CHATSALA);
				for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
					if (Servidor.getSalas().get(paqueteMensaje.getNombreSala()).getUsuariosConectados()
							.contains(conectado.getPaqueteUsuario().getUsername())) {
						conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
					}
				}
				String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsj() + "\n";
				Servidor.getSalas().get(paqueteMensaje.getNombreSala()).agregarMsj(msjAgregar);
				Servidor.getConector().guardarChatSala(paqueteMensaje);
			} catch (IOException e) {
				Servidor.getLog().append("Error al tratar de responder la solicitud de fecha y hora." + System.lineSeparator());
				e.printStackTrace();
			}			
}
	
	private String getHusoHorario() {
		 JSONObject j;
			try {
				j = getJSONFromURL("http://ip-api.com/json");
			    String r = j.getString("timezone");
			    if (r.equals("N/A")) return null;
			    else return r;
			} catch (IOException e) {
				Servidor.getLog().append("No se pudo encontrar los datos del huso horario.");
				return null;
			}
	}
}

