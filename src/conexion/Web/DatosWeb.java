package conexion.Web;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import servidor.Servidor;

public final class DatosWeb {

	public DatosWeb(){

	}

	/**
	 * Lee la url jsonObject.
	 * @dir direccion url del objeto json.
	 * @return devuelve un objeto Json.
	 * @IOException La clase Scanner puede lanzar excepciones .
	 */
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

	/**
	 * se usa yahoo finance xchange para obtener un json
	 * de la base de datos que contiene el promedio de precios
	 *  de intercambio de la moneda elegida.
	 * @deMoneda moneda a la que se quiere intercambiar.
	 * @aMoneda moneda de la que se quiere intercambiar.
	 * @return devuelve un objeto Json.
	 */
	public static Double getPrecio(final String deMoneda, final String aMoneda) {
		JSONObject j;
		try {
			j = DatosWeb.getJSONFromURL("https://query.yahooapis.com/v1/public/yql?q=select%20Name%2C%20Rate%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22" + GestorMoneda.getInstance("src/data/codmoneda.json").getCodigo(deMoneda)+GestorMoneda.getInstance("src/data/codmoneda.json").getCodigo(aMoneda) + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
			String r = j.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
		    if (r.equals("N/A")) {
		    	return null;
		    } else {
		    	return Double.parseDouble(r);
		    }
		} catch (IOException e) {
			Servidor.getLog().append("No se pudo encontrar la direccion web");
			return null;
		}
	}

	/**
	 * Obtiene el valor de una moneda elegida en Pesos ARG.
	 * @moneda Moneda de la que se quiere tener el valor.
	 * @return valor de la moneda.
	 */
	public static Double getPrecio(final String moneda) {
		return DatosWeb.getPrecio(moneda, "ARS");
	}

	/**
	 * Obtiene el valor de una moneda a otra elegida
	 * @cantidad Cantidad de la moneda a convertir.
	 * @deMoneda Moneda de la que se quire convertir.
	 * @aMonedaMoneda Moneda a la que se quiere convertir.
	 * @return valor de la moneda en la cantidad dada.
	 */
	public static Double convertir(final double cantidad,final String deMoneda,final String aMoneda) {
		Double r = getPrecio(deMoneda, aMoneda);
		if (r != null) {
			return (r * cantidad);
		} else {
			return null;
		}
	}
	/**
	 * convierte de una moneda a pesos ARG
	 * @param cantidad Cantidad de la moneda a convertir.
	 * @param deMoneda Moneda de la que se quire convertir.
	 * @return valor en pesos ARG de la moneda en la cantidad dada.
	 */
	public static Double convertir(final double cantidad,final String deMoneda) {
		return DatosWeb.convertir(cantidad, deMoneda, "ARS");
	}

	/**
	 * Obtiene el clima de la ciudad elegida en
	 * formato "CONTINENTE/PAIS/CIUDAD" o
	 * "PAIS/CIUDAD".
	 *  ciudad asdadssadas.
	 * @param ciudad pais y ciudad de la que se quiere obtener el clima.
	 * @return objeto clima con atributos del mismo.
	 */
	public static Clima getClima(final String ciudad) {
		return new Clima(ciudad);
	}

	/**
	 * Obtiene fecha y hora recibiendo el huso horario
	 * utilizando gson de google.
	 * @param husoHorario huso horario del pais/ciudad.
	 * @return Objeto Date con el contenido del mismo.
	 */
	public static Date getFechaHora(final String husoHorario) {
		try {
			JSONObject j = DatosWeb.getJSONFromURL("https://script.google.com/macros/s/AKfycbyd5AcbAnWi2Yn0xhFRbyzS4qMq1VucMVgVvhul5XqS9HkAyJY/exec?tz="  +  husoHorario);
			return new SimpleDateFormat("dd-mm-yyyy HH:mm:ss").parse(j.getInt("day") + "-" + j.getInt("month") + "-" + j.getInt("year") + " " + j.getInt("hours") + ":" + j.getInt("minutes") + ":" + j.getInt("seconds"));
		} catch (Exception e) {
			Servidor.getLog().append("No se pudo obtener la Fecha. DatosWeb->getFechaHora");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Obtiene la localizacion
	 * utilizando gson de google.
	 * @return retorna un String de la localizacion.
	 */
	public static String getLocalizacion() throws JSONException {
	    JSONObject j;
		try {
			j = DatosWeb.getJSONFromURL("http://ip-api.com/json");
		    String r = /*j.getString("city") + "\n " + j.getString("country") + "\n" +  */j.getString("timezone");
		    if (r.equals("N/A")) return null;
		    else return r;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("No se pudo encontrar la direccion web");
			return null;
		}
	}

	/**
	 * consulta contra la api de wolfram alpha, contesta
	 * preguntas muy variadas.
	 * @param consulta consulta que recibe wolfram
	 * @return respuesta de Wolfram.
	 */
	public static String consultarWolfram(final String consulta) {
		JSONObject j;
		try {
			j = DatosWeb.getJSONFromURL("http://api.wolframalpha.com/v2/query?appid=AEQQL7-383JE34XYH&input=\"" + URLEncoder.encode(consulta, "UTF-8") + "\"&includepodid=Result&format=plaintext&output=json&units=metric");
			return String.valueOf(j.getJSONObject("queryresult").getJSONArray("pods").getJSONObject(0).getJSONArray("subpods").getJSONObject(0).get("plaintext"));
		} catch (Exception e) {
			System.err.println("No se pudo encontrar los datos");
			return null;
		}
	}
}
