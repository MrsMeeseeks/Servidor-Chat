package comandosChatBot;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONObject;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMensaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Clima extends ComandoChatBot {
	private double temp;
	private double temp_max;
	private double temp_min;
	private String descripcion;
	private int presion;
	private double visibilidad;
	private double vel_viento;
	private String msjFinal;

	@Override
	public void ejecutar() {
		String ciudad = Servidor.getCiudad();
		if (ciudad!=null) {
			try {
				JSONObject obj = getJSONFromURL("http://api.openweathermap.org/data/2.5/weather?q="
						+ URLEncoder.encode(ciudad, "UTF-8")
						+ "&appid=612a51535e726e4c14f5361e57802030&lang=es&units=metric");
				if (obj!=null) {
					descripcion = obj.getJSONArray("weather").getJSONObject(0).getString("description");
					temp = obj.getJSONObject("main").getDouble("temp");
					temp_min = obj.getJSONObject("main").getDouble("temp_min");
					temp_max = obj.getJSONObject("main").getDouble("temp_max");
					presion = obj.getJSONObject("main").getInt("pressure");
					visibilidad = obj.getDouble("visibility") / 10000;
					vel_viento = obj.getJSONObject("wind").getDouble("speed");
					msjFinal = toString();
				} else {
					msjFinal = "Error al tratar de conseguir los datos del clima de la ciudad.";
				}
			} catch (IOException e) {
				Servidor.getLog().append("No se pudieron encontrar los datos del clima." + System.lineSeparator());
				e.printStackTrace();
			}

		} else {
			msjFinal = "Error al tratar de conseguir la ciudad del servidor.";
		}

		try {
			PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);
			paqueteMensaje.setComando(Comando.CHATSALA);
			if (!nombreSala.equals("Ventana Principal")) {
				for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
					if (Servidor.getSalas().get(paqueteMensaje.getNombreSala()).getUsuariosConectados()
							.contains(conectado.getPaqueteUsuario().getUsername())) {
						conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
					}
				}
					String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsjChat() + "\n";
					Servidor.getSalas().get(paqueteMensaje.getNombreSala()).agregarMsj(msjAgregar);
					Servidor.getConector().guardarChatSala(paqueteMensaje);
				} else {
					paqueteMensaje.setComando(Comando.CHATALL);
					for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
						conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
					}
				}
		} catch (IOException e) {
			Servidor.getLog().append("Error al tratar de responder la solicitud de clima."+ System.lineSeparator());
			e.printStackTrace();
		}


	}

	public String toString() {
		return new String("Pronostico: "+ this.descripcion +" "+ "Temperatura: " + this.temp + " �C. Maxima: " + this.temp_max + " �C. Minima: "+ this.temp_min + " �C. Presion: " + this.presion + " hPa. Visibilidad: " + this.visibilidad + " km/h. Velocidad del viento: " + this.vel_viento + " km/h.");
	}


}
