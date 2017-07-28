package conexion.Web;

import java.net.URLEncoder;
import org.json.*;

import servidor.Servidor;

public class Clima {
	private double temp;
	private double temp_max;
	private double temp_min;
	private String descripcion;
	private int presion;
	private double visibilidad;
	private double vel_viento;
	
	public Clima(String ciudad) {
		try {
			JSONObject obj = DatosWeb.getJSONFromURL("http://api.openweathermap.org/data/2.5/weather?q="+URLEncoder.encode(ciudad, "UTF-8")+"&appid=612a51535e726e4c14f5361e57802030&lang=es&units=metric");
			this.descripcion = obj.getJSONArray("weather").getJSONObject(0).getString("description");
			this.temp = obj.getJSONObject("main").getDouble("temp");
			this.temp_min = obj.getJSONObject("main").getDouble("temp_min");
			this.temp_max = obj.getJSONObject("main").getDouble("temp_max");
			this.presion = obj.getJSONObject("main").getInt("pressure");
			this.visibilidad = obj.getDouble("visibility")/10000;
			this.vel_viento = obj.getJSONObject("wind").getDouble("speed");
		} catch (Exception e) {
			Servidor.getLog().append("Error al obtener el JSON del clima POLIMORFISMO");
		}
	}

	public double getTemp() {
		return temp;
	}

	public double getTemp_max() {
		return temp_max;
	}

	public double getTemp_min() {
		return temp_min;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public int getPresion() {
		return presion;
	}

	public double getVisibilidad() {
		return visibilidad;
	}

	public double getVel_viento() {
		return vel_viento;
	}
	
	public String toString() {
		return new String("Temperatura: " + this.temp + " �C. Maxima: " + this.temp_max + " �C. Minima: "+ this.temp_min + " �C. Presion: " + this.presion + " hPa. Visibilidad: " + this.visibilidad + " km/h. Velocidad del viento: " + this.vel_viento + " km/h.");
	}
	
}
