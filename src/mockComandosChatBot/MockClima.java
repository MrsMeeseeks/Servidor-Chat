package mockComandosChatBot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import Tests.MockServidor;
import comandosChatBot.ComandoChatBot;
import paqueteEnvios.PaqueteMensaje;


public class MockClima extends ComandoChatBot {
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
		String ciudad = MockServidor.getCity();
		String nombreSala = "Sala Test";
		if (ciudad!=null) {
			try {
				JSONObject obj = getJSONFromURL("http://api.openweathermap.org/data/2.5/weather?q="
						+ URLEncoder.encode(ciudad, "UTF-8")
						+ "&appid=612a51535e726e4c14f5361e57802030&lang=es&units=metric");

				if (obj!=null) {
					msjFinal = "Pronostico: " + obj.getJSONArray("weather").getJSONObject(0).getString("description")
							+ " " + "Temperatura: " + obj.getJSONObject("main").getDouble("temp") + " �C. Maxima: "
							+ obj.getJSONObject("main").getDouble("temp_min") + " �C. Minima: "
							+ obj.getJSONObject("main").getDouble("temp_max") + " �C. Presion: "
							+ obj.getJSONObject("main").getInt("pressure") + " hPa. Visibilidad: "
							+ obj.getDouble("visibility") / 10000 + " km/h. Velocidad del viento: "
							+ obj.getJSONObject("wind").getDouble("speed") + " km/h.";
				} else {
					msjFinal = "Error al tratar de conseguir los datos del clima de la ciudad.";
				}
			} catch (UnsupportedEncodingException e) {
				msjFinal = "Error al tratar de conseguir los datos del clima de la ciudad.";
				e.printStackTrace();
			}
		} else {
			msjFinal = "Error al tratar de conseguir la ciudad del servidor.";
		}

		PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);

		String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsjChat() + "\n";
		MockServidor.getSalitas().get(nombreSala).agregarMsj(msjAgregar);
	}

	public String toString() {
		return new String("Pronostico: "+ this.descripcion +" "+ "Temperatura: " + this.temp + " �C. Maxima: " + this.temp_max + " �C. Minima: "+ this.temp_min + " �C. Presion: " + this.presion + " hPa. Visibilidad: " + this.visibilidad + " km/h. Velocidad del viento: " + this.vel_viento + " km/h.");
	}


}
